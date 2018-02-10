package bgu.spl.app;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.RequestCompleted;
import bgu.spl.mics.impl.MessageBusImpl;
import bgu.spl.mics.impl.MyLogger;

public class ManagementService extends MicroService {
	
	private LinkedList<DiscountSchedule> discounts;
	private ConcurrentHashMap<String,LinkedBlockingQueue<RestockRequest>>  shoe_And_Restocks;
	private ConcurrentHashMap<String, Integer> shoe_And_Amount_Ordered;
	private int currentTick; 
	private Store store = Store.get_Instance();
	private Logger logger = MyLogger.getInstance().logger;
	
	public ManagementService( LinkedList<DiscountSchedule> discounts) {
		super("manager");
		this.discounts = discounts; 
		this.currentTick = 1;
		shoe_And_Restocks = new ConcurrentHashMap<String, LinkedBlockingQueue<RestockRequest>>();
		shoe_And_Amount_Ordered = new ConcurrentHashMap<String, Integer>();
	}
	
	/**
	 * checks how many {@code RestockRequest} are waiting for {@code shoe}
	 * @param shoe the shoe type  to check its queue
	 * @return int the number of restock requests waiting for the shoe
	 */
	private int How_Many_Restocks(String shoe)
	{
		if (!shoe_And_Restocks.containsKey(shoe)) return 0;
		else return shoe_And_Restocks.get(shoe).size();
	}
	
	@Override
	protected void initialize() {
		
		subscribeBroadcast(TerminationBroadcast.class, (x)-> {logger.info(getName()+" terminated");});
		
		subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> 
		{	
			currentTick = tickBroadcast.getTick();
			//check if there is a discount that should be sent at the new tick
			for(DiscountSchedule discount : discounts)
			{   //if there is, send the discount broadcast
				if(discount.getTick() == currentTick){
					logger.info(getName()+" sent a new discount broadcast for shoe "+discount.getShoeType()+", amount: "+discount.getAmount());
					store.addDiscount(discount.getShoeType(), discount.getAmount());
					sendBroadcast(new NewDiscountBroadcast(discount.getShoeType(), discount.getAmount()));
				}
			}
				
			
		});
		
		subscribeRequest(RestockRequest.class, new RestockReceived());
		
		logger.info(getName() + "is initializing");
	}
	
	/**
	 * this is the callback to be executed when receiving a RestockRequest to handle
	 * @author razno
	 *
	 */
	public class RestockReceived implements Callback<RestockRequest>
	{
		public void call(RestockRequest req)
		{
			
			//are there any prior orders of this shoe?
			if(shoe_And_Amount_Ordered.containsKey(req.getShoeType()))
			{
				//were enough shoes of this type ordered for the requesting seller?
				if(shoe_And_Amount_Ordered.get(req.getShoeType()) >How_Many_Restocks(req.getShoeType()))
					shoe_And_Restocks.get(req.getShoeType()).add(req);
				//need to order more if not
				else 
				{
					shoe_And_Restocks.get(req.getShoeType()).add(req);
					logger.info(getName()+" sent ManufatureRequest for "+((currentTick%5)+1)+" "+req.getShoeType()+" shoes");
					sendRequest(new ManufacturingOrderRequest(req.getShoeType(), (currentTick%5)+1, getName(), currentTick), new ManufactureFinished());
					Integer AmountOnOrder = shoe_And_Amount_Ordered.get(req.getShoeType());
					AmountOnOrder = AmountOnOrder + (currentTick%5)+1;
				}
			}
			//there are'nt prior orders of this shoe, meaning this is the first time
			else
			{
				shoe_And_Restocks.put(req.getShoeType(), new LinkedBlockingQueue<RestockRequest>());
				try {
					shoe_And_Restocks.get(req.getShoeType()).put(req);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				shoe_And_Amount_Ordered.put(req.getShoeType(), (currentTick%5)+1);
				logger.info(getName()+" sent ManufatureRequest for "+((currentTick%5)+1)+" "+req.getShoeType()+" shoes");
				sendRequest(new ManufacturingOrderRequest(req.getShoeType(), (currentTick%5)+1, getName(), currentTick), new ManufactureFinished());	
			}
			
			
		}
		
	}
	
	/**
	 * this is the callback to be executed when the {@link ManufacturingOrderRequest} is completed
	 * @author razno
	 *
	 */
	
	public class ManufactureFinished implements Callback<Receipt>
	{
		@Override
		public void call(Receipt r) {
			int manufacturedAmount = r.getAmountSold();
			
			/*adding the shoes to the store, if there are more restock requesters than this manufactured amount then we know
			for sure that each shoe manufactured via this ManufacturingRequest is reserved for a seller, therefore
			we add "0" shoes to the stock*/
			logger.info(getName()+" received Manufacture order is completed, shoe: "+r.getShoeType()+" amount: "+r.getAmountSold());
			store.add(r.getShoeType(), Math.max(manufacturedAmount-How_Many_Restocks(r.getShoeType()), 0) );
			
			store.file(r);
			//give shoes to the requesters until there are no more shoe or no more requesters
			while(manufacturedAmount > 0 && How_Many_Restocks(r.getShoeType()) > 0)
			{
				manufacturedAmount = manufacturedAmount-1;
				complete(shoe_And_Restocks.get(r.getShoeType()).remove(),true);
			}
			
			
		}

	}
	
	public LinkedList<DiscountSchedule> getDiscounts() {
		return discounts;
	}

	public ConcurrentHashMap<String, LinkedBlockingQueue<RestockRequest>> getShoe_And_Restocks() {
		return shoe_And_Restocks;
	}

	public ConcurrentHashMap<String, Integer> getShoe_And_Amount_Ordered() {
		return shoe_And_Amount_Ordered;
	}

	public int getCurrentTick() {
		return currentTick;
	}
	
}
