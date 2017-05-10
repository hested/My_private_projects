import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RentAMovie {
    // Local MySQL database credentials
    private static final String DB_URL_IMDB = "jdbc:mysql://localhost:3306/imdb?autoReconnect=true&useSSL=false";
    private static final String DB_URL_CUSTOMERBASE = "jdbc:mysql://localhost:3306/customerbase?autoReconnect=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "xxxx";

    private static int userId;
    private static String userName;

    private boolean signInUser(String user, int password) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT count(*), customerid FROM customer WHERE login = " + "'" + user + "'" + "AND password = " + "'" + password + "'");

            resultSet.first();

            int count = resultSet.getInt(1);

            if (count == 0) {
                return false;
            } else {
                userId = resultSet.getInt(2);
                userName = user;
            }

            System.out.println("You are now signed in as " + userName);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean createUser(String user, int password, String firstName, String lastName) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT count(*), customerid FROM customer WHERE login = " + "'" + user + "'");

            resultSet.first();

            int count = resultSet.getInt(1);

            if (count > 0) {
                System.out.println("Username is taken.");
                return false;
            }

            connection.setAutoCommit(false);
            PreparedStatement preparedStatementInsert;

            String insertSQL = "INSERT INTO customer (login, password, firstname, lastname) VALUES (" + "'" + user + "', '" + password + "', '" + firstName + "', '" + lastName + "')";

            preparedStatementInsert = connection.prepareStatement(insertSQL);
            preparedStatementInsert.executeUpdate();
            connection.commit();

            userName = user;

            System.out.println("Account successfully created.");
            return true;

        } catch (Exception e) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            e.printStackTrace();
        }
        return false;
    }

    private void searchMovie(String movieTitle) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL_IMDB, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM movie WHERE title = " + "'" + movieTitle + "'");

            List<String> movies = new ArrayList<>();

            while (resultSet.next()) {
                movies.add(resultSet.getString("id") + " " + resultSet.getString("title") + " " + resultSet.getString("year"));
            }

            if (movies.size() > 0) {

                for (String movie : movies) {
                    System.out.println(" ---------------------------------- ");
                    System.out.println(movie);

                    String[] splitMovie = movie.split("\\s+");

                    int movieId = -1;
                    try {
                        movieId = Integer.parseInt(splitMovie[0]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    resultSet = statement.executeQuery("SELECT name, role FROM involved, person WHERE involved.personId = person.id AND movieId = " + "'" + movieId + "'");

                    String movieDirectors = "Directors: ";
                    String movieActors = "Actors: ";

                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String role = resultSet.getString("role");

                        if (role.equals("actor")) {
                            movieActors += name + ", ";
                        } else {
                            movieDirectors += name + ", ";
                        }
                    }

                    System.out.println(movieDirectors);
                    System.out.println(movieActors);

                    Connection connectionRental = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
                    Statement statementRental = connectionRental.createStatement();
                    ResultSet resultSetRental = statementRental.executeQuery("SELECT count(*) FROM rental WHERE rentstatus = " + "'" + 1 + "'" + "AND movieid = " + "'" + movieId + "'");
                    resultSetRental.first();
                    int isMovieRentedOut = resultSetRental.getInt(1);

                    if (isMovieRentedOut > 0) {
                        System.out.println("Rental status: The movie is already rented out.");
                    } else {
                        System.out.println("Rental status: The movie is not rented out yet.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(" ---------------------------------- ");
        System.out.println("Search complete.");
        System.out.println(" ---------------------------------- ");

    }

    private boolean rentMovie(int movieId) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
            Statement statement = connection.createStatement();

            ResultSet resultSetCustomer = statement.executeQuery("SELECT count(*) FROM rental WHERE rentstatus = " + "'" + 1 + "'" + "AND customerid = " + "'" + userId + "'");
            resultSetCustomer.first();
            int moviesRentedByCustomer = resultSetCustomer.getInt(1);

            ResultSet resultSetMovie = statement.executeQuery("SELECT count(*) FROM rental WHERE rentstatus = " + "'" + 1 + "'" + "AND movieid = " + "'" + movieId + "'");
            resultSetMovie.first();
            int isMovieRentedOut = resultSetMovie.getInt(1);

            if (moviesRentedByCustomer > 2) {
                System.out.println("You can not rent more movies. Please return one!");
                return false;
            }

            if (isMovieRentedOut > 0) {
                System.out.println("The movie you are trying to rent is already rented out. Find another.");
                return false;
            }

            connection.setAutoCommit(false);
            PreparedStatement preparedStatementInsert;

            String insertSQL = "INSERT INTO rental (customerID, movieID, rentStatus, rentDate) VALUES (" + "'" + userId + "', '" + movieId + "', " + 1 + ", current_timestamp())";

            preparedStatementInsert = connection.prepareStatement(insertSQL);
            preparedStatementInsert.executeUpdate();
            connection.commit();

            System.out.println(userName + ", you have successfully rented the movie with the id: " + movieId);
            System.out.println(userName + ", you can rent " + (2 - moviesRentedByCustomer) + " more movies.");
            return true;

        } catch (Exception e) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }

            e.printStackTrace();
        }

        return false;
    }

    private boolean returnMovie(int movieId) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSetMovie = statement.executeQuery("SELECT COUNT(*) FROM rental WHERE rentstatus = " + "'" + 1 + "'" + "AND movieid = " + "'" + movieId + "'" + "AND customerid = " + "'" + userId + "'");

            resultSetMovie.first();

            int isMovieRentedOut = resultSetMovie.getInt(1);

            if (isMovieRentedOut < 1) {
                System.out.println("Hey " + userName + ". You did not rent this movie and can not return it.");
                return false;
            }

            connection.setAutoCommit(false);
            PreparedStatement preparedStatementInsert;

            String insertSQL = "UPDATE rental SET rentstatus = 0, returndate = current_timestamp() WHERE customerid = " + "'" + userId + "'" + " AND movieId = " + "'" + movieId + "'" + "AND rentstatus = 1";

            preparedStatementInsert = connection.prepareStatement(insertSQL);
            preparedStatementInsert.executeUpdate();
            connection.commit();

            System.out.println(userName + ", you have successfully returned the movie with the id: " + movieId);
            return true;

        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }

            e.printStackTrace();
        }

        return false;
    }


    public static void main(String[] args) {
        RentAMovie rentAMovie = new RentAMovie();

        userId = -1;
        boolean signedIn = false;
        boolean exitStore = false;
        boolean choosingSignIn = false;
        String signInChoice = "";

        System.out.println("Welcome to The Matrix Movie Store");

        while (!choosingSignIn) {
            System.out.println("To log in type <login>. To create a new user type <create>.");

            Scanner scanner = new Scanner(System.in);
            String userCommand = scanner.nextLine();

            switch (userCommand) {
                case "login":
                    signInChoice = "login";
                    choosingSignIn = true;
                    break;

                case "create":
                    signInChoice = "create";
                    choosingSignIn = true;
                    break;

                default:
                    System.out.println("Could not recognize what you wrote. Try again.");
                    break;
            }
        }

        while (!signedIn) {
            Scanner scanner = new Scanner(System.in);

            switch (signInChoice) {
                case "login": {
                    System.out.println("Type <username> and <password>");

                    String userInfo;
                    userInfo = scanner.nextLine();
                    String[] splitUserInfo = userInfo.split("\\s+");

                    String name;
                    int userPass;

                    name = splitUserInfo[0];

                    try {
                        userPass = Integer.parseInt(splitUserInfo[1]);

                        if (rentAMovie.signInUser(name, userPass)) {
                            signedIn = true;
                        } else {
                            System.out.println("Could not find username and password. Try again.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Your Password is not an integer. Try again.");
                    }
                    break;
                }

                case "create": {
                    System.out.println("Type <username> and <password> and <firstname> and <lastname>");

                    String userInfo;
                    userInfo = scanner.nextLine();
                    String[] splitUserInfo = userInfo.split("\\s+");

                    if (splitUserInfo.length != 4) {
                        System.out.println("Could not recognize what you wrote. Try again.");
                        break;
                    }

                    String name;
                    int userPass = -1;
                    String fName;
                    String lName;

                    name = splitUserInfo[0];
                    fName = splitUserInfo[2];
                    lName = splitUserInfo[3];

                    try {
                        userPass = Integer.parseInt(splitUserInfo[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Your Password is not an integer. Try again.");
                    }

                    if (rentAMovie.createUser(name, userPass, fName, lName)) {
                        if (rentAMovie.signInUser(name, userPass)) {
                            signedIn = true;
                        } else {
                            System.out.println("Failed to create new account. Try again.");
                        }

                    } else {
                        System.out.println("Failed to create new account. Try again.");
                    }
                    break;
                }

                default:
                    System.out.println("Could not recognize what you wrote. Try again.");
                    break;
            }
        }



        System.out.println("+----------------------------------+");
        System.out.println("|            HOW TO USE            |");
        System.out.println("+----------------------------------+");
        System.out.println("| To search type: search movieName |");
        System.out.println("| To rent   type: rent movieID     |");
        System.out.println("| To return type: return movieID   |");
        System.out.println("| To exit   type: exit             |");
        System.out.println("+----------------------------------+");



        while (!exitStore) {
            Scanner scanner = new Scanner(System.in);
            String query = scanner.nextLine();

            String[] splitQuery = query.split("\\s+");

            int movieId;

            switch (splitQuery[0]) {
                case "search":
                    rentAMovie.searchMovie(query.substring(7));
                    break;

                case "rent":
                    try {
                        movieId = Integer.parseInt(splitQuery[1]);
                        rentAMovie.rentMovie(movieId);
                    } catch (NumberFormatException e) {
                        System.out.println("Movie ID is not recognized. Try again.");
                    }
                    break;

                case "return":
                    try {
                        movieId = Integer.parseInt(splitQuery[1]);
                        rentAMovie.returnMovie(movieId);
                    } catch (NumberFormatException e) {
                        System.out.println("Movie ID is not recognized. Try again.");
                    }
                    break;

                case "exit":
                    exitStore = true;
                    System.out.println("Goodbye " + userName + ". Thanks for visiting The Matrix Movie Store.");
                    break;

                default:
                    System.out.println("Use the correct terms to use the Movie Rental. Try again.");
                    break;
            }
        }
    }
}
