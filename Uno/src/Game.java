// this is effectively the controller (server side)
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;


public class Game{

  private ArrayList<Player> players;
  private boolean started;
  private Deck deck;
  private LinkedList<Card> pile;
  private String activeColour; // if a wild is player this is not the same as latest pile card
  private Player currentPlayer;
  public boolean skip;
  public int maxPlayers;
  public int drawPenalty = 0;
  public static Comparator<Player> playersComp = Comparator.comparing(Player::getID);

  public Game(int numberOfPlayers){
    maxPlayers = numberOfPlayers;
    deck = new Deck(numberOfPlayers);
    pile = new LinkedList<Card>();
    players = new ArrayList<Player>();
  }

  public void start(){
    Collections.sort(players, playersComp);
    startingHands();
    addToPile(deck.draw(1).get(0));
    currentPlayer = players.get((int) Math.random() * players.size());
    started = true;
  }

  public boolean getStarted(){
    return started;
  }

  public void startingHands(){
    Hand newHand;
    for(Player player : players){
      newHand = new Hand();
      newHand.addCards(deck.draw(7));
      player.setHand(newHand);
    }
  }

  public int deckRemaining(){
    return deck.cardsLeft();
  }

  public void pileToDeck(){
    Card topPile = pile.removeLast();
    this.deck = new Deck(pile);
    this.pile.clear();
    this.pile.add(topPile);
  }

  public Player nextPlayer(){
    int increment = 1;
    currentPlayer = players.get((players.indexOf(currentPlayer) + increment) % players.size());
    return currentPlayer;
  }

  public List<Card> draw(int cards){
    List<Card> draw = deck.draw(cards);
    currentPlayer.addCards(draw);
    return draw;
  }

  public int getDrawPenalty(){
    return drawPenalty;
  }

  public boolean getSkip(){
    return skip;
  }

  public void reverse(){
    if(players.size() == 2){ // if only two players reverse acts as a skip
      skipTurn();
      return;
    }
    Collections.reverse(players);
  }

  public void setDrawPenalty(int cards){
    drawPenalty = cards;
  }

  public void addPlayer(Player player){
    players.add(player);
  }

  public void removePlayer(Player player){
    players.remove(player);
  }

  public Deck getDeck(){
    return deck;
  }

  public void skipTurn(){
    this.skip = true;
  }

  public void skipped(){
    this.skip = false;
  }

  public Card getPileCard(){
    return pile.getLast();
  }

  public String getActiveColour(){
    return activeColour;
  }

  public boolean hasWon(){
    return currentPlayer.hasNoCards();
  }

  public int getActiveNumber(){
    return getPileCard().getNumber();
  }

  public Player getCurrentPlayer(){
    return currentPlayer;
  }

  public boolean unoCondition(){
    return currentPlayer.unoCondition();
  }

  public List<Player> getOtherPlayers(){
    ArrayList<Player> otherPlayers = new ArrayList<>(players);
    otherPlayers.remove(currentPlayer);
    Collections.sort(otherPlayers, playersComp);
    return otherPlayers;
  }

  public void addToPile(Card card){
    pile.add(card);
    activeColour = card.getColour();
  }

  public void wildResult(int index){
    activeColour = Card.Colour.values()[index].name();
  }

  public void playerQuits(){
    players.remove(currentPlayer);
  }

  public boolean acceptPlayers(){
    return players.size() < maxPlayers;
  }

  public List<Player> getAllPlayers(){
    return players;
  }
  
  public int getFinalScore() {
	int total = 0;
	for(Player p : players) {
      total+= p.getHandValue();
	}
	  return total;
  }
}