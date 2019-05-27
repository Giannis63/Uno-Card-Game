import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Paint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the main game view. Main class in the clientside application, responding
 * to various method calls and data received from the Client (based in turn on data received 
 * from serverside).
 * 
 * Displays player names, whose turn it is, cards in users' hand, how many cards the 
 * other players have, the active colour and the latest pile card. General gameplay messages are also displayed.
 * 
 * User can try to play a card by clicking on it, or draw a card by clicking on the deck.
 * Actions are validated by the serverside (via the Client) - if invalid a message will
 * be displayed. Mouse/ Action events will do nothing if not the user's turn.
 * 
 * Game data is received by the Client from serverside and the appropriate methods in this
 * view called. Data is sent to the Client for transmission via a shared BlockingQueue.
 * 
 * The view adapts to the number of players, ensuring when 2 players they are always opposite
 * and when 3 or 4 that play appears clockwise for all players.
 * 
 * Players' hand is redrawn each turn, both to ensure correct placement and assign correct 
 * ids to each, as these are used to determine which card was played (e.g. id 3 means the player
 * wants to play the card at index 3 of their hand). Other players hands are only redrawn if 
 * they change size.
 * 
 * The gameview has no concept of game related classes, such as Card, Hand, Player or Deck,
 * acting simply upon basic String and int inputs. To do so, card images are loaded when
 * application started into a hashmap, with card names (which are the same as the png file names)
 * as keys and ImageViews as keys. This allows for efficient retrieval.
 * 
 * Once a game is ended, alert box gives user the option to play again (taking them back to
 * the choose game screen) or not (exiting the application).
 */
public class GameViewController {

  @FXML
  private Pane myCards;

  @FXML
  private Pane playerCards2;

  @FXML
  private Pane playerCards3;

  @FXML
  private Pane playerCards4;
  
  private Pane[] playerCardLocations;

  @FXML
  private ImageView pileCard;

  @FXML
  private Rectangle activeColour;

  @FXML
  private Label player1Label;

  @FXML
  private Label playerLabel2;

  @FXML
  private Label playerLabel3;

  @FXML
  private Label playerLabel4;
  
  private Label[] playerLabelLocations;


  @FXML
  private Label words;

  @FXML
  private Button uno;

  public enum Action {Skip, Draw2, Reverse};
  public enum Wild {Wild, WildDraw4};
  public enum Colour {Blue, Green, Red, Yellow};

  public static Map<String, Image> cardImages;
  public static Image back;
  public static Image backLeft;
  public static Image backRight;
  private static FXMLLoader loader;

  private int gameSize;
  private BlockingQueue<Object> toClient;
  private ArrayList<String> players = new ArrayList<>();
  private boolean player1Turn;
  private boolean sayUno;
  private boolean error = false;;


  public static void setupCardImages(String type) {
    cardImages = new HashMap<>();
    Image image;
    try {
      back = new Image(new FileInputStream( "src/" + type +"/Back.png"));
      backLeft = new Image(new FileInputStream( "src/" + type + "/BackLeft.png"));
      backRight = new Image(new FileInputStream( "src/" + type + "/BackRight.png"));
      for(Colour colour : Colour.values()){
        int counter = 0;
        while(counter <10) {
          image = new Image(new FileInputStream( "src/" + type + "/" +colour.name() + Integer.toString(counter) + ".png"));
          cardImages.put(colour.name() + Integer.toString(counter), image);
          counter++;
        }
        for(Card.Action action : Card.Action.values()) {
          image = new Image(new FileInputStream( "src/" + type + "/" +colour.name() + action + ".png"));
          cardImages.put(colour.name() + action.name(), image);
        }
        for(Card.Wild wild : Card.Wild.values()) {
          image = new Image(new FileInputStream( "src/" + type + "/" + wild + ".png"));
          cardImages.put(wild.name(), image);
        }
      }
    }catch(Exception e) {
      System.out.println(e);
    }
  }

  public void setGameSize(String gameSize) { // determines player positioning
	  this.gameSize = Integer.parseInt(gameSize);
  }
  
  public void addPlayer(String player) {
	 System.out.println("adding player " + player);
    players.add(player);
    if(players.size() == gameSize) {
    	setPlayers();
    }
  }
  
