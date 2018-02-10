package bgu.spl.app;


import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import bgu.spl.mics.impl.MyLogger;


public class Store {
	private ConcurrentHashMap<String , ShoeStorageInfo> Shoes;
	private LinkedBlockingQueue<Receipt> receipts;
	private Logger logger = MyLogger.getInstance().logger;
	
	private static class StoreHolder
	{
		private static Store store = new Store();
	}

	private Store() {
		Shoes = new ConcurrentHashMap<String, ShoeStorageInfo>();
		receipts = new LinkedBlockingQueue<Receipt>();
	}
	
	/**
	 * 
	 * @return Store the singleton of the {@link Store} class 
	 */
	public static Store get_Instance(){
		return StoreHolder.store;
	}
	
	/**
	 * loads the shoes to the store
	 * @param storage an array of {@link ShoeStorageInfo}, the initial storage
	 */
	public void load (ShoeStorageInfo [] storage)
	{
		if(storage.length>0){
			for(int i =0; i < storage.length; i++)
			{
				if(storage[i] != null)
					Shoes.put(storage[i].getShoeType(), storage[i]);
			}
			logger.info("shoes loaded to the store");
		}

	}

	/**
	 * 
	 * @param shoeType the shoe type to try and take from the store
	 * @param onlyDiscount true if the client will only buy the shoe if its on discount
	 * @return BuyResult the Enum which represents the outctome of the take attempt
	 */
	public synchronized BuyResult take(String shoeType, boolean onlyDiscount)
	{
		
		if(Shoes.containsKey(shoeType)){
			
			/*if the client will only buy the shoe if it's on discount and there are'nt any discounts 
			  available on the shoe in the store*/
			if(Shoes.get(shoeType).getDiscountedAmount() == 0 && onlyDiscount)
				return BuyResult.NOT_ON_DISCOUNT;
			
			//the store ran out of this shoe type
			else if(Shoes.get(shoeType).getAmountOnStorage() == 0 )
				return BuyResult.NOT_IN_STOCK;
			
			//there is a shoe available of this type, and also there is a discount on it
			else if(Shoes.get(shoeType).getAmountOnStorage() > 0 && Shoes.get(shoeType).getDiscountedAmount() > 0)
			{
				Shoes.get(shoeType).decrease_Amount_By(1);
				Shoes.get(shoeType).decrease_Discounted_Amount_By(1);
				return BuyResult.DISCOUNTED_PRICE;
			}
			//there is'nt a discount on the shoe, but the client does'nt care if its on discount,
			//buy at regular price
			else {
				Shoes.get(shoeType).decrease_Amount_By(1);
				return BuyResult.REGULAR_PRICE;
			}
		}
		//if the store does'nt even have the requested shoe type, return NOT_IN_STOCK
		else return BuyResult.NOT_IN_STOCK;
	}

	/**
	 * 
	 * @param shoeType the shoe type to add
	 * @param amount the amount of shoes to add
	 */
	public void add(String shoeType, int amount)
	{
		if(Shoes.containsKey(shoeType))
			Shoes.get(shoeType).increase_Amount_By(amount);
		else Shoes.put(shoeType, new ShoeStorageInfo(shoeType, amount));
	}

	/**
	 * 
	 * @param shoeType the shoe type to add discounts on
	 * @param amount	the amount of shoes of type {@code shoeType} to add discount on
	 */
	public void addDiscount(String shoeType, int amount)
	{
		if(Shoes.containsKey(shoeType)){
			Shoes.get(shoeType).increase_Discounted_Amount_By(amount);
		
			//if there are more discounts than shoes of the type in the store, correct it
			//by making all the shoes of this type in the store on discount
		if(Shoes.get(shoeType).getAmountOnStorage() < Shoes.get(shoeType).getDiscountedAmount())
			Shoes.get(shoeType).setDiscountToAmount();
		}
	}
	
	/**
	 * 
	 * @param receipt the receipt to be filed
	 */
	public void file(Receipt receipt)
	{
		try {
			receipts.put(receipt);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * prints all the receipts that were filed in the store so far
	 */

	public void print()
	{
		
		synchronized (Shoes) {
			System.out.println("----SHOES IN SOTRAGE---- ");
			for(ShoeStorageInfo shoe : Shoes.values())
				System.out.println("----\n Shoe type: " + shoe.getShoeType()+ "\n amount on storage: "
						+ shoe.getAmountOnStorage() + "\n discounted amount on storage: "+ shoe.getDiscountedAmount());
			
			System.out.println("\n\n----RECEIPTS----");
			for(Receipt receipt : receipts){
				System.out.println("\n---- \n Seller: " + receipt.getSeller() + "\n Costumer: " + receipt.getCustomer()
				+ "\n Shoe type: " + receipt.getShoeType() + "\n Discount: " + receipt.isDiscount() +
				"\n Amount sold: " + receipt.getAmountSold() + "\n Issued tick: " +receipt.getIssuedTick()
				+"\n Request tick: " + receipt.getRequestTick() + "\n ----");
		
			}
			System.out.println("number of receipts: "+ receipts.size());
			
		}

	}
	
}
	















	
