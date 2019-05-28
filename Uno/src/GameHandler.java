import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * 
 * Runnable GameHandler class, than coordinates the flow of a single Game.
 * Containsa  Game instance and controls start of turns, validating user actions,
 * acting upon action or wild cards and checking for end of the game. 
 * 
 * Controls messages/ instructions sent to and received by players, using the player's
 * ClientHandler via blockingQueues
 * 
 * Once run, the methods continue in a loop until someone wins the game, at which point the 
 * thread ends.
 *
 */
public class GameHandler implements Runnable{

  public Game game;
  public boolean shown = false;

  public GameHandler(Game game){
    this.game = game;
  }

  @Override
  public void run(){
	announceStart();
    game.start();
    announcePlayers();
    newTurn();
  }

  //announcing current state of game to each player and whose turn it is
  public void newTurn(){
	if(game.hasWon()){
	  showStatus(game.getPileCard(), game.getActiveColour());
	  showPlayerCardNumbers();
	  showPlayersCards();
	  showWinnerAndSave();
	  return; 
	}  
    announceTurn(game.nextPlayer());
    if(game.getActiveColour() == "black"){
      wild();
    }
    showStatus(game.getPileCard(), game.getActiveColour());
    int drawPenalty = game.getDrawPenalty();
    if(drawPenalty > 0){
      showDrawPenalty(drawPenalty);
      draw(drawPenalty);
      game.setDrawPenalty(0);
    }
    showPlayerCardNumbers();
    showPlayersCards();
    if(game.getSkip() == true){
      showSkip();
      game.skipped();
      newTurn();
    } else {
      newChoice(game.getCurrentPlayer());
    }
  }

  public void newChoice(Player currentPlayer){
    int choice = showOptions(currentPlayer);
    action(choice);
  }

  public void action(int choice){
    int secondChoice = 0;
    if(choice == 1 || choice == 2){
      System.out.println("playing a card" + choice);
      secondChoice = chooseCard(game.getCurrentPlayer());
      playCard(game.getCurrentPlayer().getCardAt(secondChoice), secondChoice);
      System.out.println(secondChoice);
      if(game.unoCondition() == true && choice != 2){
        showNoUno();
        draw(2);
      } else if(game.unoCondition() == false && choice == 2){
        stopCryingWolf();
      }
  }
  if(choice == 3){
	  System.out.println(choice + " draw");
    draw(1);
  }

    // if(choice == 4){
    //   String quit = View.confirmQuit();
    //   if(quit.contains("Y")){
    //     if(game.getOtherPlayers().size() == 1){
    //       View.tooManyQuiters(game.getOtherPlayers().get(0));
    //       endGame();
    //     }
    //     game.playerQuits();
    //   } else{
    //     newChoice();
    //   }
    // }
  newTurn();
}

  
// retrieving card player has chosen to play, checking if valid and any special effects.
// if not valid, message sent to player informing them
public void playCard(Card card, int index){
  BlockingQueue messages = game.getCurrentPlayer().getClient().getInputQueue();
  if(card.getWild() == "WildDraw4"){
    if(checkNoColorMatch()){
      game.setDrawPenalty(4);
      game.skipTurn();
    } else{
      try{
        messages.put("Can only play Wild Draw 4 if unable to play another card.");
      } catch(Exception e){
        System.out.println(e);
      }
      newChoice(game.getCurrentPlayer());
    }
  }
  if(card.getColour() == "black" || card.getColour() == game.getActiveColour() ||
    card.getNumber() == game.getActiveNumber() && card.getNumber() != -10 ||
    card.getAction() != "none" && card.getAction() == game.getPileCard().getAction()){
    game.addToPile(game.getCurrentPlayer().playCard(index));
} else {
  try{
    messages.put("Invalid choice, does not match.");
  } catch(Exception e){
    System.out.println(e);
  }
  newChoice(game.getCurrentPlayer());
}
if(card.getAction() == "Skip"){
  game.skipTurn();
}
if(card.getAction() == "Draw2"){
  game.setDrawPenalty(2);
}
if(card.getAction() == "Reverse"){
  game.reverse();
}
announceCardPlayed(card, game.getCurrentPlayer());
if(card.getWild() == "Wild" || card.getWild() ==  "WildDraw4"){
  wild();
}
}

// for if wild card played - player to choose new active colour
public int getColourChoice(){
  Player current = game.getCurrentPlayer();
  BlockingQueue messages = current.getClient().getInputQueue();
  BlockingQueue receive = current.getClient().getOutputStream();
  int choice = -1;
  try{
    messages.put("COLOUR");
    while(choice < 0){
      choice = (int)receive.take();
    }
  }catch(Exception e){
    System.out.println(e);
  }
  return choice;
}

// telling all players the latest card played
public void announceCardPlayed(Card card, Player player){
  BlockingQueue messages;
  try{
    for(Player p : game.getAllPlayers()){
      messages = p.getClient().getInputQueue();
      if(p == player){
        messages.put("You played " + card);
      } else{
        messages.put(player.getName() + " played " + card);
      }
    }
  } catch(InterruptedException e){
    System.out.println(e);
  }
}

// checking if can play Wild Draw 4 card: only if cannot play any matching colour cards
public boolean checkNoColorMatch(){
  List<Card> hand = game.getCurrentPlayer().getCards();
  for(Card c : hand){
    if(c.getColour() == game.getActiveColour()){
      return false;
    }
  }
  return true;
}


public int chooseCard(Player player){
  BlockingQueue messages = player.getClient().getInputQueue();
  BlockingQueue receive = player.getClient().getOutputStream();
  List<Card> cards = player.getCards();
  int choice = -1;
  try{
    while(choice < 0){
      choice = (int)receive.take();
    }
  } catch(Exception e){
    System.out.println(e);
  }
    return choice; // -1 as indexed from 0
  }

