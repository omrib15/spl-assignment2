package bgu.spl.app;

import java.util.List;

public class Services
{
	private TimerService time;

    private ManagementService manager;

    private WebClientService[] customers;

    private List<ShoeFactoryService> factories;
    
    
	public Services(List<SellingService> sellers, TimerService time, ManagementService manager,
		WebClientService[] customers, List<ShoeFactoryService> factories) {
		this.sellers = sellers;
		this.time = time;
		this.manager = manager;
		this.customers = customers;
		this.factories = factories;
	}
    public void setSellers(List<SellingService> sellers) {
		this.sellers = sellers;
	}

	public void setTime(TimerService time) {
		this.time = time;
	}

	public void setManager(ManagementService manager) {
		this.manager = manager;
	}

	public void setCustomers(WebClientService[] customers) {
		this.customers = customers;
	}

	public void setFactories(List<ShoeFactoryService> factories) {
		this.factories = factories;
	}
	private List<SellingService> sellers;

    public List<SellingService> getSellers() {
		return sellers;
	}

	public TimerService getTime() {
		return time;
	}

	public ManagementService getManager() {
		return manager;
	}

	public WebClientService[] getCustomers() {
		return customers;
	}

	public List<ShoeFactoryService> getFactories() {
		return factories;
	}

	

	

}   