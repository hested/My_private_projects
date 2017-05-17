# Group Matrix - Project 3

Introduction to Database Design - Spring 2017

Hand in date: 2017-05-17

By Johnni Hested, Kristin Kaltenhäuser, Sarah Moore-Simonsen, Sophia Anatolieva Evtimova. 


## Running the program 
Before compiling and running the program you need to edit the code and insert your local MySQL username and login in the top of the java file.

From the code:
```
private static final String USER = "SQL-username";
private static final String PASS = "SQL-login";
```

Compile and run the program in terminal.

## Create user
Type in terminal:
```
create
```
if you want to create a new user. Creating a user takes 4 arguments:

* UserName (String)
* Password (integer)
* FirstName  (String)
* LastName  (String)

Example:
```
user1 1234 john doe
```


## Login
Type in terminal:
```
login
```
if you want to login with an already existing user. Login takes 2 arguments:

* UserName (String)
* Password (integer)

Example:
```
user1 1234
```


## Search for a movie
Type in terminal:
```
search movie name
```
if you want to search for a movie. It returns a list of matching movies in the database, the involved directors and actors, and he movie's rental status.


## Rent a movie
Type in terminal:
```
rent movieID
```
if you want to rent a movie. You will rent the movie if it is not rented out already and if you have not reached your rental limit.


## Return a movie
Type in terminal:
```
return movieID
```
if you want to return a movie. The movie will be returned if you have already rented the movie.
