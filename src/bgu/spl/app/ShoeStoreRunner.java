package bgu.spl.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;



public class ShoeStoreRunner {

	
    public static void main(String[] args) {
    	Scanner in = new Scanner(System.in);
    	File initialFile = new File("src/bgu/spl/app/"+args[0]+".json");
    	JsonData input=readJson(initialFile);
		final CountDownLatch latch = new CountDownLatch(input.getTotalAmount());
		Store.get_Instance().load(input.getInitialStorage());
		runServices(input, latch);
		
    }
    
	public static JsonData readJson(File initialFile) {
    	
        try {
			InputStream targetStream = new FileInputStream(initialFile);
			JsonData input=new JsonData();
			input=input.readInput(targetStream);
			return input;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	
        
    }
    public static void runFactories(List<ShoeFactoryService> factories,CountDownLatch latch){
    	for (ShoeFactoryService it: factories)
    	{
    		it.setM_latchObject(latch);
    		Thread thread = new Thread(it);
    		thread.start();
    	}
    }
    public static void runClients(WebClientService[] clients,CountDownLatch latch){
    	for (WebClientService it: clients)
    	{
    		it.setM_latchObject(latch);
    		Thread thread = new Thread(it);
    		thread.start();
    	}
    }
    private static void runSellers(List<SellingService> sellers, CountDownLatch latch) {
    	for (SellingService it: sellers)
    	{
    		it.setM_latchObject(latch);
    		Thread thread = new Thread(it);
    		thread.start();
    	}		
	}
    private static void runManager(ManagementService manager, CountDownLatch latch) {
    	manager.setM_latchObject(latch);
		Thread thread = new Thread(manager);
		thread.start();
		
	}
    private static void runTimer(TimerService time, CountDownLatch latch) {
    	time.setM_latchObject(latch);
		
		time.start();

		
	}
    private static void runServices(JsonData input,CountDownLatch latch) {
    	runFactories(input.getServices().getFactories(), latch);
		runClients(input.getServices().getCustomers(), latch);
		runSellers(input.getServices().getSellers(),latch); 
		runManager(input.getServices().getManager(),latch);
		runTimer(input.getServices().getTime(),latch);
    }
    

		
    
	 
	 
	
   
    

}

	
	
	
	
