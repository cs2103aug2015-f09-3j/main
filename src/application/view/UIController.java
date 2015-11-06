package application.view;

//@@author  A0125975U
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

import application.Main;
import application.controller.GoogleCalendarManager;
import application.controller.LogManager;
import application.controller.LogicController;
import application.exception.InvalidCommandException;
import application.utils.GoogleCalendarUtility;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

public class UIController implements HotkeyListener {
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

	public static UIController getInstance() {
		if (instance == null) {
			instance = new UIController();
		}
		return instance;
	}

	private UIController() {  
		try {
			JIntellitype.getInstance();  
			JIntellitype.getInstance().registerHotKey(1, JIntellitypeConstants.MOD_CONTROL, 'T');
			JIntellitype.getInstance().addHotKeyListener(this);
		} catch (Exception e) {
			LogManager.getInstance().log(this.getClass().getName(), "Unable to init Jintel");
		}  
	}

	public void setMainApp(Main app) {
		this.mainApp = app;

	}

	public void toggleWindow() {

		Window window = btSend.getScene().getWindow();

		if (window.isShowing()) {
			window.hide();
		} else {
			mainApp.stage.show();
		}

	}

	// Event Listener on MenuItem[#minimizeMenuItem].onAction
	@FXML
	public void onMinimize(ActionEvent event) {
		
	}

	public void showToUser(String response) {
		textConsoleOutput.setStyle("-fx-font-family: monospace");
		textConsoleOutput.insertText(textConsoleOutput.getLength(), response);

	}

	// Event Listener on Button[#btSend].onMouseClicked
	@FXML
	public void onMouseClicked(MouseEvent event) {

		processUIRequest();

	}

	private String onCommandReceived(String command) {

		String feedback;

		try {
			LogicController.getInstance();
			feedback = LogicController.onCommandProcess(command);
		} catch (InvalidCommandException e) {
			feedback = e.getMessage();
		}
		if (GoogleCalendarUtility.hasInternetConnection()) {
			GoogleCalendarManager.getInstance().performSync();
		}

		return feedback;
	}

	@FXML
	public void onEnter(ActionEvent event) {
		processUIRequest();

	}

	private void processUIRequest() {
		try {
			String response = onCommandReceived(textCommandInput.getText()) + "\n";
			showToUser(response);
		} catch (NullPointerException ex) {
			showToUser("Please try again with command details. \n");
		} finally {
			textCommandInput.clear();
		}
	}

	@Override
	public void onHotKey(int arg0) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				toggleWindow();
			}
		});

	}

}