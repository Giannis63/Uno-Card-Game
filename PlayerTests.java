import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PlayerTests {
	private Player player1;
	private Player player2;
	private Player player3;
	private Player player4;
	
	private String p1;
	private String p2;
	private String p3;
	private String p4;
	
	private String username1;
	private String password1;

	
	private Card blueOne;
	private Card greenOne;
	private Card wild;
	private LinkedList<Card> cards1;
	private LinkedList<Card> cards2;
	private Hand hand1;
	private Hand hand2;

	@Before
	public void setUp() throws Exception {
		player1 = new Player(p1);
		player2 = new Player(p2);
		player3 = new Player(p3);
		player4 = new Player(p4);
		
		blueOne = new Card (Card.Colour.Blue, 1);
		greenOne = new Card (Card.Colour.Green, 1);
		wild = new Card (Card.Wild.Wild);

		cards1 = new LinkedList<Card>();
		cards1.add(blueOne);
		cards1.add(greenOne);
		cards1.add(wild);
		
		cards2 = new LinkedList<Card>();
		cards2.add(blueOne);
		
		hand1 = new Hand();
		hand2 = new Hand();
		
	}

	@Test
	public void testSetAndGetCards() {
		hand1.addCards(cards1);
		player1.setHand(hand1);
		assertEquals("Error in setHand()",3, player1.getNumberOfCards());
		List<Card> cards = player1.getCards();
		List<Card> cards2 = hand1.getCards();
		assertEquals(cards, cards2);
	}

	@Test
	public void testAddCards() {
		hand1.addCards(cards1);
		player1.setHand(hand1);
		player1.addCards(cards2);
		assertEquals("Error in addCards()",4, player1.getNumberOfCards());
	}


	@Test
	public void testUnoCondition() {
		hand2.addCards(cards2);
		player1.setHand(hand2);
		assertEquals("Error in unoCondition()",true, player1.unoCondition());
	}
	
	@Test
	public void testHasNoCards() {
	  Hand hand = new Hand();
	  player1.setHand(hand);
      assertTrue(player1.hasNoCards());
      player1.addCards(cards2);
      assertFalse(player1.hasNoCards());
	}
	
	@Test
	public void testTotalValue() {
	  hand2.addCards(cards2);
      player1.setHand(hand2);
	  System.out.println(player1.getHandValue());
	  assertEquals("Error in total value()", 1, player1.getHandValue());
	}
	
	@Test
	public void testGetCardAt() {
	  hand2.addCards(cards2);
	  player1.setHand(hand2);
	  Card actual = player1.getCardAt(0);
	  Card expected = new Card(Card.Colour.Blue, 1);
	  assertEquals(actual, expected);
	}

	@Test
	public void testPlayCard() {
      hand2.addCards(cards2);
      hand2.addCards(cards2);
	  player1.setHand(hand2);
	  List<Card> cards = player1.getCards();
	  int original = cards.size();
	  Card actual = player1.playCard(0);
	  Card expected = new Card(Card.Colour.Blue, 1);
	  assertEquals(actual, expected);
	  List<Card> cards1 = player1.getCards();
	  int now = cards1.size();
	  assertEquals(original -1, now);
	}

}
