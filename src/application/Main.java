package application;

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
			Scene scene = new Scene(root, 1007, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			uiControl.setMainApp(this);
			firstTime = true;
		    Platform.setImplicitExit(false);  
			primaryStage.show();
			
			createAndShowGUI();
			this.stage = primaryStage;
			
		
			
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		launch(args);
	}
	
	/**
	 * DISCLAIMER: this code is adapted from java official code examples.
	 */
	 private void createAndShowGUI() {
	        //Check the SystemTray support
	        if (!SystemTray.isSupported()) {
	            System.out.println("SystemTray is not supported");
	            return;
	        }
	        final PopupMenu popup = new PopupMenu();
	        trayIcon =
	                new TrayIcon(createImage("bulb.gif", "tray icon"));
	        final SystemTray tray = SystemTray.getSystemTray(); 
	        
	        // Create a popup menu components
	        /*
	        MenuItem aboutItem = new MenuItem("About"); 
	        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
	        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
	        Menu displayMenu = new Menu("Display");
	        MenuItem errorItem = new MenuItem("Error");
	        MenuItem warningItem = new MenuItem("Warning");
	        MenuItem infoItem = new MenuItem("Info");
	        MenuItem noneItem = new MenuItem("None");
	        */
	        MenuItem exitItem = new MenuItem("Exit");
	       /* 
	        //Add components to popup menu
	        popup.add(aboutItem);
	        popup.addSeparator();
	        popup.add(cb1);
	        popup.add(cb2);
	        popup.addSeparator();
	        popup.add(displayMenu);
	        displayMenu.add(errorItem);
	        displayMenu.add(warningItem);
	        displayMenu.add(infoItem);
	        displayMenu.add(noneItem);
	        */
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
	               // JOptionPane.showMessageDialog(null,
	                //        "This dialog box is run from System Tray");
	            	 Platform.runLater(new Runnable() {   
	                        @Override
	                        public void run() { 
	                            stage.show();
	                        }
	                    });
	            	
	            }
	        });
	        /*
	        aboutItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            
	                JOptionPane.showMessageDialog(null,
	                        "This dialog box is run from the About menu item");
	            }
	        });
	        
	        cb1.addItemListener(new ItemListener() {
	            public void itemStateChanged(ItemEvent e) {
	                int cb1Id = e.getStateChange();
	                if (cb1Id == ItemEvent.SELECTED){
	                    trayIcon.setImageAutoSize(true);
	                } else {
	                    trayIcon.setImageAutoSize(false);
	                }
	            }
	        });
	        
	        cb2.addItemListener(new ItemListener() {
	            public void itemStateChanged(ItemEvent e) {
	                int cb2Id = e.getStateChange();
	                if (cb2Id == ItemEvent.SELECTED){
	                    trayIcon.setToolTip("Sun TrayIcon");
	                } else {
	                    trayIcon.setToolTip(null);
	                }
	            }
	        });
	        
	        ActionListener listener = new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                MenuItem item = (MenuItem)e.getSource();
	                //TrayIcon.MessageType type = null;
	                System.out.println(item.getLabel());
	                if ("Error".equals(item.getLabel())) {
	                    //type = TrayIcon.MessageType.ERROR;
	                    trayIcon.displayMessage("Sun TrayIcon Demo",
	                            "This is an error message", TrayIcon.MessageType.ERROR);
	                    
	                } else if ("Warning".equals(item.getLabel())) {
	                    //type = TrayIcon.MessageType.WARNING;
	                    trayIcon.displayMessage("Sun TrayIcon Demo",
	                            "This is a warning message", TrayIcon.MessageType.WARNING);
	                    
	                } else if ("Info".equals(item.getLabel())) {
	                    //type = TrayIcon.MessageType.INFO;
	                    trayIcon.displayMessage("Sun TrayIcon Demo",
	                            "This is an info message", TrayIcon.MessageType.INFO);
	                    
	                } else if ("None".equals(item.getLabel())) {
	                    //type = TrayIcon.MessageType.NONE;
	                    trayIcon.displayMessage("Sun TrayIcon Demo",
	                            "This is an ordinary message", TrayIcon.MessageType.NONE);
	                }
	            }
	        };
	        */
	        /*
	        errorItem.addActionListener(listener);
	        warningItem.addActionListener(listener);
	        infoItem.addActionListener(listener);
	        noneItem.addActionListener(listener);
	        
	        */
	        
	        exitItem.addActionListener(new ActionListener() {
	            @Override
				public void actionPerformed(ActionEvent e) {
	                tray.remove(trayIcon);
	                JIntellitype.getInstance().cleanUp();
	                System.exit(0);
	            }
	        });
	    } 
	    
	    //Obtain the image URL
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
