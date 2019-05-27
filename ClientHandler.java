// server side

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Runnable ClientHandler class for coordinating communication between serverside and clientside.
 * Sends/ receives data from clientside (Client object), and communicates with serverside 
 * using two blockingQueues. 
 * 
 * When first run, enforces that user logs in or registers. When login/registration request
 * received, calls its Database instance, which in turn validates/ inserts with PostgreSQL
 * database.
 * 
 * 
 *
 */
public class ClientHandler implements Runnable{

  final DataInputStream in;
  final PrintWriter out;
  final Socket socket;
  private BlockingQueue<Object> inputQueue;
  private BlockingQueue<Object> outputQueue;
  private Player player = null;
  private Database db; 
  private String username;

  public ClientHandler(Socket socket, DataInputStream inputFromClient, PrintWriter outputToClient){
    this.in = inputFromClient;
    this.out = outputToClient;
    this.socket = socket;
    inputQueue = new LinkedBlockingDeque<>();
    outputQueue = new LinkedBlockingDeque<>();
    db = new Database();
  }

  @Override
  public void run(){
    while(player == null){ // forces user to login/ register before can access main application
      try{
    	System.out.println("requesting login");
        out.println("LOGIN");
        String type = in.readUTF();
        System.out.println(type);
        username = in.readUTF();
        System.out.println(username);
        String pass = in.readUTF();
        System.out.println(pass);
        if(type.equals("LOGIN")) {
          System.out.println("Received login attempt");
          if(db.existingUser(username, pass)){
        	 out.println("LOGGEDIN");
             player = new Player(username, this);
             System.out.println("Player: " + player.getName());
          } else {
             System.out.println("invalid");	
             out.println("INVALID");
          }
        } else if(type.equals("REGISTER")) {
        	System.out.println("Received login attempt");
        	if(db.newUser(username, pass)){ 
            	out.println("REGISTERED");
                player = new Player(username, this);
                System.out.println("Player: " + player.getName());
            } else {
              out.println("TAKEN");
            }
        }
      } catch(IOException e){
        System.out.println("Communication error");
        return;
      }
    }
	showLeaderBoard();
    chooseGame(); // informs clientside to go to Choose game screen
    
    while(true){ // listening for input to act upon, from client or game
      try{
        if(in.available() >0){
          System.out.println("Reading from client");
          int received = in.readInt();
          if(received == 99) { // game has ended, user wants to play again.
        	  System.out.println("new game request");
            chooseGame();
          } else {
          outputQueue.put(received);
          }
        }
        if(!inputQueue.isEmpty())
          read(inputQueue.take());
        } catch(Exception e){
          System.out.println(e);
          return;
      }
    }
  }
  
  public void chooseGame() {
	    Hand empty = new Hand();
	    player.setHand(empty);
	    int numberOfPlayers = 0;
	    // choose type of game to join
	    try {
	    	out.println("GAMETYPE");
	        numberOfPlayers = in.readInt();
	        System.out.println(numberOfPlayers);
	    } catch(IOException e){
	        System.out.println("Communication error");
	        return;
	    }
	    Server.addToGame(player, numberOfPlayers);
	    System.out.println("Player assigned");
	   
	 
  }

  public void read(Object obj){
    out.println(obj);
  }

  public BlockingQueue getInputQueue(){
    return inputQueue;
  }

  public BlockingQueue getOutputStream(){
    return outputQueue;
  }
  
  public void showLeaderBoard() {
    List<String> results = db.retrievePointsData();
    for(String s : results) {
    	try {
    	  out.println("LEADER " + s);
    	}catch(Exception e) {
          System.out.println(e);
    	}
    }
  }
}
    // System.out.println("Closing connection");
    // try{
    //   socket.close();
    // } catch(IOException e){
    //   System.out.println("Connection error");
    // }



