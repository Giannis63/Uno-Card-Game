import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Controller for the choose game screen, where users can see a leaderboard of the top 15 players,
 * select the deck they want to play with and request to join a game. Once the game has enough
 * players user is taken to the Main game view.
 * 
 * Can reach this view from login/ register or at the end of a game.
 * 
 * User can select either classic Uno (selected by default), Nintendo or Sonic branded cards,
 * by clicking the relevant image. Their choice is highlighted.
 * 
 * These are loaded by the GameController class in a background thread, put into a hashmap
 * of ImageViews for quick retrieval. The user can change their selection up until their game
 * begins.
 * 
 * Users select what kind of game they want (2, 3 or 4 players) by clicking the relevant image.
 * Their choice will be highlighted and they cannot change their selection once made.
 * 
 * Controller communicates with serverside via a Client, using a shared BlockingQueue. If the
 * Client cannot communicate with serverside an error alert message will be shown.
 *
 */
public class ChooseController {
	
	@FXML
	GridPane leaderboard;
	
	@FXML
	Label message;
	
	@FXML
	Pane unoBorder;
	
	@FXML
	Pane nintendoBorder;
	
	@FXML
	Pane sonicBorder;
	
	@FXML
	Pane border2;
	
	@FXML 
	Pane border3;
	
	@FXML
	Pane border4;

	private int counter = 1;
	private BlockingQueue<Object> toClient;
	private boolean waiting = false;
    private boolean error = false;
	
    public void addToLeaderBoard(String details) {
      Platform.runLater(new Runnable(){
        @Override public void run() {
          String[] breakdown = details.split(" ");
          Label name = new Label(breakdown[1]);
          Label wins = new Label(breakdown[2]);
          Label loses = new Label(breakdown[3]);
          Label points = new Label(breakdown[4]);
          leaderboard.add(name, 1, counter);
          leaderboard.add(wins, 2, counter);
          leaderboard.add(loses, 3, counter);
          leaderboard.add(points, 4, counter);
          counter++;
    	}
      });
    }
    
    public void joinGame(MouseEvent event) {
    	if(error == true) {
    		error();
    		return;
    	}
    	 if(waiting == false) {
    	 boolean error = false;
    	 System.out.println("Joining a game");
    	 String clicked = event.getPickResult().getIntersectedNode().getId();
    	    	int numberOfPlayers = Character.getNumericValue(clicked.charAt(4));
    	        try {
    	          toClient.put(numberOfPlayers);
    	        }catch(Exception e) {
    	            System.out.println(e);
    	            error();
    	            error = true;

    	        } if(error == false) {
    	            message.setText("Waiting for more players"); 
    	            waiting = true;
    	            Pane[] borders = new Pane[] {border2, border3, border4};
    	            Pane border = borders[numberOfPlayers-2];
    	            border.setStyle("-fx-border-color:  #F0E68C;\n"
    	                      + "-fx-border-radius: 5;\n"
    	                      + "-fx-border-width: 3;\n");
    	        }
    	      }
    	    }

    // moving to main game view. Scene pre-loaded in Uno class  
    public void gameStart() {
      Platform.runLater(new Runnable(){
      @Override public void run() {
        try {
          	
          Scene gameScene = Uno.getGameScene();
          Stage primary = Uno.getPrimary();
          primary.setScene(gameScene);
          primary.centerOnScreen();
    	} catch(Exception ex) {
    	  System.out.println(ex);
    	}
      }
      });
    }
    
    public void setCardImages(MouseEvent event) {
    	String id = event.getPickResult().getIntersectedNode().getId();
    	     if(id.equals("nintendo")){
    	        nintendoBorder.setStyle("-fx-border-color:  #F0E68C;\n"
    	                        + "-fx-border-radius: 5;\n"
    	                        + "-fx-border-width: 3;\n");
    	        		unoBorder.setStyle("");
    	        		sonicBorder.setStyle("");
    	        		
    	     } else if(id.equals("uno")) {
    	       unoBorder.setStyle("-fx-border-color:  #F0E68C;\n"
    	                        + "-fx-border-radius: 5;\n"
    	                        + "-fx-border-width: 3;\n");
    	        		nintendoBorder.setStyle("");
    	        		sonicBorder.setStyle("");
    	     } else if(id.equals("sonic")) {
    	    	 sonicBorder.setStyle("-fx-border-color:  #F0E68C;\n"
	                        + "-fx-border-radius: 5;\n"
	                        + "-fx-border-width: 3;\n");
	             nintendoBorder.setStyle("");
	        	 unoBorder.setStyle("");
    	     }
       imageChoice(id);
    }
    
    // significant processing so in a seperate thread
    public void imageChoice(String id) 
    {
        Runnable task = new Runnable()
        {
            public void run()
            {
              GameViewController.setupCardImages(id);
            }
        };
         
        Thread backgroundThread = new Thread(task);
        backgroundThread.start();
    }

    public void setToClient(BlockingQueue<Object> toClient) {
        this.toClient = toClient;
      }
    
    // for when returning from end of a game.
    public void reset() {
      Platform.runLater(new Runnable(){
    	@Override public void run() {
    	  try {
    	    waiting = false;
    	    message.setText("Join A Game: Choose Number of Players"); 
     	    border2.setStyle("");
    	    border3.setStyle("");
    	    border4.setStyle("");
    	  }catch(Exception e){
    		  System.out.println(e);
    	  }
    	}
    	 });
    }
    
    public void error() {
        Platform.runLater(new Runnable(){
      	@Override public void run() {
      	  reset();
      	  error = true;
      	  Alert alert = new Alert(Alert.AlertType.INFORMATION);
      	  alert.initStyle(StageStyle.UTILITY);
      	  alert.setTitle("Error");
      	  alert.setHeaderText("Connection Error.");
      	  alert.setContentText("Server connection lost. Please close the application and try later.");
          alert.showAndWait();
      	}
        });
      }

}
