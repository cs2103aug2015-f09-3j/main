package application.view;

import application.controller.LogicController;
import application.exception.InvalidCommandException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class UIController {
	@FXML
	private Button btSend;
	@FXML
	private TextField textCommandInput;
	@FXML
	private TextArea textConsoleOutput;
	
	

	// Event Listener on Button[#btSend].onKeyPressed
	@FXML
	public void onKeyPressed(KeyEvent event) {
		// TODO Autogenerated
		System.out.println("onKeyPressed");
		processUIRequest();
		
	
		
		
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
			feedback = LogicController.getInstance().onCommandProcess(command);
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
	
	
	
}