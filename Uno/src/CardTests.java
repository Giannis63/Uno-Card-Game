import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

	public class CardTests {
		private Card greenSix;
		private Card blueSix;
		private Card redTwo;
		private Card wild;
		private Card wild_Draw4;
		private Card blueSkip;
		private Card redDraw2;
		private Card greenReverse;

		// Colour & number card
		 @Test
		  public void InitializeTest1() {
			Card colourNum = new Card(Card.Colour.Blue, 0);
			String expected = "Blue";
			String actual = colourNum.getColour();
		    assertEquals(expected, actual);
		    int expectedN = 0;
		    int actualN = colourNum.getNumber();
		    assertEquals(expectedN, actualN);
		    expected = "none";
		    actual = colourNum.getAction();
		    assertEquals(expected, actual);
		    expected = null;
		    actual = colourNum.getWild();
		    assertEquals(expected, actual);
		    actualN = colourNum.getValue();
		    assertEquals(expected, actual);
		  }
		 
		 // Colour & action card
		 @Test
		  public void InitializeTest2() {
			Card colourAction = new Card(Card.Colour.Red, Card.Action.Draw2);
			String expected = "Red";
			String actual = colourAction.getColour();
		    assertEquals(expected, actual);
		    int expectedN = -10;
		    int actualN = colourAction.getNumber();
		    assertEquals(expectedN, actualN);
		    expected = "Draw2";
		    actual = colourAction.getAction();
		    assertEquals(expected, actual);
		    expected = null;
		    actual = colourAction.getWild();
		    assertEquals(expected, actual);
		    expectedN = 20;
		    actualN = colourAction.getValue();
		    assertEquals(expected, actual);
		  }
		 
		 // Wild card
		 @Test
		  public void InitializeTest3() {
			Card wild = new Card(Card.Wild.WildDraw4);
			String expected = "black";
			String actual = wild.getColour();
		    assertEquals(expected, actual);
		    int expectedN = -10;
		    int actualN = wild.getNumber();
		    assertEquals(expectedN, actualN);
		    expected = "none";
		    actual = wild.getAction();
		    assertEquals(expected, actual);
		    expected = "WildDraw4";
		    actual = wild.getWild();
		    assertEquals(expected, actual);
		    expectedN = 50;
		    actualN = wild.getValue();
		    assertEquals(expected, actual);
		  }
		 
		 @Test
		  public void InitializeTest4() {
			Card wild = new Card(Card.Wild.WildDraw4);
			String expected = "black";
			String actual = wild.getColour();
		    assertEquals(expected, actual);
		    int expectedN = -10;
		    int actualN = wild.getNumber();
		    assertEquals(expectedN, actualN);
		    expected = "none";
		    actual = wild.getAction();
		    assertEquals(expected, actual);
		    expected = "WildDraw4";
		    actual = wild.getWild();
		    assertEquals(expected, actual);
		    expectedN = 50;
		    actualN = wild.getValue();
		    assertEquals(expected, actual);
		  }
		 
		 // testing card a valid match
		 
	
		@Test
		public void testGetColour() {
			greenSix = new Card (Card.Colour.Green, 6);
			blueSix = new Card (Card.Colour.Blue, 6);
			wild = new Card (Card.Wild.Wild);
			wild_Draw4 = new Card (Card.Wild.WildDraw4);
			blueSkip = new Card(Card.Colour.Blue, Card.Action.Skip);
			redDraw2 = new Card(Card.Colour.Red, Card.Action.Draw2);
			greenReverse = new Card (Card.Colour.Green, Card.Action.Reverse);
			assertEquals("black", wild.getColour());
			assertEquals("black", wild_Draw4.getColour());
			assertEquals("Blue", blueSkip.getColour());
			assertEquals("Red", redDraw2.getColour());
			assertEquals("Green", greenReverse.getColour());
		}

		@Test
		public void testGetAction() {
			greenSix = new Card (Card.Colour.Green, 6);
			blueSix = new Card (Card.Colour.Blue, 6);
			wild = new Card (Card.Wild.Wild);
			wild_Draw4 = new Card (Card.Wild.WildDraw4);
			blueSkip = new Card(Card.Colour.Blue, Card.Action.Skip);
			redDraw2 = new Card(Card.Colour.Red, Card.Action.Draw2);
			greenReverse = new Card (Card.Colour.Green, Card.Action.Reverse);
			assertEquals("none", greenSix.getAction());
			assertEquals("none", wild.getAction());
			assertEquals("none", wild_Draw4.getAction());
			assertEquals("Skip", blueSkip.getAction());
			assertEquals("Draw2", redDraw2.getAction());
			assertEquals("Reverse", greenReverse.getAction());
		}

		@Test
		public void testGetWild() {
			greenSix = new Card (Card.Colour.Green, 6);
			blueSix = new Card (Card.Colour.Blue, 6);
			wild = new Card (Card.Wild.Wild);
			wild_Draw4 = new Card (Card.Wild.WildDraw4);
			blueSkip = new Card(Card.Colour.Blue, Card.Action.Skip);
			redDraw2 = new Card(Card.Colour.Red, Card.Action.Draw2);
			greenReverse = new Card (Card.Colour.Green, Card.Action.Reverse);
		    assertEquals(null, greenSix.getWild());
			assertEquals("Wild", wild.getWild());
			assertEquals("WildDraw4", wild_Draw4.getWild());
			assertEquals(null, blueSkip.getWild());
			assertEquals(null, redDraw2.getWild());
			assertEquals(null, greenReverse.getWild());
		}

		@Test
		public void testToString() {
			greenSix = new Card (Card.Colour.Green, 6);
			blueSix = new Card (Card.Colour.Blue, 6);
			wild = new Card (Card.Wild.Wild);
			wild_Draw4 = new Card (Card.Wild.WildDraw4);
			blueSkip = new Card(Card.Colour.Blue, Card.Action.Skip);
			redDraw2 = new Card(Card.Colour.Red, Card.Action.Draw2);
			greenReverse = new Card (Card.Colour.Green, Card.Action.Reverse);
			assertEquals("Wild", wild.toString());
			assertEquals("WildDraw4", wild_Draw4.toString());
			assertEquals("Green6", greenSix.toString());
			assertEquals("BlueSkip", blueSkip.toString());
			assertEquals("RedDraw2", redDraw2.toString());
			assertEquals("GreenReverse", greenReverse.toString());

		}

	}