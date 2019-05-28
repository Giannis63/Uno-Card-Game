import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Deck{

  private LinkedList<Card> cards;
  private int setupCounter = 0; // index of next card
  public static  Comparator<Card> comp = Comparator.comparing(Card::getColour).
                                        thenComparing(Card::getNumber);


  public Deck(int numberOfPlayers) {
    Card[] deck;
    if(numberOfPlayers <5){
      deck = new Card[108]; //faster to add to an array
      deck = setup(deck);
    } else {
      deck = new Card[216]; // double sized deck if more than 5 players
      deck = setup(deck);
      deck = setup(deck);
    }
    cards = new LinkedList(Arrays.asList(deck));
    Collections.shuffle(cards); // multiple shuffles to increase randomness
    Collections.shuffle(cards);
    Collections.shuffle(cards);
    // for(Card c : cards){
    //    System.out.println(c);
    //  }
  }

   public Card[] setup(Card[] deck){
    for(Card.Colour colour : Card.Colour.values()){
      deck[setupCounter] = new Card(colour, 0);
      setupCounter++;
      for(int i = 1; i < 10; i++){
        deck[setupCounter] = new Card(colour, i);
        deck[setupCounter+1] = new Card(colour, i);
        setupCounter = setupCounter + 2;
      }
      for(Card.Action action : Card.Action.values()){
        deck[setupCounter] = new Card(colour, action);
        deck[setupCounter + 1] = new Card(colour, action);
        setupCounter = setupCounter + 2;
      }
    }

    for(Card.Wild wild : Card.Wild.values()){
      for(int l = 0; l < 4; l++){
        deck[setupCounter] = new Card(wild);
        setupCounter++;
      }
    }
    return deck;
  }

   // for if deck runs out
  public Deck(List<Card> pile){
    this.cards = (LinkedList<Card>) pile;
    Collections.shuffle(cards);
    Collections.shuffle(cards);
  }

  public LinkedList<Card> draw(int number){
    LinkedList<Card> draw = new LinkedList<>();
    for(int i = 0; i < number; i++){
      if(cards.size() > 0){
        draw.add(this.cards.remove());
      }
    }
    return draw;
  }

  public int cardsLeft(){
    return cards.size();
  }


  public static void main(String[] args){
    // Deck deck = new Deck();
    // for(Card c : deck.cards){
    //   System.out.println(c);
    // }
    // System.out.println(deck.cards.size());
    // System.out.println(deck.draw(12));
    // System.out.println(deck.cards.size());

  }
}
