import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RentAMovie {
    // Local MySQL database credentials
    private static final String DB_URL_IMDB = "jdbc:mysql://localhost:3306/imdb?autoReconnect=true&useSSL=false";
    private static final String DB_URL_CUSTOMERBASE = "jdbc:mysql://localhost:3306/customerbase?autoReconnect=true&useSSL=false";
    private static final String USER = "root"; // Type your local MySQL user name
    private static final String PASS = "xxxx"; // Type your local MySQL password

    private static int mUserId;
    private static String mUserName;


    /**
     * Signs in a user with a given user name and password.
     *
     * @param user      The user name the user typed in.
     * @param password  The password the user typed in. Must be an integer.
     * @return          True if the sign in succeeded. False otherwise.
     */
    private boolean signInUser(String user, int password) {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // To prevent 'non-repeatable reads' and 'phantom reads'.

            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT count(*), customerid FROM customer WHERE login = " + "'" + user + "'" + "AND password = " + "'" + password + "'");
            resultSet.first();
            int count = resultSet.getInt(1);

            if (count == 0) {
                return false;
            } else {
                mUserId = resultSet.getInt(2);
                mUserName = user;
            }

            System.out.println("You are now signed in as " + mUserName);
            return true;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            closeConnections(connection, statement, null);
        }

        return false;
    }


    /**
     * Creates a new user in the database if it does not already exist.
     * This method does not sign in the user afterwards.
     *
     * @param user      The user name the user typed in.
     * @param password  The password name the user typed in. Must be an integer.
     * @param firstName The first name the user typed in.
     * @param lastName  The last name the user typed in.
     * @return          True if it successfully created the user. False otherwise.
     * @throws SQLException
     */
    private boolean createUser(String user, int password, String firstName, String lastName) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatementInsert = null;

        try {
            connection = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // To prevent 'non-repeatable reads' and 'phantom reads'.

            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT count(*), customerid FROM customer WHERE login = " + "'" + user + "'");
            resultSet.first();
            int count = resultSet.getInt(1);

            if (count > 0) {
                System.out.println("Username is taken.");
                return false;
            }

            String insertSQL = "INSERT INTO customer (login, password, firstname, lastname) VALUES (" + "'" + user + "', '" + password + "', '" + firstName + "', '" + lastName + "')";

            preparedStatementInsert = connection.prepareStatement(insertSQL);
            preparedStatementInsert.executeUpdate();
            connection.commit();

            mUserName = user;

            System.out.println("Account successfully created.");
            return true;

        } catch (Exception e) {

            if (connection != null) {
                connection.rollback();
            }

            e.printStackTrace();

        } finally {
            closeConnections(connection, statement, preparedStatementInsert);
        }

        return false;
    }


    /**
     * Search the database for movies with the given movie title.
     * When it finds a movie, it prints out its id, its title, and its production year.
     * After that it prints the involved directors and actors and the movie's current rental status.
     * Finally it will print our when the search is complete.
     *
     * @param movieTitle  The full movie title.
     */
    private void searchMovie(String movieTitle) {
        Connection connection = null;
        Connection connectionRental = null;
        Statement statement = null;
        Statement statementRental = null;

        try {
            connection = DriverManager.getConnection(DB_URL_IMDB, USER, PASS);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // To prevent 'non-repeatable reads' and 'phantom reads'.

            statement = connection.createStatement();

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

                    connectionRental = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
                    connectionRental.setAutoCommit(false);
                    connectionRental.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // To prevent 'non-repeatable reads' and 'phantom reads'.

                    statementRental = connectionRental.createStatement();

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

        } finally {
            closeConnections(connection, statement, null);
            closeConnections(connectionRental, statementRental, null);
        }

        System.out.println(" ---------------------------------- ");
        System.out.println(" Search complete.");
        System.out.println(" ---------------------------------- ");
    }


    /**
     * This method rents a movie to the signed in user. It checks in the database if the user has
     * reached its rental limit and if the movie is available for rent. If all the criteria are met
     * then the user rents the movie.
     *
     * @param movieId  The id of the movie.
     * @return         True if the movie was rented. False if the user can not rent more movies
     *                 or if the movie is already rented out.
     * @throws SQLException
     */
    private boolean rentMovie(int movieId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatementInsert = null;

        try {
            connection = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // To prevent 'non-repeatable reads' and 'phantom reads'.

            statement = connection.createStatement();

            ResultSet resultSetCustomer = statement.executeQuery("SELECT count(*) FROM rental WHERE rentstatus = " + "'" + 1 + "'" + "AND customerid = " + "'" + mUserId + "'");
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

            String insertSQL = "INSERT INTO rental (customerID, movieID, rentStatus, rentDate) VALUES (" + "'" + mUserId + "', '" + movieId + "', " + 1 + ", current_timestamp())";

            preparedStatementInsert = connection.prepareStatement(insertSQL);
            preparedStatementInsert.executeUpdate();
            connection.commit();

            System.out.println(mUserName + ", you have successfully rented the movie with the id: " + movieId);
            System.out.println(mUserName + ", you can rent " + (2 - moviesRentedByCustomer) + " more movies.");
            return true;

        } catch (Exception e) {

            if (connection != null) {
                connection.rollback();
            }

            e.printStackTrace();

        } finally {
            closeConnections(connection, statement, preparedStatementInsert);
        }

        return false;
    }


    /**
     * Returns the desired movie. If the user did not rent the movie, it will print out and let the user know.
     *
     * @param movieId  The id of the movie to return.
     * @return         True if the movie was returned successfully. False if the user has not rented the movie.
     * @throws SQLException
     */
    private boolean returnMovie(int movieId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatementInsert = null;

        try {
            connection = DriverManager.getConnection(DB_URL_CUSTOMERBASE, USER, PASS);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // To prevent 'non-repeatable reads' and 'phantom reads'.

            statement = connection.createStatement();

            ResultSet resultSetMovie = statement.executeQuery("SELECT COUNT(*) FROM rental WHERE rentstatus = " + "'" + 1 + "'" + "AND movieid = " + "'" + movieId + "'" + "AND customerid = " + "'" + mUserId + "'");
            resultSetMovie.first();
            int isMovieRentedOut = resultSetMovie.getInt(1);

            if (isMovieRentedOut < 1) {
                System.out.println("Hey " + mUserName + ". You did not rent this movie and can not return it.");
                return false;
            }

            String insertSQL = "UPDATE rental SET rentstatus = 0, returndate = current_timestamp() WHERE customerid = " + "'" + mUserId + "'" + " AND movieId = " + "'" + movieId + "'" + "AND rentstatus = 1";

            preparedStatementInsert = connection.prepareStatement(insertSQL);
            preparedStatementInsert.executeUpdate();
            connection.commit();

            System.out.println(mUserName + ", you have successfully returned the movie with the id: " + movieId);
            return true;

        } catch (Exception e) {

            if (connection != null) {
                connection.rollback();
            }

            e.printStackTrace();

        } finally {
            closeConnections(connection, statement, preparedStatementInsert);
        }

        return false;
    }


    /**
     * A simple method that closes all open statements and connections.
     *
     * @param connection                The connection we want to close.
     * @param statement                 The statement we want to close.
     * @param preparedStatementInsert   The prepared statement we want to close.
     */
    private void closeConnections(Connection connection, Statement statement, PreparedStatement preparedStatementInsert) {
        try {

            if (statement != null) {
                statement.close();
            }

            if (preparedStatementInsert != null) {
                preparedStatementInsert.close();
            }

            if (connection != null) {
                connection.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        RentAMovie rentAMovie = new RentAMovie();

        mUserId = -1;
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

                    try {
                        if (rentAMovie.createUser(name, userPass, fName, lName)) {
                            if (rentAMovie.signInUser(name, userPass)) {
                                signedIn = true;
                            } else {
                                System.out.println("Failed to create new account. Try again.");
                            }

                        } else {
                            System.out.println("Failed to create new account. Try again.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Something went wrong while connecting to the database. Try again.");
                        e.printStackTrace();
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
                    } catch (SQLException e) {
                        System.out.println("Something went wrong while connecting to the database. Try again.");
                        e.printStackTrace();
                    }
                    break;

                case "return":
                    try {
                        movieId = Integer.parseInt(splitQuery[1]);
                        rentAMovie.returnMovie(movieId);
                    } catch (NumberFormatException e) {
                        System.out.println("Movie ID is not recognized. Try again.");
                    } catch (SQLException e) {
                        System.out.println("Something went wrong while connecting to the database. Try again.");
                        e.printStackTrace();
                    }
                    break;

                case "exit":
                    exitStore = true;
                    System.out.println("Goodbye " + mUserName + ". Thanks for visiting The Matrix Movie Store.");
                    break;

                default:
                    System.out.println("Use the correct terms to use the Movie Rental. Try again.");
                    break;
            }
        }
    }
}
