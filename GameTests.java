import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GameTests {
	
	 @Test
		public void gameCreationTest1() {
			Game game = new Game(4);
			boolean expected = true;
			boolean actual = game.acceptPlayers();
			assertEquals(expected, actual);
			Deck gDeck = game.getDeck();
			int expectedN = 108;
			int actualN = gDeck.cardsLeft();
			assertEquals(expected, actual);
		}
	 

	 @Test
		public void gameCreationTest2() {
			Game game = new Game(4);
			boolean expected = true;
			boolean actual = game.acceptPlayers();
			assertEquals(expected, actual);
			Deck gDeck = game.getDeck();
			int expectedN = 216;
			int actualN = gDeck.cardsLeft();
			assertEquals(expected, actual);
		}
	 
	 @Test
	   public void addPlayersTest() {
         Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Player p3 = new Player("Sam");
         Player p4 = new Player("Kim");
         Game game = new Game(4);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.addPlayer(p3);
         game.addPlayer(p4);
         List<Player> expected = new ArrayList<>();
         expected.add(p1);
         expected.add(p2);
         expected.add(p3);
         expected.add(p4);
         List<Player> actual = game.getAllPlayers();
         assertEquals(actual, expected);  
         assertFalse(game.acceptPlayers());
	 }
	 
	 @Test
	 public void startTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Player p3 = new Player("Sam");
         Player p4 = new Player("Kim");
         Game game = new Game(4);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.addPlayer(p3);
         game.addPlayer(p4);
         game.start();
         int expected = 7;
         int actual = p1.getCards().size();
         assertEquals(expected, actual);
         Card pile = game.getPileCard();
         assertEquals(pile.getColour(), game.getActiveColour());
         assertTrue(game.getStarted()); 
         assertEquals(game.deckRemaining(), 79);
	 }
	 
	 @Test
	 public void nextPlayerTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Player p3 = new Player("Sam");
         Player p4 = new Player("Kim");
         Game game = new Game(4);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.addPlayer(p3);
         game.addPlayer(p4);
         game.start();
         Player p = game.getCurrentPlayer();
         Player p5 = game.nextPlayer();
         assertNotSame(p, p5);
         Player p6 = p5;
         p5 = game.nextPlayer();
         assertNotSame(p5, p6);
	 }
	 
	 @Test
	 public void drawTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Game game = new Game(2);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.start();
         List<Card> draw = game.draw(2);
         assertEquals(draw.size(), 2);
         Player active = game.getCurrentPlayer();
         assertTrue(active.getCards().containsAll(draw));
	 }
	 
	 @Test 
	 public void drawPenaltyTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Game game = new Game(2);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.start();
         game.setDrawPenalty(2);
         int actual = game.getDrawPenalty();
         assertEquals(actual, 2);
	 }
	 
	 @Test
	 public void skipTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Game game = new Game(2);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.start();
         game.skipTurn();
         assertTrue(game.getSkip());
         game.skipped();
         assertFalse(game.getSkip());
	 }
	 
	 @Test 
	 public void hasWonTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Game game = new Game(2);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.start();
         assertFalse(game.hasWon());
         Player p = game.getCurrentPlayer();
         p.setHand(new Hand());
         assertTrue(game.hasWon());
	 }
	 
	 @Test
	 public void gettingPlayersTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Player p3 = new Player("Sarah");
         Game game = new Game(3);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.addPlayer(p3);
         game.start();
         Player p = game.getCurrentPlayer();
         List<Player> others = game.getOtherPlayers();
         List<Player> all = game.getAllPlayers();
         assertTrue(all.contains(p));
         assertTrue(all.contains(p1));
         assertTrue(all.contains(p2));
         assertTrue(all.contains(p3));
         assertFalse(others.contains(p));
	 }
	 
	 @Test
	 public void unoConditionTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Player p3 = new Player("Sarah");
         Game game = new Game(3);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.addPlayer(p3);
         game.start();
         assertFalse(game.unoCondition());
         Player p = game.getCurrentPlayer();
         Hand h = new Hand();
         List<Card> card = new ArrayList<>();
         card.add(new Card(Card.Colour.Blue, 7));
         h.addCards(card);
         p.setHand(h);
         assertTrue(game.unoCondition());
	 }
	 
	 @Test
	 public void addToPileTest() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Player p3 = new Player("Sarah");
         Game game = new Game(3);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.addPlayer(p3);
         game.start();
         Card c = new Card(Card.Colour.Blue, 7);
         game.addToPile(c);
         assertEquals(game.getActiveColour(), "Blue");
         assertEquals(game.getActiveNumber(), 7);
	 }
	 
	 @Test
	 public void wildResult() {
		 Player p1 = new Player("Bill");
         Player p2 = new Player("Bob");
         Player p3 = new Player("Sarah");
         Game game = new Game(3);
         game.addPlayer(p1);
         game.addPlayer(p2);
         game.addPlayer(p3);
         game.start();
         game.wildResult(0);
         assertEquals(game.getActiveColour(), "Blue");
	 }
	 
}

	 
