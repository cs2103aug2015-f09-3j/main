package application.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TokenManager {

	private static TokenManager instance;
	private static final String DEFAULT_TOKEN_PATH = "token.txt";
	File file;

	private TokenManager() {
		file = new File(DEFAULT_TOKEN_PATH);
	}

	public static TokenManager getInstance() {
		if (instance == null) {
			instance = new TokenManager();
		}

		return instance;
	}

	public String getLastToken() {

		try {
			if (!file.exists()) {
				return null;
			} else {

				FileInputStream fIn = new FileInputStream(file);
				BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
				String token = myReader.readLine();

				myReader.close();
				return token;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
	
	public void clearToken(){
		setToken("");
	}

	public boolean setToken(String token) {

		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write(token);
			bw.close();
			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

}
