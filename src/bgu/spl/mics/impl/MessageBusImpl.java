package bgu.spl.mics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import bgu.spl.app.ManufacturingOrderRequest;
import bgu.spl.app.PurchaseOrderRequest;
import bgu.spl.app.TerminationBroadcast;
import bgu.spl.mics.*;
public class MessageBusImpl implements MessageBus{
	
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcast_Subscribers;
	private ConcurrentHashMap<Class<? extends Request>, RoundRobin> request_Subscribers;
	private ConcurrentHashMap<MicroService, MessageQueue> message_Queues;
	private ConcurrentHashMap<Request, MicroService> Requests_And_Senders;
	private Logger logger = MyLogger.getInstance().logger;
	private int unregister_Counter = 0;
	
	private static class BusHolder
	{
		private static MessageBusImpl Bus = new MessageBusImpl();
	}
	
	private MessageBusImpl() {
		message_Queues = new ConcurrentHashMap<MicroService, MessageQueue>();
		broadcast_Subscribers = new ConcurrentHashMap<Class<? extends Broadcast>,LinkedBlockingQueue<MicroService>>();
		request_Subscribers = new ConcurrentHashMap<Class<? extends Request>,RoundRobin>();
		Requests_And_Senders = new ConcurrentHashMap<Request, MicroService>();
		
	}
	
	public static MessageBusImpl get_Instance(){
		return BusHolder.Bus;
	}
	
	@Override
	public void subscribeRequest(Class<? extends Request> type, MicroService m) {
		synchronized (this) {
			request_Subscribers.putIfAbsent(type, new RoundRobin());
		} 
		try {
			request_Subscribers.get(type).put(m);
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (this) {
			broadcast_Subscribers.putIfAbsent(type, new LinkedBlockingQueue<MicroService>());
		} 
		broadcast_Subscribers.get(type).add(m);
	}

	@Override
	public <T> void complete(Request<T> r, T result) {
		synchronized (this) {
			(message_Queues.get(Requests_And_Senders.get(r))).put(new RequestCompleted(r, result));
			Requests_And_Senders.remove(r);
		} 
		
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if(!(broadcast_Subscribers.get(b.getClass()).isEmpty())){
			
			for(MicroService m : broadcast_Subscribers.get(b.getClass()))
			{
				if(message_Queues.containsKey(m))
					(message_Queues.get(m)).put(b); 
			}
		}
		

	}

	@Override
	public synchronized boolean sendRequest(Request<?> r, MicroService requester) {
		if (request_Subscribers.containsKey(r.getClass()))
		{
			Requests_And_Senders.putIfAbsent(r, requester);
			if(request_Subscribers.get(r.getClass()).isEmpty()){
				return false;
			}
			else {
				try{
					
						MicroService handler = request_Subscribers.get(r.getClass()).roundRobin();
						if(r.getClass()==PurchaseOrderRequest.class)
							logger.info(handler.getName()+" received a new order from "+((PurchaseOrderRequest) r).getSender());				
						else if (r.getClass()==ManufacturingOrderRequest.class)
							logger.info(handler.getName()+" received a new order from "+((ManufacturingOrderRequest) r).getRequester());
						message_Queues.get(handler).put(r);
						return true;
					
					}
				
				catch (InterruptedException e){
					System.out.println("InterruptedException caught");
					return false;
				}
			}
		}
		else {
			logger.info(r.getClass().getName() +"  is not availble");

			return false;
		}
	}

	@Override
	public  void  register(MicroService m) {
		logger.info("Microservice " + m.getName()+ " has registered");
		message_Queues.putIfAbsent(m, new MessageQueue());
		
	}

	@Override
	public void unregister(MicroService m) {
		logger.info(m.getName()+ " is unregistering");
		if (message_Queues.containsKey(m)){
			message_Queues.remove(m);
			request_Subscribers.remove(m);
			broadcast_Subscribers.remove(m);
		}
			
		Collection<MicroService> c = Requests_And_Senders.values();
		for(MicroService micro : c){
			if(micro == m)
				c.remove(micro);
		}
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!message_Queues.containsKey(m))
			throw new IllegalStateException();
		return message_Queues.get(m).take();
	
	}
	
	

}
