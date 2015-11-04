package application.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Utility {
	
	
	public static boolean hasInternetConnection() {
		try {
			URL urlObj = new URL("http://www.google.com");
			URLConnection connection = urlObj.openConnection();
			connection.connect();
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}

}
