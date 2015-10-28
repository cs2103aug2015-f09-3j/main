package application.view;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

import application.Main;
import application.controller.LogicController;
import application.exception.InvalidCommandException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

public class UIController implements HotkeyListener{
	@FXML
	private MenuItem minimizeMenuItem;
	@FXML
	private Button btSend;
	@FXML
	private TextField textCommandInput;
	@FXML
	private TextArea textConsoleOutput;

	Main mainApp;


	private static UIController instance;

	public static UIController getInstance(){
		if(instance == null){
			instance = new UIController();
		}
		return instance;
	}

	private UIController(){
		JIntellitype.getInstance();
		JIntellitype.getInstance().registerHotKey(1, JIntellitypeConstants.MOD_CONTROL, 'T');
		JIntellitype.getInstance().addHotKeyListener(this);
	}

	public void setMainApp(Main app){
		this.mainApp = app;


	}

	public void toggleWindow(){

		Window window = btSend.getScene().getWindow();

		if(window.isShowing()){
			window.hide();
		}
		else{
			mainApp.stage.show();
		}

	}


	// Event Listener on MenuItem[#minimizeMenuItem].onAction
	@FXML
	public void onMinimize(ActionEvent event) {
		//Minimize the program here.
		System.out.println("hello");
		//getWindow().hide();


	}

	public void showToUser(String response) {
		textConsoleOutput.setStyle("-fx-font-family: monospace");
		System.out.println(response);
		textConsoleOutput.insertText(textConsoleOutput.getLength(), response);

	}
	// Event Listener on Button[#btSend].onMouseClicked
	@FXML
	public void onMouseClicked(MouseEvent event) {
		// TODO Autogenerated
		System.out.println("onMouseClicked");
		processUIRequest();


	}

	private String onCommandReceived(String command){
		//Do whatever you want with the command here. Pass to LogicController to do its stuff.

		String feedback;

		try {
			LogicController.getInstance();
			feedback = LogicController.onCommandProcess(command);
		} catch (InvalidCommandException e) {
			feedback = e.getMessage();
		}

		return feedback;
	}

	@FXML
	public void onEnter(ActionEvent event) {
		System.out.println("onEnter");
		processUIRequest();

	}
	private void processUIRequest() {
		try{
			String response = onCommandReceived(textCommandInput.getText()) + "\n";
			showToUser(response);
		}catch(NullPointerException ex){
			showToUser("Please try again with command details. \n");
		}finally{
			textCommandInput.clear();
		}
	}

	@Override
	public void onHotKey(int arg0) {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				toggleWindow();
			}});

	}



}