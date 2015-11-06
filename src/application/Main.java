package application;

//@@author  A0125975U
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;

import com.melloware.jintellitype.JIntellitype;

import application.controller.GoogleCalendarManager;
import application.controller.LogManager;
import application.utils.GoogleCalendarUtility;
import application.view.UIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	boolean firstTime;
	private TrayIcon trayIcon;
	public Stage stage;

	@Override
	public void start(Stage primaryStage) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UI.fxml"));
			UIController uiControl = UIController.getInstance();

			fxmlLoader.setController(uiControl);

			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 1000, 600); 
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("toDoo");
			primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("toDoo.png")));
			uiControl.setMainApp(this);
			firstTime = true;
			Platform.setImplicitExit(false);
			primaryStage.show();

			createAndShowGUI();
			this.stage = primaryStage; 

			if (GoogleCalendarUtility.hasInternetConnection()) {
				GoogleCalendarManager.getInstance().performSync();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		launch(args);

	}

	/**
	 * @@Java DISCLAIMER: this code is adapted from java official code examples.
	 */
	private void createAndShowGUI() {
		// Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(createImage("toDoo.png", "tray icon"));
		final SystemTray tray = SystemTray.getSystemTray();

		MenuItem exitItem = new MenuItem("Exit");

		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		trayIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						stage.show();
					}
				});

			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				try {
					tray.remove(trayIcon);
					JIntellitype.getInstance().cleanUp();
				} catch (Exception err) {
					LogManager.getInstance().log(this.getClass().getName(), err.toString());
				}
				System.exit(0);

			}
		});
	}

	// Obtain the image URL
	protected static Image createImage(String path, String description) {

		URL imageURL = Main.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

}
