package bgu.spl.mics.impl;


import java.util.logging.Level;
import java.util.logging.Logger;





public class MyLogger {

	public static final Logger logger = Logger.getLogger("Test");
	private static MyLogger instance = null;
	    
	private MyLogger(){
		logger.setLevel(Level.INFO);
	}
	
	   public static MyLogger getInstance() {
	      if(instance == null) {
	         instance = new MyLogger ();
	      }
	      return instance;
	   }
	  
	
}
