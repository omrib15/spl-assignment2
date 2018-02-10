package bgu.spl.app;

import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.impl.MessageBusImpl;
import bgu.spl.mics.impl.MyLogger;

public class WebClientService extends MicroService {
	private LinkedList<PurchaseSchedule> purchaseList;
	private Set<String> wishList;
	private int currentTick;
	private Logger logger = MyLogger.getInstance().logger;
	
	public WebClientService(String name, LinkedList<PurchaseSchedule> purchaseList, Set<String> wishList) 
	{
		super(name);
		this.purchaseList=purchaseList;
		this.wishList=wishList;
		this.currentTick=1;
		
	}
	@Override
	protected void initialize() {
		subscribeBroadcast(TerminationBroadcast.class, (x)-> {logger.info(getName()+" terminated");});
		
		subscribeBroadcast(TickBroadcast.class, new newTickReceived());
		
		subscribeBroadcast(NewDiscountBroadcast.class, new newDiscountReceived());
		logger.info(getName()+ " has initialized");
	}
	
	// we keep a reference to "this" so the Callback newTickReceived would have access to this client
	// who invoked it
	private MicroService myself = this;
	
	
	/**
	 * the Callback to be invoked when handling a newTickBroadcast message
	 * @author razno
	 *
	 */
	public class newTickReceived implements Callback<TickBroadcast>
	{
		public void call(TickBroadcast t)
		{
			//update the tick
			currentTick = t.getTick();
			
			//check if the are any shoes that we should attempt to buy in this new tick, if so - send
			//a PurchaseRequest for them
			for(PurchaseSchedule purchase : purchaseList)
			{
				
				if(purchase.getTick() == currentTick)
				{	
					//ordering the shoe since the schedule says it should be done at the current tick
					
					PurchaseOrderRequest order = new PurchaseOrderRequest(purchase.getShoeType(), false, getName(), currentTick);
					logger.info(getName()+ " sent a purchase request for "+purchase.getShoeType());
					sendRequest(order, (r)->
					{
						//the Callback to be invoked when receiving a complete message of this request
						
						if(r==null)
							logger.info(getName()+ " purchase request for"+order.getShoeType()+" could not complete");
						if(r!=null){
							logger.info(getName()+" had received a "+purchase.getShoeType() );
							
							//if the shoe received was on the wishlist, remove it from wishlist
							if(wishList.contains(r.getShoeType())){
								wishList.remove(r.getShoeType());
								purchaseList.remove(purchase);
							}
						}
					});
				}
			
			}
			
			//if the client has no more shoes on his wishlist or purchase schedule, he unregisters 
			//and terminates using the finished() method
			if( purchaseList.isEmpty() && wishList.isEmpty()){
				logger.info(getName()+" has no more shoes to buy");
				myself.finished();
			}
		}
		
	}
	
	/**
	 * a Callback to be invoked when handling a newDiscountReceived message
	 * @author razno
	 *
	 */
	public class newDiscountReceived implements Callback<NewDiscountBroadcast>
	{
		public void call(NewDiscountBroadcast discount)
		{
			//if the new discount is on a shoe that the client has on his wishlist, send a 
			//purchase request for it
			if(wishList.contains(discount.getShoeType())){
				logger.info(getName()+" sent a purchase request for "+discount.getShoeType()+" from his wishlist");
				sendRequest(new PurchaseOrderRequest(discount.getShoeType(), true, getName(), currentTick), r->
						{
							if(r==null)
							logger.info(getName()+ " purchase request for"+discount.getShoeType()+" could not complete");
							if(r!=null){
								logger.info(getName()+" had received a "+discount.getShoeType()+" from his wishlist" );
								wishList.remove(discount.getShoeType());
							}
						});
			}
			
			//if as a result of handling the newDiscountBroadcast the client has
			//no more shoes to buy, he unregisters and terminate using the finished() method
			if( purchaseList.isEmpty() && wishList.isEmpty()){
				logger.info(getName()+" has no more shoes to buy");
				myself.finished();
			}
		}
	}

}
