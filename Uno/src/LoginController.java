import java.security.MessageDigest;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controller for the first view, login. User must login before they can access the application.
 * 
 * Username and password fields provided - if entered, password is hashed and then both
 * sent to serverside (via Client object) for validation. If valid, taken through to choose
 * game screen, if not message shown and user can try again. Can also move to a register
 * screen from here.
 * 
 * Controller communicates with Client using a shared BlockingQueue.
 * If Client cannot communicate with serverside and error message is shown.
 */
public class LoginController {
	
	@FXML
	TextField username;
	
	@FXML
	PasswordField password;
	
	@FXML
    Label message;
	
	@FXML 
	Button login;
	
	@FXML
	Button register;
	
	@FXML
	Label confirmLabel;
	
	@FXML
	PasswordField confirm;
	
	@FXML
	GridPane grid;
	
	private BlockingQueue<Object> toClient;
	private boolean error = false;
	
	public void loginExisting(ActionEvent e) {
		  if(error == true) {
			  showError();  
		  } else {
		    try {
			  System.out.println("Sending to client");
	    	  String pass = hash(password.getText());
	    	  toClient.put("LOGIN");
	          toClient.put(username.getText());
	          toClient.put(pass);
		    } catch(Exception i) {
			  System.out.println(i);
		  }
		}
		}

	
	public String hash(String password) {
    	try {
    	  MessageDigest md = MessageDigest.getInstance("SHA-256");
    	  md.update(password.getBytes());
    	  byte[] digest = md.digest();
    	  StringBuffer hexString = new StringBuffer(); 
          for (int i = 0;i<digest.length;i++) {
            hexString.append(Integer.toHexString(0xFF & digest[i]));
          }
    	  password = hexString.toString();
    	}catch(Exception e) {
    		System.out.println(e);
    	}
    	return password;
    }
	
	public void invalid(){
	System.out.println("Login controller told invalid");
      Platform.runLater(new Runnable(){
	    @Override public void run() {
	      message.setText("Incorrect Username or Password");
	      message.setTextFill(Color.RED);
	      username.setText("");
	      password.setText("");
	    }
	  });
     }

	public void loggedIn() {
	  System.out.println("taking to chooseView");
      Platform.runLater(new Runnable(){
	    @Override public void run() {
	    	try {
	    	    Scene chooseScene = Uno.getChooseScene();
	    	    Stage primary = Uno.getPrimary();
	    	    primary.setScene(chooseScene);
	    	    primary.centerOnScreen();
	    	   } catch(Exception ex) {
	    	       System.out.println(ex);
	    	   }
	    }
	  });
	}
 
	public void register(ActionEvent e) {
		if(error == true) {
			showError();
		} else {
		  System.out.println("Taking to register view");
          Scene registerScene = Uno.getRegisterScene();
          Stage primary = Uno.getPrimary();
          primary.setScene(registerScene);
		}
    }
	
	public void error() {
		error = true;
	}

	
	public void setToClient(BlockingQueue<Object> toClient) {
	  this.toClient = toClient;
	}
	
	public void showError() {
		Platform.runLater(new Runnable(){
		    @Override public void run() {
		    	try {
		          Alert alert = new Alert(Alert.AlertType.INFORMATION);
		          alert.initStyle(StageStyle.UTILITY);
		         alert.setTitle("Error");
		         alert.setHeaderText("Connection Error.");
		         alert.setContentText("Unable to connect to server at this time. Please close the application and try again later");
	             alert.showAndWait();
		    	}catch(Exception e) {
		    		System.out.println(e);
		    	}
		    }
		    });
    }
}


