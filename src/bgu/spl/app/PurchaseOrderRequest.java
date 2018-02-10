package bgu.spl.app;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;

public class PurchaseOrderRequest implements Request<Receipt>  {
	private String shoeType;
	private boolean onlyDiscount;
	private String sender;
	private int reqTick;
	
	
	public PurchaseOrderRequest(String shoeType, boolean onlyDiscount, String sender,int issuedTick) {
		this.sender = sender;
		this.shoeType = shoeType;
		this.onlyDiscount = onlyDiscount;
	
		this.reqTick = issuedTick;
	}

	
	
	public String getSender(){
		return sender;
	}
	
	public int getReqTick() {
		return reqTick;
	}

	public String getShoeType() {
		return shoeType;
	}

	public boolean isOnlyDiscount() {
		return onlyDiscount;
	}

	
	

}
