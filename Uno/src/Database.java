import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.*;


/**
 * Class for interacting with PostgreSQL user database
 * Used to validate user login attempts, register new users (if they have unique
 * username), retrieve top 15 players and update players wins, losses and points at the end
 * of each game.
 *
 */
public class Database {

  String url;
  String username;
  String password;

  String usernameClient;
  String passwordClient;

  private boolean check = false;


  public Database() {
  }

  public void getConnection(){

    try (FileInputStream input = new FileInputStream(new File("src/uno/db.properties"))) {
      Properties props = new Properties();

      props.load(input);

            // String driver = (String) props.getProperty("driver");
      username = (String) props.getProperty("username");
      password = (String) props.getProperty("password");
      url = (String) props.getProperty("URL");

            // We do not need to load the driver explicitly
            // DriverManager takes cares of that
            // Class.forName(driver);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  public boolean newUser(String usernameClient, String passwordClient) {


getConnection();


try (Connection connection = DriverManager.getConnection(url, username, password);) {

  if (connection != null) {
    System.out.println("Database accessed!");

  } else {
    System.out.println("Failed to make connection.");
  }

  try (PreparedStatement selectStatement =
   connection.prepareStatement("SELECT user_name FROM credentials")) {

    try (ResultSet resultSet = selectStatement.executeQuery()) {
      while (resultSet.next()) {

        if (usernameClient.equals(resultSet.getString("user_name"))) {

          System.out.println(usernameClient + " is taken, please select a new one");
          return check;

        }
      }
    }
  }


  try (PreparedStatement insertStatement =
   connection.prepareStatement("INSERT INTO credentials (user_name,password) VALUES (?,?) ");) {

    System.out.println("Table Insert accessed");

    insertStatement.setString(1, usernameClient);
    insertStatement.setString(2, passwordClient);

    insertStatement.executeUpdate();

    addPointsData(usernameClient,0,0,0);
     check=true;
  }
} catch (SQLException e) {
  e.printStackTrace();
}
return check;
}

  public boolean existingUser(String usernameClient, String passwordClient) {

//            Scanner sc = new Scanner(System.in);
//
//            System.out.println("please enter your username");
//            String usernameClient = sc.nextLine();


    getConnection();

    try (Connection connection = DriverManager.getConnection(url, username, password);) {

      if (connection != null) {
        System.out.println("Database accessed!");

      } else {
        System.out.println("Failed to make connection.");
                   // return;
      }

      try (PreparedStatement selectStatementUser =
       connection.prepareStatement("SELECT user_name FROM credentials")) {

        try (ResultSet resultSetUser = selectStatementUser.executeQuery()) {
          while (resultSetUser.next()) {

            if (usernameClient.equals(resultSetUser.getString("user_name")) && !usernameClient.equals("")) {

//                                System.out.println("please enter your password");
//
//                                String passwordClient = sc.nextLine();

              try (PreparedStatement selectStatementPass =
               connection.prepareStatement("SELECT password FROM credentials WHERE user_name = ?"))

              {
                selectStatementPass.setString(1, usernameClient);


                try (ResultSet resultSetPass = selectStatementPass.executeQuery()) {
                  while (resultSetPass.next()) {


                    if (passwordClient.equals(resultSetPass.getString("password"))) {

                      System.out.println("Success welcome back " + usernameClient);
                      return true;
                    }

                  }
                }
              }
            }
          }
        }
      }
      System.out.println("incorrect username or password");


    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;

  }

  public void addPointsData(String usernameClient, int win, int loss, int points) {


    getConnection();

    try (Connection connection = DriverManager.getConnection(url, username, password);) {

      if (connection != null) {
        System.out.println("Database accessed!");

      } else {
        System.out.println("Failed to make connection.");
                // return;
      }

      try (PreparedStatement selectStatementUser =
       connection.prepareStatement("SELECT user_name FROM credentials")) {

        try (ResultSet resultSetUser = selectStatementUser.executeQuery()) {
          while (resultSetUser.next()) {

            if (usernameClient.equals(resultSetUser.getString("user_name")) && !usernameClient.equals("")) {

//                                System.out.println("please enter your password");
//
//                                String passwordClient = sc.nextLine();


              try (PreparedStatement insertStatement =
               connection.prepareStatement("UPDATE credentials SET (win_count,loss_count,total_score)= (?,?,?) WHERE user_name = (?) ");) {


                System.out.println("Table Insert accessed");

                insertStatement.setInt(1, win );
                insertStatement.setInt(2, loss);
                insertStatement.setInt(3, points);
                insertStatement.setString(4,usernameClient);


                insertStatement.executeUpdate();
                System.out.println("points updated");

              }

            }
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }


  }

  public ArrayList<String> retrievePointsData() {

//            Scanner sc = new Scanner(System.in);
//
//            System.out.println("please enter your username");
//            String usernameClient = sc.nextLine();

    ArrayList<String> results = new ArrayList<>();
    getConnection();

    try (Connection connection = DriverManager.getConnection(url, username, password);) {

      if (connection != null) {
        System.out.println("Database accessed!");

      } else {
        System.out.println("Failed to make connection.");
                // return;
      }

      try (PreparedStatement selectStatementUser =
       connection.prepareStatement("SELECT user_name, win_count, loss_count, total_score FROM credentials WHERE total_score > 0 ORDER BY total_score DESC LIMIT 15")) {

        try (ResultSet resultSetUser = selectStatementUser.executeQuery()) {
          while (resultSetUser.next()) {

            String name = resultSetUser.getString("user_name");
            String win = Integer.toString(resultSetUser.getInt("win_count"));
            String loss = Integer.toString(resultSetUser.getInt("loss_count"));
            String points = Integer.toString(resultSetUser.getInt("total_score"));

            results.add(name + " " + win + " " + loss + " " + points);
          }
        }
      
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return results;
  }



//    public void givenUsingApache_whenGeneratingRandomStringBounded_thenCorrect() {
//
//        int length = 10;
//        boolean useLetters = true;
//        boolean useNumbers = false;
//        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
//
//        System.out.println(generatedString);
//    }



  public String randomString() {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
          int randomLimitedInt = leftLimit + (int)
          (random.nextFloat() * (rightLimit - leftLimit + 1));
          buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
      }


      public static void main (String[] args) {
        Database test = new Database();

//        for (int i = 0; i < 100; i++) {
//
//            String x = test.randomString();
//
//            test.newUser(x, test.randomString());
//
//            test.addPointsData(x, (int)(Math.random()*((1000-0)+1))+0, (int)(Math.random()*((1000-0)+1))+0, (int)(Math.random()*((1000-0)+1))+0);
//        }
        // test.existingUser("kosta3", "test2");
       // test.retrievePointsData();
        // test.newUser("kosta2", "tets2");
        test.addPointsData("kosta4", 1, 0, 99);
      }

    }
