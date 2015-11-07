package application;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import application.model.LocalStorage;

public class LocalStorageTest {

	String args = "mytest.txt";
	LocalStorage ls = new LocalStorage(args);

	@Test
	public void changePaths() {
		assertEquals(3, ls.changePath("test.txt"));
	}

}
