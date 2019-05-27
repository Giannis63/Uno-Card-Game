import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/** Class to start the application for a user.
 *  Sets up stages for the 4 views: login, register, choose game and main game.
 *  Stores these statically so they can be retrieved at the appropriate point,
 *  without having to reload FXML.
 *  
 *  Attempts to connect to server, putting this connection in a Client thread
 *  if successful. 
 *  
 *  The controllers for all views are passed to the Client thread so their methods
 *  can be called when appropriate data received from serverside application.
 *  
 * Each controller is passed the Client's blockingQueue, which is used for thread safe
 * communication between them.
 */
public class Uno extends Application {
	
	public static GameViewController gController;
	public static LoginController lController;
	public static Client client;
	public static Scene gameScene;
	public static Stage primary;
	public static Scene registerScene;
	public static Scene loginScene;
	public static Scene chooseScene;
	public static RegisterController rController;
	public static ChooseController cController;

	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primary = primaryStage;
		
		GameViewController.setupCardImages("uno");
		
		client = new Client("localhost", 7777);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		Parent root = loader.load();
		gController = loader.getController();
		client.setGController(gController);
		BlockingQueue<Object> toClient = client.getInputQueue();
		gController.setToClient(toClient);
		gameScene = new Scene(root);
		
		loader = new FXMLLoader((getClass().getResource("/Choose.fxml")));
		root = loader.load();
		cController = loader.getController();
		cController.setToClient(toClient);
		client.setCController(cController);
		chooseScene = new Scene(root);
		
		loader = new FXMLLoader((getClass().getResource("/RegisterScreen.fxml")));
	    root = loader.load();
		rController = loader.getController();
		rController.setToClient(toClient);
		client.setRController(rController);
		registerScene = new Scene(root);
		
		loader = new FXMLLoader(getClass().getResource("/login.fxml"));
		root = loader.load();
		lController = loader.getController();
		lController.setToClient(toClient);
		client.setLController(lController);
		Scene loginScene = new Scene(root);
		
		Thread t = new Thread(client);
		t.start();
		
		if(t.isAlive()){
		  primaryStage.setTitle("Uno");
		  primaryStage.setOnCloseRequest(e -> {
		        Platform.exit();
		        System.exit(0);
		    });
		  primaryStage.setScene(loginScene);
		  primaryStage.setResizable(false);
		  primaryStage.show();
		}
		
	}
	
	public static Scene getGameScene() {
		return gameScene;
	}
	
	public static void setGameScene(Scene newGameScene) {
		gameScene = newGameScene;
	}
	
	public static void setLoginScene(Scene newLoginScene) {
		loginScene = newLoginScene;
	}
	

	public static Stage getPrimary() {
		return primary;
	}
	
	public static  Scene getRegisterScene(){
		return registerScene;
    }
    
	public static Scene getLoginScene(){
		return  loginScene;
    }  
	
	public static Scene getChooseScene() {
		return chooseScene;
	}
	
	public static ChooseController getCController() {
		return cController;
	}
	
	public static Client getClient() {
		return client;
	}

	
}
