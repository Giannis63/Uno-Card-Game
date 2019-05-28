import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

public class DeckTests {	

	@Test
	public void testCardNumbers1() {
		Deck deck = new Deck(4);
		int expected = 108;
		int actual = deck.cardsLeft();
		assertEquals(expected, actual);
		deck.draw(10);
		expected = 98;
		actual = deck.cardsLeft();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCardNumbers2() {
		Deck deck = new Deck(8);
		int expected = 216;
		int actual = deck.cardsLeft();
		assertEquals(expected, actual);
		deck.draw(10);
		expected = 206;
		actual = deck.cardsLeft();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDraw1() {
		Deck deck = new Deck(4);
		List<Card> cards = deck.draw(108);
		cards.sort(Deck.comp);
		Card expected = new Card(Card.Colour.Blue, 8);
		Card actual = cards.get(17);
		assertEquals(expected, actual);
		expected = new Card(Card.Wild.WildDraw4);
		actual = cards.get(107);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDraw2() {
		Deck deck = new Deck(4);
		List<Card> cards = deck.draw(10);
		int expected = 10;
		int actual = cards.size();
		assertEquals(expected, actual);
		expected = 1;
		cards = deck.draw(1);
		actual = cards.size();
		assertEquals(expected, actual);
	}
	
	@Test
	public void recreateDeck() {
		Deck deck = new Deck(4);
		List<Card> cards = deck.draw(10);
		int expected = 10;
		int actual = cards.size();
		assertEquals(expected, actual);
		Deck deck1 = new Deck(cards);
	    actual = deck1.cardsLeft();
	    assertEquals(expected, actual);
	}
	
	
}

