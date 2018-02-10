package bgu.spl.mics.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.app.TerminationBroadcast;
import bgu.spl.mics.Message;

public class MessageQueue {
	private LinkedBlockingQueue<Message> queue;
	
	public MessageQueue()
	{
		queue = new LinkedBlockingQueue<Message>() {
		};
	}
	
	public void put(Message m)
	{
		if(m.getClass() == TerminationBroadcast.class)
		{
			queue.clear();
			try{
				queue.put(m);
			}
			catch (InterruptedException e)
			{}
		} 
		
		else
			try {
				queue.put(m);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
	}
	
	public Message take()
	{
		try {
			return queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
		
}
