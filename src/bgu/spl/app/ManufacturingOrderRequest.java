package bgu.spl.app;

import java.util.logging.Logger;

import bgu.spl.mics.Request;

public class ManufacturingOrderRequest implements Request<Receipt>{
	private String shoeType;
	private int amount;
	private String requester;
	private int requestedTick;
	private int shoesMadeSoFar;
	private boolean isDone;
	
	
	public ManufacturingOrderRequest(String shoeType, int amount,String requester,int requestedTick) {
		super();
		this.shoeType = shoeType;
		this.amount = amount;
		this.requester =requester;
		this.requestedTick =requestedTick;
		shoesMadeSoFar=0;
		isDone = false;
	}
	
	public String getRequester() {
		return requester;
	}

	public int getRequestedTick() {
		return requestedTick;
	}

	public int getShoesMadeSoFar() {
		return shoesMadeSoFar;
	}

	public String getShoeType() {
		return shoeType;
	}
	public int getAmount() {
		return amount;
	}
	
	public void shoeMade()
	{
		shoesMadeSoFar++;
		if(amount == shoesMadeSoFar)
			isDone = true;
	}

	public boolean isDone() {
		return isDone;
	}
	

}
