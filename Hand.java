import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

public class Hand{

private LinkedList<Card> hand;

  public Hand(){
    this.hand = new LinkedList<Card>();
  }

  public void addCards(List<Card> cards){
    hand.addAll(cards);
    Collections.sort(hand, Deck.comp);
  }

  public Card playCard(int index){
    return hand.remove(index);
  }

  public int getNumberOfCards(){
    return this.hand.size();
  }

  public List<Card> getCards(){
    Collections.sort(hand, Deck.comp);
    return this.hand;
  }

  public boolean empty(){
    return hand.isEmpty();
  }
  
  public int getTotalValue() {
    int total = 0;
	for(Card c : hand) {
	  total += c.getValue();  	  
	}
	return total;
  }
}