  public void setPlayers() {
	if(gameSize >2) { // ensure in 2 player game players are opposite
		playerCardLocations = new Pane[] {playerCards3, playerCards2, playerCards4};
		playerLabelLocations = new Label[] {playerLabel3, playerLabel2, playerLabel4}; 
	} else {
		playerCardLocations = new Pane[] {playerCards2};
		playerLabelLocations = new Label[] {playerLabel2};
	}
    while(players.indexOf("YOU") != 0) { // to ensure play looks clockwise
      players.add(players.remove(0)); // if before this player move to end of arraylist until player is in index 0   	
    }  
    players.remove(0); // this player not needed from list of players
    for(String p : players) {
      int playerNumber = players.indexOf(p);
      Platform.runLater(new Runnable(){
        @Override public void run() {
          playerLabelLocations[playerNumber].setText(p);
        }
    });
    }
  }

  public void updatePile(String card){
    Platform.runLater(new Runnable(){
      @Override public void run() {
        pileCard.setImage(cardImages.get(card));
      }
    });
  }

  public void updateActiveColour(String colour) {
    Platform.runLater(new Runnable(){
      @Override public void run() {
        activeColour.setFill(Paint.valueOf(colour));
      }
    });
  }

  public void draw(MouseEvent e) {
	if(error == true) {
		error();
		return;
	}
    if(player1Turn) {
      try {
        toClient.put(03);
        System.out.println("Request to draw card");
      } catch(Exception ex) {
        System.out.println(ex);
        error();
      }
    }
  }

  public void playCard(MouseEvent event){
	if(error == true) {
		error();
		return;
	}
    if(player1Turn) {
      try {
        if(sayUno == true) {
          toClient.put(02);
          System.out.println("Choice: 02");
        } else {
          toClient.put(01);
          System.out.println("Choice: 01");
        }
        toClient.put(Integer.parseInt(event.getPickResult().getIntersectedNode().getId()));
        System.out.println("card index: " +event.getPickResult().getIntersectedNode().getId());
        System.out.println("Sent to client for server transmission");
      } catch(Exception e) {
        System.out.println(e);
        error();
      }
    }
  }

  public void turnInput(String whoseTurn) {
	if(sayUno == true) {
      toggleSayUno();
    }
    if(whoseTurn.equals("YOU")) {
      player1Turn = true;
      Platform.runLater(new Runnable(){
        @Override public void run() {
          player1Label.setText("Your Turn");
          for(int i = 0; i< players.size(); i++) {
        	  playerLabelLocations[i].setText(players.get(i));
          }
        }
      });
    } else {
      player1Turn = false;
      int indexCurrentPlayer = players.indexOf(whoseTurn);
      Platform.runLater(new Runnable(){
        @Override public void run() {
          player1Label.setText(" ");
          for(int i = 0; i < players.size(); i++) {
        	  if(i == indexCurrentPlayer) {
        	    playerLabelLocations[i].setText(whoseTurn + "'s turn");
        	  } else {
        	  playerLabelLocations[i].setText(players.get(i));
        	  }
          }
        }
    });
    }
    resetHand();
  }

  // for redrawing hand
  public void resetHand() {
    Platform.runLater(new Runnable(){
      @Override public void run() {
        ObservableList<Node> cards = myCards.getChildren();
        cards.clear();
        System.out.println("Hand reset");
      }
    });
  }

