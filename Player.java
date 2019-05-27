import java.util.List;

public class Player{

  private String name;
  private int id;
  private String username;
  private String password;
  private int wins;
  private int loses;
  private Hand hand;
  private ClientHandler client;
  private static int idCounter = 1;

  public Player(String name, ClientHandler client){
    this.name = name;
    this.id = idCounter;
    this.client = client;
    idCounter++;
  }

  public Player(String name){ // historic, left in for compiling
    this.name = name;
    this.id = idCounter;
    idCounter++;
  }

  public Player(String name, String username, String password){
    this.name = name;
    this.username = username;
    this.password = password;
    this.id = idCounter;
    idCounter++;
  }

  public void setHand(Hand hand){
    this.hand = hand;
  }

  public void addCards(List<Card> cards){
    this.hand.addCards(cards);
  }

  public List<Card> getCards(){
    return this.hand.getCards();
  }

  public Hand getHand(){
    return hand;
  }
  
  public int getHandValue() {
	  return hand.getTotalValue();
  }

  public boolean hasNoCards(){
    return hand.empty();
  }

  public boolean unoCondition(){
    return hand.getNumberOfCards() == 1;
  }

  public Card getCardAt(int index){
    return hand.getCards().get(index);
  }

  public String getName(){
    return this.name;
  }

  public Card playCard(int index){
    return hand.playCard(index);
  }

  public int getID(){
    return this.id;
  }

  public boolean checkPassword(String password){
    return this.password == password;
  }

   public ClientHandler getClient(){
    return client;
  }

  public int getNumberOfCards(){
    return hand.getNumberOfCards();
  }

}
