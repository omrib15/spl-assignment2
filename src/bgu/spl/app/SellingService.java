package bgu.spl.app;

import java.util.logging.Logger;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.impl.MyLogger;

public class SellingService extends MicroService {
	private int currentTick = 1;
	private Logger logger = MyLogger.getInstance().logger;
	private Store store = Store.get_Instance();
	public SellingService(String name)
	{
		super(name);
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminationBroadcast.class, (x)-> {logger.info(getName()+" terminated");;});
		
		subscribeBroadcast(TickBroadcast.class, tickBroad -> {
			
			currentTick = tickBroad.getTick();});
		
		
		subscribeRequest(PurchaseOrderRequest.class, new PurchaseOrederReceived());
		
		logger.info(getName() + " has initialiazed");
	}
	
	/**
	 * this is the Callback to be executed when the selling service receives a {@link PurchaseOrderRequest}
	 * @author razno
	 *
	 */
	
	class PurchaseOrederReceived implements Callback<PurchaseOrderRequest>
	{
		public void call(PurchaseOrderRequest req)
		{
			//try to take the requested shoe from the store, and complete the request according to
			//the outcome of the "take" method
			
			BuyResult takeResult = store.take(req.getShoeType(), req.isOnlyDiscount());
			
			if(takeResult == BuyResult.REGULAR_PRICE)
			{
				Receipt receipt = new Receipt(getName(), req.getSender(), req.getShoeType(), 
						false,currentTick, req.getReqTick(), 1);
				Store.get_Instance().file(receipt);
				complete(req, receipt);
			}
			
			if(takeResult == BuyResult.DISCOUNTED_PRICE)
			{
				Receipt receipt = new Receipt(getName(), req.getSender(), req.getShoeType(), 
						true,currentTick, req.getReqTick(), 1);
				Store.get_Instance().file(receipt);
				
				complete(req, receipt);
			}
			
			if(takeResult == BuyResult.NOT_ON_DISCOUNT)
			{
				complete(req,null);
			}
			
			else if(takeResult == BuyResult.NOT_IN_STOCK)
			{	//if the requested shoe was not in stock, send a RestockRequest for that shoe
				logger.info(req.getShoeType()+" is not in stock,"+getName() +" is sending restock request");
				sendRequest(new RestockRequest(req.getShoeType()), isTrue->
				{
					//this is the Callback to be executed when receiving the message that the restock
					//is completed
					
					if(!isTrue)
					{
						//if the restock request could not be completed, complete the purchase request
						//with null as the result
						complete(req, null);
					}
					
					else
					{
						logger.info(req.getSender()+" received "+req.getShoeType());
						Receipt receipt = new Receipt(getName(), req.getSender(), req.getShoeType(),
								req.isOnlyDiscount(), currentTick, req.getReqTick(), 1);
						Store.get_Instance().file(receipt);
						complete(req,receipt);
					}
					
				});
			}
			
		}
		}
		
	}

