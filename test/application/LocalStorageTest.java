package application;

import static org.junit.Assert.*;

import org.junit.Test;

import application.model.LocalStorage;

public class LocalStorageTest {

	String args = "mytest.txt";
	LocalStorage ls = new LocalStorage(args);

	@Test
	public void changePaths() {
		assertEquals(1, ls.changePath("test.txt"));
	}

}