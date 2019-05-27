import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class HandTests {
	private Card blueOne;
	private Card greenOne;
	private Card wild;
	private LinkedList<Card> cards1;
	private Hand hand1;
	
	@Before
	public void setUp() throws Exception {
		blueOne = new Card (Card.Colour.Blue, 1);
		greenOne = new Card (Card.Colour.Green, 1);
		wild = new Card (Card.Wild.Wild);

		cards1 = new LinkedList<Card>();
		cards1.add(blueOne);
		cards1.add(greenOne);
		cards1.add(wild);
		
		hand1 = new Hand();
	}

	@Test
	public void testAddCards() {
		hand1.addCards(cards1);
		assertEquals("Error in addCards()", 3, hand1.getNumberOfCards());
	}

	@Test
	public void testPlayCard() {
		hand1.addCards(cards1);
		hand1.playCard(0);
		assertEquals("Error in playCard()", 2, hand1.getNumberOfCards());;
	}

	@Test
	public void testGetNumberOfCards() {
		hand1.addCards(cards1);
		assertEquals("Error in getNumberOfCards()", 3, hand1.getNumberOfCards());
	}


	@Test
	public void testEmpty() {
		hand1.addCards(cards1);
		assertEquals("Error in empty()", false, hand1.empty());
	}
	
	@Test
	public void testTotalValue() {
		hand1.addCards(cards1);
		System.out.println(hand1.getTotalValue());
		assertEquals("Error in total value()", 52, hand1.getTotalValue());
	}

}