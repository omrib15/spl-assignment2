package bgu.spl.app;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.impl.MyLogger;

public class ShoeFactoryService extends MicroService {
	private int currentTick;
	private LinkedBlockingQueue<ManufacturingOrderRequest> manufacture_Orders;
	private Logger logger = MyLogger.getInstance().logger;
	
	public ShoeFactoryService(String name)
	{
		super(name);
		currentTick =1;
		manufacture_Orders = new LinkedBlockingQueue<ManufacturingOrderRequest>();
		
	}
	
	protected void initialize() {
		subscribeBroadcast(TerminationBroadcast.class, (x)-> {logger.info(getName()+" terminated");;});
		
		subscribeBroadcast(TickBroadcast.class, new newTick());
		
		subscribeRequest(ManufacturingOrderRequest.class, (order) -> 
		{
			logger.info(getName()+" received a manufacturing order request from: "+order.getRequester()+ " for: "+order.getAmount()+" "+order.getShoeType());
			
			//add the restock request to the queue that belongs to the shoe type requested
			
			try {
				manufacture_Orders.put(order);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		logger.info(getName() + " has initialized");
	}
	
	public class newTick implements Callback<TickBroadcast>
	{
		public void call(TickBroadcast t)
		{
			currentTick = t.getTick();
			if(!manufacture_Orders.isEmpty()) //checks that there are orders at all
			{	//handle the oldest not completed order (the first in queue right now)
				ManufacturingOrderRequest order =  manufacture_Orders.element(); 
				
				//if the order is not yet done, create another shoe
				if(!order.isDone()){
					logger.info(order.getShoeType()+" is made");
					order.shoeMade();
				}
				else //the order is ready, complete it
				{
					logger.info(getName()+" completed the order of "+order.getAmount()+" "+order.getShoeType());
					Receipt r = new Receipt(getName(), "store", order.getShoeType(), false, currentTick, order.getRequestedTick(), order.getAmount());
					complete(manufacture_Orders.remove(), r);
					if(!manufacture_Orders.isEmpty())
						manufacture_Orders.element().shoeMade();
				}
				
			}
			
			else {}
			
		}
	}
	
	

}
