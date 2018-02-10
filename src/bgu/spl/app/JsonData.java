package bgu.spl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gson.stream.JsonReader;
/**
 * this class reads the json file input into one object
 */
public class JsonData
	{
		int sellersAmount = 0;
		int factoriesAmount = 0;
		int clientsAmount = 0;
		int totalAmount;
		private Services services;
		private ShoeStorageInfo[] initialStorage;
		
		public JsonData(Services services, ShoeStorageInfo[] initialStorage) {
			this.services = services;
			this.initialStorage = initialStorage;
	}
	public JsonData(){}	
		
	/**
     * @return jsonData , that contains the input
     */
		public JsonData readInput(InputStream in) throws IOException {
	        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	        try {
	        	reader.beginObject();
	            while (reader.hasNext()) {
	              String name = reader.nextName();
	              if (name.equals("initialStorage")) {
	            	  initialStorage = readShoesArray(reader);
	              } else if (name.equals("services")) {
	            	  services = readServices(reader);
	              } else {
	            	  
				         reader.skipValue();  
				  }
	              
	            }
	            totalAmount=2+sellersAmount+factoriesAmount+clientsAmount;
	            return this;
	              
	        } finally {
	          reader.close();
	        }
	    }
	    
	    public Services getServices() {
			return services;
		}

		public ShoeStorageInfo[] getInitialStorage() {
			return initialStorage;
		}

		public ShoeStorageInfo readShoe(JsonReader reader) throws IOException {
	    	String shoeType=null;;
	    	int amountOnStorage=-1;
	    	
	        reader.beginObject();
	        while (reader.hasNext()) {
	          String name = reader.nextName();
	          if (name.equals("shoeType")) {
	        	  shoeType = reader.nextString();
	          } else if (name.equals("amount")) {
	        	  amountOnStorage = reader.nextInt();
	          }  else {
	        	  reader.skipValue();
	          }
	        }
	        reader.endObject();
	        return new ShoeStorageInfo(shoeType,amountOnStorage);
	    }
	    
	    public ShoeStorageInfo[] readShoesArray(JsonReader reader) throws IOException {
	        List<ShoeStorageInfo> shoes = new ArrayList<ShoeStorageInfo>();

	        reader.beginArray();
	        while (reader.hasNext()) {
	        	shoes.add(readShoe(reader));
	        }
	        reader.endArray();
	        ShoeStorageInfo[] ar = (ShoeStorageInfo[]) shoes.toArray(new ShoeStorageInfo[shoes.size()] );
	        return ar;
	    }
	    
	    public TimerService readTimer(JsonReader reader) throws IOException {
	    	int speed=-1;
	    	int duration=-1;
	        reader.beginObject();
	        while (reader.hasNext()) {
	          String name = reader.nextName();
	          if (name.equals("speed")) {
	        	  speed = reader.nextInt();
	          } else if (name.equals("duration")) {
	        	  duration = reader.nextInt();
	          } else {
	              reader.skipValue();
	          }
	        }
	        reader.endObject();
	        return new TimerService(speed, duration);
	    }
	    public ManagementService readManager(JsonReader reader) throws IOException {
	    	LinkedList<DiscountSchedule> discountSchedual = new LinkedList<DiscountSchedule>();
	        reader.beginObject();
	        String name = reader.nextName();
	        if(name.equals("discountSchedule")){
	        	reader.beginArray();
		        while (reader.hasNext()) {
		        	discountSchedual.add(readDiscountSced(reader));
		        }
		        reader.endArray();
		        reader.endObject();
	        }
	        return new ManagementService(discountSchedual);
	    }
		private DiscountSchedule readDiscountSced(JsonReader reader) throws IOException {
			String shoeType=null;;
	    	int amount=-1;
	    	int tick=-1;
	        reader.beginObject();
	        while (reader.hasNext()) {
	          String name = reader.nextName();	           
	          if (name.equals("shoeType")) {
	        	  shoeType = reader.nextString();
	          } else if (name.equals("amount")) {
	        	  amount = reader.nextInt();
	          } else if (name.equals("tick")) {
	        	  tick = reader.nextInt();
	          } else {
	              reader.skipValue();
	          }
	        }
	        reader.endObject();
	        return new DiscountSchedule(shoeType,tick,amount);
			
		}
		 public List<ShoeFactoryService> readFactories(JsonReader reader) throws IOException {
		    	List<ShoeFactoryService> factories = new ArrayList<ShoeFactoryService>();
		    	int amount = reader.nextInt();
		    	factoriesAmount=amount;
		        for (int i = 1;i<=amount;i++ ){
		        	factories.add(new ShoeFactoryService("factory"+Integer.toString(i)));
		        }
		        return factories;
		 }
		 public List<SellingService> readSellers(JsonReader reader) throws IOException {
		    	List<SellingService> sellers = new ArrayList<SellingService>();
		    	int amount = reader.nextInt();
		    	sellersAmount=amount;
		    	for (int i = 1;i<=amount;i++ )
		        	sellers.add(new SellingService("seller"+Integer.toString(i)));
		        return sellers;
		 }
		 
		 private  Set<String> readWishList(JsonReader reader) throws IOException {
			 	Set<String> wishList = new HashSet<>(); 
		        reader.beginArray();
		        while (reader.hasNext()) {
		          String name = reader.nextString(); 
		          wishList.add(name);
		        }
		        reader.endArray();
		        return wishList;
				
		}
		
		 private  PurchaseSchedule readPurchaseSced(JsonReader reader) throws IOException {
				String shoeType=null;;
		    	int tick=-1;
		        reader.beginObject();
		        while (reader.hasNext()) {
		          String name = reader.nextName();
		          if (name.equals("shoeType")) {
		        	  shoeType = reader.nextString();
		          } else if (name.equals("tick")) {
		        	  tick = reader.nextInt();
		          } else {
		              reader.skipValue();
		          }
		        }
		        reader.endObject();
		        return new PurchaseSchedule(shoeType,tick);
				
		}
		 
		 public LinkedList<PurchaseSchedule> readPurchaseScedList(JsonReader reader) throws IOException {
			 LinkedList<PurchaseSchedule> list = new LinkedList<PurchaseSchedule>();

		        reader.beginArray();
		        while (reader.hasNext()) {
		        	list.add(readPurchaseSced(reader));
		        }
		        reader.endArray();
		        return list;
		 }
		 public WebClientService readClient(JsonReader reader) throws IOException {
			 	String name1 = null;
			 	Set<String> wishList = new HashSet<String>();
			 	LinkedList<PurchaseSchedule> purchaseScheduleList = new LinkedList<PurchaseSchedule>();
		        reader.beginObject();
		        while (reader.hasNext()) {
		          String name = reader.nextName();
		          if (name.equals("name")) {
		        	  name1 = reader.nextString();
		          } else if (name.equals("wishList")) {
		        	  wishList = readWishList(reader);	          
		          } else if (name.equals("purchaseSchedule")) {
		        	  purchaseScheduleList = readPurchaseScedList(reader);	          
		          } else {
		              reader.skipValue();
		          }
		        }
		        reader.endObject();
		        return new WebClientService(name1, purchaseScheduleList, wishList);
		 }
		 public WebClientService[] readCustomers(JsonReader reader) throws IOException {
			 LinkedList<WebClientService> customers = new LinkedList<WebClientService>();
		        
		        reader.beginArray();
		        while (reader.hasNext()) {
		        	customers.add(readClient(reader));
		        	clientsAmount++;
		        }
		        reader.endArray();
		        WebClientService[] ar = (WebClientService[]) customers.toArray(new WebClientService[customers.size()]);
		        return ar;
		 }
		 public Services readServices(JsonReader reader) throws IOException {
			  List<SellingService> sellers = null;
			  TimerService time = null;
			  ManagementService manager = null;
			  WebClientService[] customers = null;
			  List<ShoeFactoryService> factories = null;

			  reader.beginObject();
			  while (reader.hasNext()) {
				  String name = reader.nextName();
				  if (name.equals("time")) {
					  time = readTimer(reader);
					  
				  } else if (name.equals("manager")) {
					  manager = readManager(reader);
				  } else if (name.equals("factories")) {
					  factories = readFactories(reader);
				  } else if (name.equals("sellers")) {
					  sellers = readSellers(reader);
				  } else if (name.equals("customers")) {
					  customers = readCustomers(reader);
				  } else {
				         reader.skipValue(); 
	      
				  }
				 
			  }
		      reader.endObject();
		      return new Services(sellers, time, manager, customers, factories);
		 }
		public int getSellersAmount() {
			return sellersAmount;
		}
		public int getFactoriesAmount() {
			return factoriesAmount;
		}
		public int getClientsAmount() {
			return clientsAmount;
		}
		public int getTotalAmount() {
			return totalAmount;
		}
		
		
		 
		
		
		
		
		
		
		
		
	}		