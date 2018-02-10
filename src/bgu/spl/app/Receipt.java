package bgu.spl.app;

public class Receipt {
	private String seller;
	private String customer;
	private String shoeType;
	private boolean discount;
	private int issuedTick;
	private int requestTick;
	private int amountSold;
	
	public Receipt( String seller,String customer,String shoeType,
	 boolean discount,int issuedTick,int requestTick,int amountSold)
	{
		this.amountSold =amountSold;
		this.customer = customer;
		this.discount = discount;
		this.issuedTick = issuedTick;
		this.requestTick = requestTick;
		this.seller = seller;
		this.shoeType = shoeType;
	}

	public String getSeller() {
		return seller;
	}

	public String getCustomer() {
		return customer;
	}

	public String getShoeType() {
		return shoeType;
	}

	public boolean isDiscount() {
		return discount;
	}

	public int getIssuedTick() {
		return issuedTick;
	}

	public int getRequestTick() {
		return requestTick;
	}

	public int getAmountSold() {
		return amountSold;
	}
	
	
	
	
}
