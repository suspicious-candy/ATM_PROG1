//////////////// FILE HEADER (INCLUDE IN EVERY FILE) //////////////////////////
//
// Title:    ATM Program
// Author:   Shreyansh jain
// Email:    jainshreyansh606@gmail.com
//
///////////////////////// ALWAYS CREDIT OUTSIDE HELP //////////////////////////
//
// Online Sources: geekforgeek.org
//
///////////////////////////////////////////////////////////////////////////////



import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {


    /**
     * Serializes the Objects
     *
     * @param obj - Object to serialize
     * @return - serialized object
     */
    public static byte[] serializeObject(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Inserts new account to the database
     *
     * @param ID - Unique ID of the user
     * @param pass - password of the user
     * @param s - Savings account object
     * @param c - Checking account object
     * @param connection - connection to the database
     */
    public static void toinsert (int ID,String pass, savingsAcc s, checkingaccount c,Connection connection) {
        try {
            // Prepare the SQL statement to insert a new user
            String sql = "INSERT INTO Users (UserID, PasswordHash, SavingAccount, CheckingAccount) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Set the values for the placeholders in the SQL statement
            statement.setInt(1, ID);            // Assign the UserID value
            statement.setString(2, pass);       // Assign the password hash
            statement.setBytes(3, serializeObject(s));  // Serialize and set the SavingAccount object
            statement.setBytes(4, serializeObject(c));  // Serialize and set the CheckingAccount object

            // Execute the SQL statement to insert the new user
            int rowsInserted = statement.executeUpdate();

            // Check if the insertion was successful
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }
        } catch (SQLException ex) {
            // Handle any SQL errors that occur
            ex.printStackTrace();
        }
    }


    /**
     * get the string from a table
     *
     * @param conn- connection to database
     * @param field - field to search in
     * @param userID - unique user ID
     * @return - string searching for;
     */
    public static String getString(Connection conn, String field, int userID) {
        String str = null;  // Initialize the return variable

        try {
            // Construct the SQL query to retrieve the specified field
            String sql = "SELECT " + field + " FROM Users WHERE UserID = ?";

            // Prepare the statement for execution
            PreparedStatement statement = conn.prepareStatement(sql);

            // Set the placeholder value in the SQL statement
            statement.setInt(1, userID);  // Provide the UserID for the query

            // Execute the query and retrieve the results
            ResultSet result = statement.executeQuery();

            // Check if a matching user is found
            if (result.next()) {
                // Extract the requested field value from the result set
                str = result.getString(field);
            }
        } catch (SQLException ex) {
            // Handle any SQL errors that occur during the operation
            ex.printStackTrace();
        }

        // Return the retrieved value (or null if not found)
        return str;
    }


    /**
     * gives a column as an arraylist
     *
     * @param field -  column to pull out
     * @param connection - connection to database
     * @return - column in form of arraylist
     */
    public static ArrayList toget(String field,Connection connection){
        // Initialize an empty list to store the UserIDs
        ArrayList<Integer> userIds = new ArrayList<>();

        try {
            // Construct the SQL query to retrieve the specified field from all users
            String sql = "SELECT " + field + " FROM Users";

            // Create a statement object to execute the query
            Statement statement = connection.createStatement();

            // Execute the query and obtain the result set
            ResultSet result = statement.executeQuery(sql);

            // Process each row in the result set
            while (result.next()) {
                // Extract the UserID value from the current row
                int userId = result.getInt("UserID");  // Get the UserID from the result set
                userIds.add(userId);  // Add the UserID to the list
            }
        } catch (SQLException ex) {
            // Handle any SQL errors that may arise
            ex.printStackTrace();
        }

        // Return the collected UserIDs
        return userIds;
    }


    /**
     * Gets objects in a coulmn and field
     *
     * @param field - column to retrieve
     * @param ID - Unique user ID
     * @param connection - connection to database
     * @return - Object to retrieve
     */
    public static Object togetuserID(String field , int ID , Connection connection){
        Object toget = null;  // Initialize the variable to hold the retrieved object

        try {
            // Prepare the SQL query to retrieve the specified field for the given UserID
            String sql = "SELECT " + field + " FROM Users WHERE UserID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, ID);  // Set the UserID placeholder in the query

            // Execute the query and obtain the result set
            ResultSet result = statement.executeQuery();

            if (result.next()) {  // Check if a matching user is found
                // Retrieve the BLOB data containing the serialized object
                Blob blob = result.getBlob(field);
                byte[] bytes = blob.getBytes(1, (int) blob.length());  // Extract the bytes from the BLOB

                // Deserialize the object from the byte array
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                    toget = ois.readObject();  // Read the object from the stream
                }
            }
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            // Handle potential SQL, I/O, and class loading errors
            ex.printStackTrace();
        }

        // Return the retrieved object (or null if not found or an error occurred)
        return toget;
    }

    /**
     * updates the field with a new object
     * @param conn - connection to databases
     * @param field - column to retrieve
     * @param userID - Unique user ID
     * @param newObj - object that is going to replace
     */

    public static void replaceObject(Connection conn, String field, int userID, Object newObj) {
        try {
            // Construct the SQL UPDATE statement to modify a specific field for a given UserID
            String sql = "UPDATE Users SET " + field + " = ? WHERE UserID = ?";
            PreparedStatement statement = conn.prepareStatement(sql);  // Prepare the statement for execution

            // Set the parameters for the prepared statement
            statement.setBytes(1, serializeObject(newObj));  // Serialize the new object and set it as the value for the field
            statement.setInt(2, userID);                      	// Set the UserID to identify the target user

            // Execute the UPDATE statement to modify the database record
            statement.executeUpdate();
        } catch (SQLException ex) {
            // Handle any SQL errors that may occur during the update process
            ex.printStackTrace();
        }
    }


    /**
     * checks if object is in an array
     * @param a - array to check
     * @param b - object to check
     * @return - true, if array has object else false
     */
    public static boolean isin(ArrayList<Integer> a , int b){
        for (int s : a) {
            // Check if the current element 's' matches the value 'b'
            if (s == b) {
                // If a match is found, return true immediately
                return true;
            }
        }

        // If the loop completes without finding a match, return false
        return false;
    }

    public static void main(String[] args) {
        try{
            // Establishing a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/atm","root","password");
            // Creating a statement object to execute SQL queries
            Statement statement = connection.createStatement();
            // Executing a SQL query to get all tables in the database
            ResultSet resultSet = statement.executeQuery("SHOW TABLES;");
            // Checking if the result set is null
            if(resultSet==null) {
                try{
                    // SQL query to create a new table named 'Users'
                    String sql = "CREATE TABLE Users (" +
                            "UserID INT," +
                            "PasswordHash VARCHAR(255)," +
                            "SavingAccount BLOB," +
                            "CheckingAccount BLOB," +
                            "PRIMARY KEY (UserID)" +
                            ")";
                    // Executing the SQL query
                    statement.execute(sql);
                    // Printing a success message
                    System.out.println("Table created successfully!");
                } catch (SQLException ex) {
                    // Printing the stack trace of the exception
                    ex.printStackTrace();
                }
            }
            // Printing a welcome message and options for the user
            System.out.println("HI!! HOW MAY I HELP YOU TODAY\n " +
                    "1. MAKE NEW ACCOUNT\n" +
                    "2. ACCESS YOUR ACCOUNT");
            // Creating a Scanner object to get user input
            Scanner D = new Scanner(System.in);
            // Getting the user's choice
            int k = D.nextInt();
            // Checking if the user wants to create a new account
            if (k==1){
                // Asking the user to create a UserID
                System.out.println("MAKE YOUR USERID: ");
                int ID = D.nextInt();
                // Getting a list of all UserIDs
                ArrayList IDS = toget("UserID",connection);
                // Checking if the UserID already exists
                for(Object a : IDS){
                    if(a.equals(ID)){
                        // Throwing an exception if the UserID already exists
                        throw  new IllegalArgumentException(" Already existing user ID");
                    }
                }
                // Asking the user to create a password
                System.out.println("MAKE YOUR Password: ");
                String pass = D.next();
                // Creating objects for savings and checking accounts
                savingsAcc s =new savingsAcc();
                checkingaccount c = new checkingaccount();
                // Asking the user if they want to create a checking or savings account or both
                System.out.println("Do you wanna make a checking or a savings Account or both");
                String d= D.next();
                // Checking if the user wants to create a checking account
                if(d.equals("checking")){
                    // Asking the user to enter the deposit amount
                    System.out.println("Enter the deposit you wanna make");
                    // Creating a new checking account with the deposit amount
                    c=new checkingaccount(D.nextInt());
                } else if (d.equals("savings")) {
                    // Checking if the user wants to create a savings account
                    System.out.println("Enter the deposit you wanna make");
                    // Creating a new savings account with the deposit amount
                    s = new savingsAcc(D.nextInt());
                }else if(d.equals("both")){
                    // Checking if the user wants to create both checking and savings accounts
                    System.out.println("Enter the deposit you wanna make in checking");
                    // Creating a new checking account with the deposit amount
                    c=new checkingaccount(D.nextInt());
                    System.out.println("Enter the deposit you wanna make in savings");
                    // Creating a new savings account with the deposit amount
                    s = new savingsAcc(D.nextInt());
                }
                // Inserting the new user into the database
                toinsert(ID,pass,s,c,connection);
            }
            // Checking if the user wants to access their account
            if(k==2){
                // Asking the user to enter their UserID
                System.out.println("Enter User ID");
                int id = Integer.parseInt(D.next());
                // Asking the user to enter their password
                System.out.println("Enter Password");
                String pass = D.next();
                // Getting a list of all UserIDs
                ArrayList IDS = toget("UserID",connection);
                // Checking if the UserID exists
                if(!isin(IDS,id)){
                    // Throwing an exception if the UserID does not exist
                    throw new IllegalArgumentException("ID DOESNT EXIST");
                }
                // Checking if the entered password matches the original password
                for (Object a : IDS) {
                    if (a.equals(id)) {
                        // Getting the original password
                        String orgpass = String.valueOf(getString(connection,"PasswordHash",id));
                        // Checking if the entered password matches the original password
                        if (!orgpass.equals(pass)) {
                            // Throwing an exception if the passwords do not match
                            throw new IllegalArgumentException("Incorrect Password!!!!");
                        }
                    }
                }

                // Asking the user which account they want to access
                System.out.println("WHICH ACCOUNT DO YOU WANT TO ACCESS (savings/checking)");
                String s = D.next();
                // Checking if the user wants to access their savings account
                if (s.equals("savings")){
                    // Getting the savings account of the user
                    savingsAcc tocheck = (savingsAcc) togetuserID("SavingAccount",id,connection);
                    // Printing options for the user
                    System.out.println("HOW CAN I HELP YOU TODAY \n"+
                            "1. CHECK BALANCE\n"+
                            "2. MAKE DEPOSIT \n" +
                            "3. WITHDRAW MONEY\n");
                    // Getting the user's choice
                    int temp = Integer.parseInt(D.next());
                    // Checking if the user wants to check their balance
                    if (temp==1){
                        // Printing the balance of the user
                        System.out.println("Your Balance is "+tocheck.getBalance());
                    } else if (temp==2) {
                        // Checking if the user wants to make a deposit
                        System.out.println("How much do you wanna deposit ");
                        // Getting the deposit amount from the user
                        int todep = Integer.parseInt(D.next());
                        // Adding the deposit amount to the balance
                        tocheck.addBalance(todep);
                        // Updating the balance in the database
                        replaceObject(connection,"CheckingAccount",id,tocheck);
                    } else if (temp==3) {
                        // Checking if the user wants to make a withdrawal
                        System.out.println("How much do you wanna withdraw ");
                        // Getting the withdrawal amount from the user
                        int todep = Integer.parseInt(D.next());
                        // Subtracting the withdrawal amount from the balance
                        tocheck.withdraw(todep);
                        // Updating the balance in the database
                        replaceObject(connection,"CheckingAccount",id,tocheck);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}