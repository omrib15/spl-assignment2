package bgu.spl.app;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StoreTest {
	Store store;

	
	@BeforeClass
	public void setUpBeforeClass() throws Exception {
		ShoeStorageInfo [] storage =  {new ShoeStorageInfo("sandals", 7),
				new ShoeStorageInfo("blundstone", 9),
				new ShoeStorageInfo("hiking-shoes", 5),
				new ShoeStorageInfo("blue-flip-flops", 8)};
		store.load(storage);
	}
	@Before
	public void setUp() throws Exception {
		store = Store.get_Instance();
		
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testGet_Instance() {
		Store s=Store.get_Instance();
		assertEquals(store, s);
	}

	@Test
	public void testLoad() {
		
	}

	@Test
	public void testTake() {
		BuyResult status = store.take("sandals", false);
		assertEquals(BuyResult.REGULAR_PRICE, status);
		 status = store.take("teva-naot", false);
		assertEquals(BuyResult.NOT_IN_STOCK, status);
		 store.addDiscount("hiking-shoes",3);
		 status = store.take("hiking-shoes", true);
		assertEquals(BuyResult.DISCOUNTED_PRICE, status);
	}
	

	@Test
	public void testAdd() {
		BuyResult status = store.take("tave", false);
		assertEquals(BuyResult.NOT_IN_STOCK, status);
		store.add("tave", 1);
		status = store.take("tave", false);
		assertEquals(BuyResult.REGULAR_PRICE, status);
	}

	@Test
	public void testAddDiscount() {
		BuyResult status = store.take("blundstone", true);
		assertEquals(BuyResult.NOT_ON_DISCOUNT, status);
		store.addDiscount("blundstone", 2);
		status = store.take("blundstone", true);
		assertEquals(BuyResult.DISCOUNTED_PRICE, status);
		status = store.take("blundstone", true);
		assertEquals(BuyResult.DISCOUNTED_PRICE, status);
		status = store.take("blundstone", true);
		assertEquals(BuyResult.NOT_ON_DISCOUNT, status);
	}

	
	

}
