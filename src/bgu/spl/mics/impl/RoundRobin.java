package bgu.spl.mics.impl;


import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.MicroService;
public class RoundRobin {

	private LinkedBlockingQueue<MicroService> queue = new LinkedBlockingQueue<MicroService>();
	
	public synchronized MicroService roundRobin() throws InterruptedException {

		MicroService handler =  queue.take();
		handler.setWorked(true);
		queue.put(handler);
		return handler;
		 
	}
	public synchronized MicroService take() {
		return take();
	}
	
	public synchronized void put(MicroService seller) throws InterruptedException {
		boolean check = false;
		Stack<MicroService> stack = new Stack<MicroService>();
		for (MicroService it : queue)
		{
			if (check) break;
			if (!it.hasWorked())
			{
				stack.add(it);
				queue.take();
			}
			else 
				check=true;
		}
		queue.put(seller);
		for (MicroService it : stack)
			queue.put(it);
		stack.clear();
		if (!check)
			for (MicroService it : queue)
				it.setWorked(false);
	}

	public BlockingQueue<MicroService> getQueue() {
		return queue;
	}
	
	public boolean isEmpty() {
	
		if(queue == null){
			return true;
		}
		else{
			return queue.isEmpty();
		}	
	}
	
	public MicroService element()
	{
		return queue.element();
	}
	

}
