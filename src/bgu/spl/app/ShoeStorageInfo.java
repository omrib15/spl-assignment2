package bgu.spl.app;

public class ShoeStorageInfo {
	private String shoeType;
	private int amountOnStorage;
	private int discountedAmount;
	
	public ShoeStorageInfo(String shoeType,int amountOnStorage)
	{
		this.shoeType = shoeType;
		this.amountOnStorage = amountOnStorage;
		discountedAmount = 0;
	}
	
	protected String getShoeType() {
		return shoeType;
	}

	protected int getAmountOnStorage() {
		return amountOnStorage;
	}

	protected int getDiscountedAmount() {
		return discountedAmount;
	}

	protected void setDiscountToAmount(){
		if (discountedAmount>amountOnStorage)
			discountedAmount=amountOnStorage;
	}
	
	protected void increase_Amount_By(int increased_By)
	{
		amountOnStorage = amountOnStorage + increased_By;
	}
	
	protected void decrease_Amount_By(int decreased_By)
	{
			amountOnStorage = amountOnStorage - decreased_By;
			if(amountOnStorage < 0)
				amountOnStorage =0;
			
	}
	
	
	protected void increase_Discounted_Amount_By(int increaces_By)
	{
		discountedAmount = discountedAmount + increaces_By;
		if(discountedAmount>amountOnStorage)
			discountedAmount = amountOnStorage;
	}
	
	protected void decrease_Discounted_Amount_By(int decreaces_By)
	{
		discountedAmount = discountedAmount - decreaces_By;
	}
}
