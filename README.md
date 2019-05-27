# Uno-Card-Game
GUI Server-Client Uno game
-------------------------------------

Networked, GUI version of the popular card game Uno. Supports multiple clients and multiple games in session at once.

To play, first run the Server file. For database access must be connected to the CS department network.

Run the Uno class to start a client session and login or register.

Once logged in/ registered, on the choose game screen you can choose your deck and what kind
of game to join (2, 3 or 4 players). Once enough players, game will begin! Standard, core Uno
rules apply.

Once game is finished your win/ loss (and any points won) will be saved in the database
and you can either play again (taking you back to the choose game screen) or exit the application.

Main Classes/ Changes
-----------------------------------

GameControllerHandler:

This converts input from the client into actions for the user/ gui.
Has some methods called based on user actions (e.g. clicking a card) and
methods called from the client class - here they have to run in seperate threads
so as not to disrupt javaFX view.

Codes from our original text versoin are retained, e.g. when playing a card it sends
01 then the index of the card.

Players are only able to take actions when it is their turn.

ClientHandler:

This class was added to seperate out socket connections in seperate threads.
Each clientHandler is associated with the appropriate player for easier access.

The ClientHandlers use BlockingQueueObjects for thread safe communication
between itself and other threads. One for receiving input and one for sending it out.
Also uses a DataInputStream to get data from its respective client, and a printwriter
to send data to the client.

GameHandler:

This class takes on a lot of the functionality from the original controller class,
controlling the flow and logic of the game and the input/ output with users.

Each one is a runnable so each game is run as a thread, allowing for concurrent,
independent games to play.

The messages sent to the player are a mixture of direct data, which are simply
printed out by the client once received, and 'codes'. These instruct the client
to run methods where mutliple prints/ specific input is required. This process
could do with improvement, perhaps with a specific protocol class.
