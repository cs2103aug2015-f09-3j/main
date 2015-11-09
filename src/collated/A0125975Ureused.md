# A0125975Ureused
###### application\utils\GoogleCalendarUtility.java
``` java
import java.io.BufferedReader;
```
###### application\utils\GoogleCalendarUtility.java
``` java
	/**
	 * Disclaimer : this method is from :
	 * http://www.journaldev.com/833/how-to-delete-a-directoryfolder-in-java-
	 * recursion
	 * 
	 * @param file
	 */
	public static void recursiveDelete(File file) {
		// to end the recursive loop
		if (!file.exists())
			return;

		// if directory, go inside and call recursively
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				// call recursively
				recursiveDelete(f);
			}
		}
		// call delete to delete files and empty directory
		file.delete();
	}

}
```