  public int showOptions(Player currentPlayer){
    BlockingQueue messages = currentPlayer.getClient().getInputQueue();
    BlockingQueue receive = currentPlayer.getClient().getOutputStream();
    int choice = -1;
    try{
      messages.put("ACTION");
      while(choice < 0){
        choice = (int)receive.take();
        System.out.println(choice);
      }
    }catch(Exception e){
      System.out.println(e);
    }
    return choice;
  }

  public void announceStart() {
	BlockingQueue messages;
	List<Player> players = game.getAllPlayers();
	try{
	  for(Player p : players){
	    messages = p.getClient().getInputQueue();
	    messages.put("RUMBLE");
	  }
	} catch(Exception e){
	      System.out.println(e);
	    }
	}
  
  public void announcePlayers(){
    BlockingQueue messages;
    List<Player> players = game.getAllPlayers();
    try{
      for(Player p : players){
        messages = p.getClient().getInputQueue();
        messages.put("GAMESIZE: " + players.size());
        for(Player p2 : players){
          if(p.getClient() == p2.getClient()){
        	messages.put("PLAYER: YOU");
          }else {
            messages.put("PLAYER " + p2.getName());
          }
        }
      }
    } catch(Exception e){
      System.out.println(e);
    }
  }

  public void announceTurn(Player player){
    BlockingQueue messages;
    try{
      for(Player p : game.getAllPlayers()){
        messages = p.getClient().getInputQueue();
        if(p == player){
          messages.put("TURN YOU");
        } else{
          messages.put("TURN " + player.getName());
        }
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void showStatus(Card pileCard, String activeColour){
    BlockingQueue messages;
    try{
      for(Player p : game.getAllPlayers()){
        messages = p.getClient().getInputQueue();
        messages.put("PILE " + pileCard);
        messages.put("ACOLOUR " + activeColour);
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void showPlayerCardNumbers(){
    BlockingQueue messages;
    try{
      for(Player p : game.getAllPlayers()){
        for(Player p2 : game.getAllPlayers()){
          if(p != p2){
            messages = p2.getClient().getInputQueue();
            messages.put("CARDNO " + p.getName() + " " + p.getNumberOfCards());
          }
        }
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void showDrawPenalty(int cards){
    BlockingQueue messages;
    Player current = game.getCurrentPlayer();
    try{
      for(Player p : game.getAllPlayers()){
        messages = p.getClient().getInputQueue();
        if(p == current){
          messages.put("A draw card was played, so you must draw " + cards + " cards!");
        } else{
          messages.put("A draw card was played, so " + current.getName() +
           " must draw " + cards + " cards!");
        }
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void draw(int cards){
    int deckRemaining = game.deckRemaining();
    // if(cards > deckRemaining){
    //   List<Card> draw = game.draw(deckRemaining);
    //   View.showDraw(draw);
    //   View.pileToDeck();
    //   game.pileToDeck();
    //   draw(cards - deckRemaining);
    // } else {
    List<Card> draw = game.draw(cards);
    showDraw(draw);
    //}
  }

  public void showDraw(List<Card> cards){
    BlockingQueue messages;
    Player current = game.getCurrentPlayer();
    try{
      for(Player p : game.getOtherPlayers()){
        messages = p.getClient().getInputQueue();
        messages.put(current.getName() + " drew " + cards.size());
      }
      for(Card c : cards){
        messages = current.getClient().getInputQueue();
        messages.put("You drew: " + c);
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void showSkip(){
    BlockingQueue messages;
    Player current = game.getCurrentPlayer();
    try{
      for(Player p : game.getAllPlayers()){
        messages = p.getClient().getInputQueue();
        if(p == current){
          messages.put("Skip card was played so you lose your turn.");
        } else{
          messages.put("Skip card was played so no turn for " + current.getName());
        }
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void showNoUno(){
    BlockingQueue messages;
    Player current = game.getCurrentPlayer();
    try{
      for(Player p : game.getAllPlayers()){
        messages = p.getClient().getInputQueue();
        if(p == current){
          messages.put("You didn't say Uno so have to draw 2 cards!");
        } else{
          messages.put(current.getName() + " didn't say Uno so has to draw 2 cards!");
        }
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void stopCryingWolf(){
    Player current = game.getCurrentPlayer();
    BlockingQueue messages = current.getClient().getInputQueue();
    try{
      messages.put("Why are you saying Uno? Crying wolf will be punished... maybe.");
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void showPlayersCards(){
    BlockingQueue messages;
    List<Card> cards;
    Player current = game.getCurrentPlayer();
    try{
      for(Player p : game.getAllPlayers()){
        messages = p.getClient().getInputQueue();
        cards = p.getCards();
        for(Card c: cards){
          messages.put("CARD-" + c.toString());
        }
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
  }

  public void wild(){
    int newColour = getColourChoice();
    game.wildResult(newColour);
  }

  public void showWinnerAndSave(){
    if(shown == false) {
      shown = true;
      BlockingQueue messages;
      Database db = new Database();
      Player current = game.getCurrentPlayer();
      try{
        for(Player p : game.getAllPlayers()){
          messages = p.getClient().getInputQueue();
          int score = game.getFinalScore();
          if(p == current){
            messages.put("WON");
            db.addPointsData(p.getName(), 1, 0, score);
            messages.put("END YOU " + score);
          } else{
           // messages.put(current.getName() + " won the game with " + score + " points!");
            db.addPointsData(p.getName(), 0, 1, 0);
            messages.put("END " + current.getName() + " " + score);
          }
      }
    } catch(InterruptedException e){
      System.out.println(e);
    }
    return;
  }
  }
 
}



