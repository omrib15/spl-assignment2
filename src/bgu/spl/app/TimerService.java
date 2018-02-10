package bgu.spl.app;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import bgu.spl.mics.impl.MessageBusImpl;
import bgu.spl.mics.impl.MyLogger;

public class TimerService {
	private static int currentTick=0;
	private int speed;
	private int duration;
	private String name = "timer";
	private Timer timer = new Timer();
    private CountDownLatch m_latchObject=null;
    private Logger logger = MyLogger.getInstance().logger;

	
	public TimerService(int speed,int duration) {
		this.speed=speed;
		this.duration=duration;
		
	}
	
	//the task that sends a new TickBroadcast each passing "speed" milliseconds 
	TimerTask task = new TimerTask() {
		 
		public void run() {
			currentTick++;
		
	
			if (currentTick>duration)
			{ // the time designated for the process has expired, stop executing.
				logger.info("~~~~~~~~~~~~~~~time expired, terminating all~~~~~~~~~~~~~~~~");
				MessageBusImpl.get_Instance().sendBroadcast(new TerminationBroadcast());
				
				//sleeping for half a second so that the store records would be printed last
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				timer.cancel();
				Store.get_Instance().print();
			}
			// did'nt reach the designated time for the process
			else {
				TickBroadcast b = new TickBroadcast(currentTick);
				logger.info("**************************a tick passed, current : " +currentTick+ " *******************");
				MessageBusImpl.get_Instance().sendBroadcast(b);
			}
			
		}
	};
	
	
	public void start() {
		//schedualing "task" to be performed each "speed" milliseconds, after a small delay of 1/5 second
		timer.schedule(task, 200, speed);
		m_latchObject.countDown();
	}


	public void setM_latchObject(CountDownLatch latch) {
		this.m_latchObject = latch;		
	}

	
}















/*package bgu.spl.app;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.impl.MyLogger;

public class TimerService extends MicroService {
	private int speed;
	private int duration;
	private int tick;
	private Timer timer;
	private TimerTask timerTask;
	private CountDownLatch latch = super.getM_latchObject();
	private Logger logger = MyLogger.getInstance().logger;
	
	public TimerService(int speed, int duration) {
		super("timer");
		this.tick = 1;
		this.speed = speed;
		this.duration = duration;
		timer = new Timer();
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				
				sendBroadcast(new TickBroadcast(tick));
				tick++;
				if(tick == duration)
					sendBroadcast(new TerminationBroadcast());
					cancel();
			}
		};
	}
	
	@Override
	protected void initialize() {
		timer.scheduleAtFixedRate(timerTask, 0, speed);
		logger.info(getName() +" has initalized");
	}
	
	
	

}
*/