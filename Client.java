import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.InputMismatchException;

/** 
 * Runnable Client class for handling communication to and from serverside application.
 * 
 * This class also coordinates the flow of the clientside application. It compares data
 * received from serverside against a set of keywords, calling appropriate contorller methods.
 * 
 * Receives data at appropriate points from user/ views using a shared BlockingQueue, passing
 * this on to serverside.
 * 
 * If an error occurs calls an appropriate error message depending on where user currently is
 * and then ends the thread.
 *
 */
public class Client implements Runnable{
	  
  private String address;
  private int port; 
  private Socket socket;
  private Scanner scanner;
  private BufferedReader serverIn;
  private DataOutputStream out;
  private boolean inGame;
  private boolean inChoose;
  public BlockingQueue<Object> inputQueue; // in from other, local classes
  public GameViewController gController;
  public LoginController lController;
  public RegisterController rController;
  public ChooseController cController;
  
  

  public Client(String address, int port){
	  this.address = address;
	  this.port = port;
      inputQueue = new LinkedBlockingDeque<>();
      inGame = false;
  }
  
  //for direct interaction with views
  public void setGController(GameViewController controller) {
	  this.gController = controller;
  }
  
  public void setLController(LoginController controller) {
	  this.lController = controller;
  }
  
  public void setRController(RegisterController controller) {
	  this.rController = controller;
  }
  
  public void setCController(ChooseController controller) {
	  this.cController = controller;
  }
  
  
  @Override
  public void run() {
      try {
		socket = new Socket(address, port);
		System.out.println("Connected to server");
	} catch (IOException e1) {
		e1.printStackTrace();
		lController.error();
		rController.error();
		lController.showError();
		return;
	}
      try {
        serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new DataOutputStream(socket.getOutputStream());
      } catch(Exception e) {
    	  System.out.println(e);
      }
    String line = "";

    // listening for data from serverside, comparing against keywords and acting appropriately 
    try{
      while (!line.equals("QUIT")) {
        line = serverIn.readLine();
        if(line.equals("ACTION")){
          int choice = (Integer)inputQueue.take();
          out.writeInt(choice);
          if(choice == 1 || choice == 2) {
            choice = (Integer)inputQueue.take();
            out.writeInt(choice);}
        } else if(line.equals("INT INPUT")){
          int input = scanner.nextInt();
          out.writeInt(input);
        } else if(line.equals("COLOUR")){
            gController.chooseColour();
            int choice = (Integer)inputQueue.take();
            out.writeInt(choice);
        } else if(line.equals("WON")){
          gameFinished();
        } else if(line.contains("END")) {
        	System.out.println("Game ending");
        	gController.infoBox(line);
        	int replay = (Integer)inputQueue.take();
        	out.writeInt(replay);
        } else if(line.contains("GAMESIZE")) {
        	inGame = true;
        	inChoose = false;
        	gController.setGameSize(line.split(" ")[1]);
        } else if(line.contains("PLAYER")) {
        	gController.addPlayer(line.split(" ")[1]);
        } else if(line.contains("TURN")) {
        	gController.turnInput(line.split(" ")[1]);
        } else if(line.contains("CARDNO")) {
            gController.cardNo(line);
        } else if(line.contains("CARD-")) {
        	gController.addToHand(line);
        	System.out.println(line);
        } else if(line.contains("PILE")) {
        	gController.updatePile(line.split(" ")[1]);
        } else if(line.contains("ACOLOUR")) {
        	gController.updateActiveColour(line.split(" ")[1]);
        } else if(line.contains("LOGIN")) {
        	String type = (String) inputQueue.take();
        	String name = (String) inputQueue.take();
        	String pass = (String) inputQueue.take();
        	out.writeUTF(type);
        	out.writeUTF(name);
        	out.writeUTF(pass);
        } else if(line.contains("INVALID")){
        	System.out.println("Telling login invalid");
        	lController.invalid();	
        } else if(line.contains("TAKEN")) {
        	System.out.println("Telling register invalid");
        	rController.invalid();
        } else if(line.contains("LOGGEDIN")) {
        	System.out.println("Telling login valid");
        	lController.loggedIn();
        } else if(line.contains("REGISTERED")) {
        	System.out.println("Telling register valid");
        	rController.loggedIn();
        } else if(line.contains("LEADER")) {
        	cController.addToLeaderBoard(line);
        } else if(line.equals("WAIT")){
        	cController.wait();
        } else if(line.contains("RUMBLE")){
        	cController.gameStart();
        } else if(line.equals("GAMETYPE")) {
        	inGame = false;
        	inChoose = true;
        	int choice = (int) inputQueue.take();
        	out.writeInt(choice);
        }
        else {
         gController.show(line);
         System.out.println(line);
        }
        
        if(!inputQueue.isEmpty()) {
          String output = (String) inputQueue.take();
          out.writeUTF(output);
        }
      }
    } // if error, call error message method for current view and then end thread
      catch(Exception e){
        System.out.println(e);
        if(inGame == true) {
          // gController.error();
        } else if(inChoose == true) {
          cController.error();
        } else {
          lController.error();
          rController.error();
          lController.showError();
        }
        return;
      }
    try {
      out.close();
      socket.close();
    }
    catch(IOException e){
      System.out.println("Communication error with server");
    }
  }

  public void gameFinished(){
	  gController.resetHand();
	 // gController.infoBox("The game has ended","What would you like to do?" );
  }

  public BlockingQueue<Object> getInputQueue(){
	  return inputQueue;
  }
}