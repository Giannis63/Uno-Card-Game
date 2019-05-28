
import java.security.MessageDigest;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controller for the register screen, which is reached from the login screen. Can return
 * to login screen using a back button.
 * 
 * Fields for user to enter a desired username, enter password and to confirm password.
 * 
 * If user selects to register, validation is carried out that password and confirm are 
 * identical and that the password is between 5 and 15 characters and has both uppercase
 * and lowercase characters.
 * 
 * If okay, password is hashed and details sent to serverside for registration (via Client object)
 * If username available, user moves on to choose game screen. If not, message shown and can
 * try with another. 
 * 
 * Sends data to the Client using a shared BlockingQueue. If the Client cannot communication
 * with serverside an error alert will be shown.
 */
public class RegisterController {

    @FXML
    Label message;

    @FXML
    Button goBack;

    @FXML
    Button registerButton;

    @FXML
    TextField newUsername;

    @FXML
    PasswordField newPassword;

    @FXML
    PasswordField checkPassword;
    
    private BlockingQueue<Object> toClient;
    private boolean error = false;;

    public void registerNow(ActionEvent e) {
    	if(error == true) {
        	  showError();
        	  return;
        } 
    	if(testUsername(newUsername.getText())) {
    	  Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("UserName");
          alert.setHeaderText("Username Contains Space");
          alert.setContentText("Please retype without Spaces");
          alert.show();
          newUsername.setText("");
          newPassword.setText("");
          checkPassword.setText("");
          return;
    	}if(!newPassword.getText().equals(checkPassword.getText())) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Password");
          alert.setHeaderText("Passwords don't match");
          alert.setContentText("Please retype your password");
          alert.show();
        } else if((newPassword.getText().length()<5 && newPassword.getText().length()>15) || testUpperLowerCaseMix(newPassword.getText())) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Password");
          alert.setHeaderText("Password strength");
          alert.setContentText("Password needs to be between 5 & 15 characters & contain a mix of upper and lower case letters.");
          alert.show();
        }
        else {
            try {
        	  String pass = hash(newPassword.getText());
              System.out.println("Sending to client");
              toClient.put("REGISTER");
              toClient.put(newUsername.getText());
              toClient.put(pass);
            } catch(Exception i) {
            System.out.println(i);
            error = true;
            showError();
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
    	System.out.println("Register controller told invalid");
          Platform.runLater(new Runnable(){
    	    @Override public void run() {
    	      message.setText("Username taken, please choose another");
    	      message.setTextFill(Color.RED);
    	      newUsername.setText("");
    	      newPassword.setText("");
    	      checkPassword.setText("");
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

    public void backButton(ActionEvent event){
    	if(error == true) {
    		showError();
    	    return;
        }
    	Stage primary = Uno.getPrimary();
    	try { // resolves issue with login screen not showing correctly on selecting back
    	  FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
		  Parent root = loader.load();
		  LoginController lController = loader.getController();
		  Uno.getClient().setLController(lController);
		  lController.setToClient(toClient);
		  Scene login = new Scene(root);
		  Uno.setLoginScene(login);
		  primary.setScene(login);
    	}catch(Exception ex) {
    		System.out.println(ex);
    	}
}

    public void setToClient(BlockingQueue<Object> toClient) {
        this.toClient = toClient;
    }
    
    public static boolean testUsername(String str) {
//    	String []  str.split(" ")
    
    	for(int i=0; i<str.length(); i++) {
    		if(str.charAt(i)== ' ') {
    			return true;
    		
    	}
    	}
    		return false;
    	
    }
    
    public static boolean testUpperLowerCaseMix(String str) {
////    	String []  str.split(" ")
//    
//    	for(int i=0; i<str.length(); i++) {
//    		if(str.charAt(i)== ' ') {
//    			return false;
//    		
//    	}
//    	}
    	
    	  int k = 0;
    	  for (int l = 0; l < str.length(); l++) {
    	    char c = str.charAt(l);
    	    if (c >= 97 && c <= 122) {
    	      k++;
    	    }
    	  }
    	  if (k < str.length() && k>0) {
    	            //str.charAt(index)
    	    System.out.println("true");
    	    return false;
    	  } else
    	  System.out.println("false");
    	  return true;
    	}
    
    public void error() {
    	error = true;
    }
    
    public void showError() {
    	error = true;
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