  public void addToHand(String details) {
    Platform.runLater(new Runnable(){
      @Override public void run() {
    	  System.out.println(details);
        ImageView imageView = new ImageView();
        String description = details.split("-")[1];
        imageView.setImage(cardImages.get(description));
        imageView.setFitHeight(140.00);
        imageView.setFitWidth(100.00);
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> playCard(event));
        ObservableList<Node> cards = myCards.getChildren();
        imageView.setLayoutX(cards.size()* 60);
        imageView.setId(Integer.toString((cards.size())));
        cards.add(imageView);
        System.out.println("Image added... maybe");
      }
    });
  }

  // for displaying general gameplay messages
  public void show(String input) {
    Platform.runLater(new Runnable(){
      @Override public void run() {
        words.setText(input);
      }
    });
  }

  // showing how many cards other players have
  public void cardNo(String details) {
    String[] breakdown = details.split(" ");
    String player = breakdown[1];
    int number = Integer.parseInt(breakdown[2]);
    int playerIndex = players.indexOf(player);
    ObservableList<Node> cards = playerCardLocations[playerIndex].getChildren();
    if(cards.size() != number) { // only redraw if a change
    Platform.runLater(new Runnable(){
      @Override public void run() {
        String orientation = ""; // this has to be set within thread;
        if( playerIndex == 2) {
          orientation = "Right";
        } else if(playerIndex == 0 && players.size() > 1) {
          orientation = "Left";
        }
        updateCards(number, cards, orientation);
      }
    });
    }
  }

  // seperated out due to differences in orientation and spacing
  public void updateCards(int n, ObservableList<Node> cards, String orientation) {
    ImageView imageView = null;
    cards.clear();
    for(int i = 0; i < n; i++) {
      if(orientation != "") {
        if(orientation == "Left") {
          imageView = new ImageView(backLeft);
        } else {
          imageView = new ImageView(backRight);
        }
        imageView.setFitHeight(100.00);
        imageView.setFitWidth(140.00);
        imageView.setLayoutY(cards.size() * 60);
      } else {
        imageView = new ImageView(back);
        imageView.setRotate(180);
        imageView.setFitHeight(140.00);
        imageView.setFitWidth(100.00);
        imageView.setLayoutX(cards.size() * 60);
      }
      cards.add(imageView); 
    }
  }

  public void setToClient(BlockingQueue<Object> toClient) {
    this.toClient = toClient;
  }

  public void sayUno(ActionEvent e) {
    if(player1Turn) {
      toggleSayUno();
    }
  }

  public void toggleSayUno() { // Separated out so can be called outside of action events
    Platform.runLater(new Runnable(){
	  @Override public void run() {
	    if(sayUno == true) {
	      sayUno = false;
	      uno.setText("Say UNO");
	      uno.setStyle("");
	    } else {
	      sayUno = true;
	      uno.setText("Saying UNO!");
	      uno.setStyle("-fx-background-color: linear-gradient(#87CEEB, #1E90FF);\n" + 
	              		"    -fx-background-radius: 30;\n" + 
	              		"    -fx-background-insets: 0;\n" + 
	              		"    -fx-text-fill: white;\n" + 
	              		"}");        
	      }
	  }
    });   
  }

    // alert for when played a wild card
    public void chooseColour(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				int choose = -1;
				ButtonType blue = new ButtonType("blue");
				ButtonType red = new ButtonType("red");
				ButtonType yellow = new ButtonType("yellow");
				ButtonType green = new ButtonType("green");
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.initStyle(StageStyle.UTILITY);
				alert.setTitle("You Wild thing!!");
				alert.setHeaderText("You just played a wildcard");
				alert.setContentText("Choose a colour from the options below :");
				alert.getButtonTypes().setAll(blue,green,red,yellow);
				alert.showAndWait();

				if (alert.getResult() == red) {
					choose = 2;
					try {
						toClient.put(choose);
						System.out.println("Red Selected");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					alert.close();
				}
				if (alert.getResult() == blue) {
					choose = 0;
					try {
						toClient.put(choose);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					alert.close();
				}
				if (alert.getResult() == green) {
					choose = 1;
					try {
						toClient.put(choose);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					alert.close();
				}
				if (alert.getResult() == yellow) {
					choose = 3;
					try {
						toClient.put(choose);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					alert.close();
				}

			}
			});
	}

    // end of game: winner & points shown and asked if want to play again
    public void infoBox( String details) {
    String[] breakdown = details.split(" ");
	{

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ButtonType play = new ButtonType("Play again");
				ButtonType exit = new ButtonType("Exit the game");
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.getButtonTypes().setAll(play,exit);
//		alert.setTitle(titleBar);
		String headerMessage = "Game over";
		String infoMessage = breakdown[1] + " won with "+ breakdown[2] + " points!";
		if(breakdown[1].equals("YOU")){
		  	headerMessage = "Congratulations!";
		  	infoMessage = "You won with " + breakdown[2] + " points!";
		}
		alert.setHeaderText(headerMessage);
		alert.setContentText(infoMessage);
		reset();
		alert.showAndWait();
		if(alert.getResult()==play){
			try {
			toClient.put(99);	
			} catch(Exception e) {
				System.out.println(e);
				error();
			}
			  ChooseController c = Uno.getCController();
			  c.reset();
			  reset();
			  Scene choose = Uno.getChooseScene();
			  Stage primary = Uno.getPrimary();
	          primary.setScene(choose);
	          primary.centerOnScreen();
			try {
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.exit(0);
		}

			}
		});
    }
}
    
    public void error() {
    	error = true;
    	Platform.runLater(new Runnable(){
        	@Override public void run() {
        	  Alert alert = new Alert(Alert.AlertType.INFORMATION);
        	  alert.initStyle(StageStyle.UTILITY);
        	  alert.setTitle("Error");
        	  alert.setHeaderText("Connection Error.");
        	  alert.setContentText("Server connection lost. Please close the application and try later.");
              alert.showAndWait();
        	}
          });
        }

    public void reset() {
    	gameSize = 0;
    	for(String p: players) {
    		cardNo("Cardno " + p + " 0");
    	}
    	players.clear();
    	player1Turn = false;
    	sayUno = false;
    	resetHand();
    	player1Label.setText("");
    	playerLabel2.setText("");
    	playerLabel3.setText("");
    	playerLabel4.setText("");
    	words.setText("");
    }

  }