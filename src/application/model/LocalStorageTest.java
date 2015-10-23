package application.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocalStorageTest {
	LocalStorage ls;

	@Before
	public void setUp() throws Exception {
		String args = "mytdd.txt";
		ls = new LocalStorage(args);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void changePaths() {
		assertEquals(1, ls.changePath("test.txt"));
	}

}
