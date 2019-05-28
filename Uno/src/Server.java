import java.net.*;
import java.io.*;

/**
 * Class to begin serverside application. Creates serversocket, listens for connectionsa and assigns
 * these to runnable ClientHandler threads. 
 * 
 * Also manages assignment of players to Games and putting these in GameHandler threads once full.
 * addToGame calls come from ClientHandler threads, so this is a synchronized method.
 *
 */
public class Server {
  private ServerSocket server;

  public static Game game2P;
  public static Game game3P;
  public static Game game4P;
  public static Game[] games;

  public Server(int port){
	// setting up game options
	game2P = new Game(2);
	game3P = new Game(3);
	game4P = new Game(4);
    games = new Game[] {game2P, game3P, game4P}; // so can be accessed by index
    
    try {
      server = new ServerSocket(port);
      System.out.println("Server running");
      System.out.println("Waiting for a client.");
    } catch(IOException e ){
      System.out.println("Error setting up socket");
    }
    while(true){ 
      Socket socket = null;
      try{
        socket = server.accept();
        System.out.println("Client accepted");

        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Assigning new thread");

        ClientHandler ch = new ClientHandler(socket, in, out);
        Thread t = new Thread(ch);
        t.start();
        System.out.println("Thread assigned and started");

      } catch(Exception e){
        System.out.println("Error accepting client");
      }
    }
  }

  
  public synchronized static void addToGame(Player player, int numberOfPlayers){
	Game game = games[numberOfPlayers - 2];  // get Game of right size from array
    if(game.acceptPlayers()){ // should always be the case
      game.addPlayer(player);
      System.out.println("Player added");
    } else {
      games[numberOfPlayers - 2] = new Game(numberOfPlayers); // purely for error handling 
      game.addPlayer(player);
      System.out.println("Player added");
      game = games[numberOfPlayers - 2];
    }
    if(!game.acceptPlayers()){
      GameHandler gh = new GameHandler(game);
      Thread t = new Thread(gh);
      t.start();
      System.out.println("New game begins");
      games[numberOfPlayers - 2] = new Game(numberOfPlayers); // replacing game now in play with a new one
  }
}

 
  public static void main(String args[]) {
    Server server = new Server(7777);
  }
}
