package bgu.spl.mics.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bgu.spl.app.PurchaseOrderRequest;
import bgu.spl.app.SellingService;
import bgu.spl.app.ShoeStorageInfo;
import bgu.spl.app.Store;
import bgu.spl.app.WebClientService;
import bgu.spl.mics.MicroService;

public class MessageBusImplTest {
	
	MessageBusImpl bus ;
	@BeforeClass
	public void setUpBeforeClass() throws Exception {
		WebClientService client = new WebClientService("client", null, null);
		SellingService seller = new SellingService("seller");
		bus.register(seller);
		bus.register(client);
		
	}
	
	
	@Before
	public void setUp() throws Exception {
		bus = MessageBusImpl.get_Instance();
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testGet_Instance() {
		MessageBusImpl bus1=MessageBusImpl.get_Instance();
		assertEquals(bus, bus1);
	}

	@Test
	public void testSubscribeRequest() {
		PurchaseOrderRequest p = new PurchaseOrderRequest("a", false, "client", 2);
		
	}

	@Test
	public void testSubscribeBroadcast() {
		
	}

	@Test
	public void testComplete() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendBroadcast() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegister() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnregister() {
		fail("Not yet implemented");
	}

	@Test
	public void testAwaitMessage() {
		fail("Not yet implemented");
	}

}
