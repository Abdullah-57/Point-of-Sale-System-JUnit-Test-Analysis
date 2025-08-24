import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class POSSystemTest {
    private POSSystem posSystem;
    private static final String EMPLOYEE_DATABASE_PATH = "Database/employeeDatabase.txt";
    private static final String LOG_FILE_PATH = "Database/employeeLogfile.txt";
    private static final String TEMP_FILE_PATH = "Database/temp.txt";
    

    @Before
    public void setUp() {
        // Create a new instance of POSSystem
        posSystem = new POSSystem();

        // Prepare the employee database for testing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EMPLOYEE_DATABASE_PATH))) {
            writer.write("user1 John Doe password1 Cashier");
            writer.newLine();
            writer.write("user2 Jane Doe password2 Admin");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Clean up the log file and temp file before each test
        cleanUpFiles();
    }

    @After
    public void tearDown() {
        // Clean up any created temp files and log files
        cleanUpFiles();
    }

    private void cleanUpFiles() {
        // Delete employee log file
        File logFile = new File(LOG_FILE_PATH);
        if (logFile.exists()) {
            logFile.delete();
        }

        // Delete temp file
        File tempFile = new File(TEMP_FILE_PATH);
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testLogIn_successfulCashierLogin() {
        // Act: Try to log in with correct credentials
        int result = posSystem.logIn("user1", "password1");

        // Assert: Check that the login is successful and returns cashier status
        assertEquals(1, result);
    }

    @Test
    public void testLogIn_successfulAdminLogin() {
        // Act: Try to log in with correct credentials
        int result = posSystem.logIn("user2", "password2");

        // Assert: Check that the login is successful and returns admin status
        assertEquals(2, result);
    }

    @Test
    public void testLogIn_invalidUsername() {
        // Act: Try to log in with an invalid username
        int result = posSystem.logIn("invalidUser", "password");

        // Assert: Check that the login fails and returns 0
        assertEquals(0, result);
    }

    @Test
    public void testLogIn_invalidPassword() {
        // Act: Try to log in with the correct username but incorrect password
        int result = posSystem.logIn("user1", "wrongPassword");

        // Assert: Check that the login fails and returns 0
        assertEquals(0, result);
    }

    @Test
    public void testCheckTemp_fileExists() {
        // Arrange: Create a temporary file
        try {
            Files.write(Paths.get(TEMP_FILE_PATH), "temp data".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Check if the temporary file exists
        boolean result = posSystem.checkTemp();

        // Assert: Check that the method returns true
        assertTrue(result);
    }

    @Test
    public void testCheckTemp_fileDoesNotExist1() {
        // Act: Check if the temporary file exists when it does not
        boolean result = posSystem.checkTemp();

        // Assert: Check that the method returns false
        assertFalse(result);
    }

    @Test
    public void testLogInToFile_successfulLogIn() {
        // Arrange: Log in successfully first
        posSystem.logIn("user2", "password2");

        // Act: Check the log file for login entry
        String expectedLog = "Jane Doe (user2 Admin) logs into POS System. Time: ";
        String logEntry = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            logEntry = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Assert: Check that the log entry starts with the expected string
        assertTrue(logEntry.startsWith(expectedLog));
    }

    @Test
    public void testLogOut_logsUserOutSuccessfully() {
        // Arrange: Log in first
        posSystem.logIn("user1", "password1");

        // Act: Log out the user
        posSystem.logOut("Cashier");

        // Assert: Check the log file for logout entry
        String expectedLog = "John Doe (user1 Cashier) logs out of POS System. Time: ";
        String logEntry = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            logEntry = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Assert: Check that the log entry starts with the expected string
        assertTrue(logEntry.startsWith(expectedLog));
    }

    @Test
    public void testContinueFromTemp_Sale() {
        // Arrange: Create a temp file for Sale
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("Sale");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Continue from temp
        String result = posSystem.continueFromTemp(1234567890L);

        // Assert: Check that the result is "Sale"
        assertEquals("Sale", result);
    }

    @Test
    public void testContinueFromTemp_Rental() {
        // Arrange: Create a temp file for Rental
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("Rental");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Continue from temp
        String result = posSystem.continueFromTemp(1234567890L);

        // Assert: Check that the result is "Rental"
        assertEquals("Rental", result);
    }

    @Test
    public void testContinueFromTemp_Return() {
        // Arrange: Create a temp file for Return
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("Return");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Continue from temp
        String result = posSystem.continueFromTemp(1234567890L);

        // Assert: Check that the result is "Return"
        assertEquals("Return", result);
    }

    @Test
    public void testContinueFromTemp_emptyFile() {
        // Arrange: Create an empty temp file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            // Empty file
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Continue from temp
        String result = posSystem.continueFromTemp(1234567890L);

        // Assert: Check that the result is empty
        assertEquals("", result);
    }

    @Test
    public void testContinueFromTemp_fileNotFound() {
        // Act: Try to continue from a non-existent temp file
        File tempFile = new File(TEMP_FILE_PATH);
        tempFile.delete(); // Ensure the file does not exist
        String result = posSystem.continueFromTemp(1234567890L);

        // Assert: Check that the result is empty
        assertEquals("", result);
    }
    
    
    
    @Test
    public void testReadFile_FileNotFound() {
        // Arrange: Set a path for a non-existent employee database
        posSystem.employeeDatabase = "Database/nonExistentFile.txt";

        // Act: Attempt to read the file
        posSystem.readFile();

        // Assert: The employees list should be empty since the file doesn't exist
        assertTrue(posSystem.employees.isEmpty());
    }

    @Test
    public void testLogInToFile_FileNotFound() {
        // Arrange: Create an invalid log file path
        posSystem.logInToFile("user1", "John Doe", "Cashier", Calendar.getInstance());

        // Act: Check the log file for login entry
        String logEntry = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            logEntry = reader.readLine();
        } catch (IOException e) {
            // Expecting this block to execute
        }

        // Assert: Check that the log entry is still empty or not written due to file not found
        assertEquals("", logEntry);
    }

    @Test
    public void testCheckTemp_fileDoesNotExist() {
        // Arrange: Ensure the temp file does not exist
        File tempFile = new File(TEMP_FILE_PATH);
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // Act: Check if the temporary file exists
        boolean result = posSystem.checkTemp();

        // Assert: Check that the method returns false
        assertFalse(result);
    }

    @Test
    public void testContinueFromTemp_InvalidType() {
        // Arrange: Create a temp file with an invalid type
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("InvalidType");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Continue from temp
        String result = posSystem.continueFromTemp(1234567890L);

        // Assert: Check that the result is an empty string for an invalid type
        assertEquals("", result);
    }

    @Test
    public void testLogOutToFile_FileNotFound() {
        // Arrange: Set an invalid log file path
        posSystem.logOutToFile("user1", "John Doe", "Cashier", Calendar.getInstance());

        // Act: Check the log file for logout entry
        String logEntry = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            logEntry = reader.readLine();
        } catch (IOException e) {
            // Expecting this block to execute
        }

        // Assert: Check that the log entry is still empty or not written due to file not found
        assertEquals("", logEntry);
    }

    @Test
    public void testLogIn_UserNotFound() {
        // Arrange: Set an invalid username and password
        String invalidUsername = "nonExistentUser";
        String invalidPassword = "wrongPassword";

        // Act: Attempt to log in with invalid credentials
        int result = posSystem.logIn(invalidUsername, invalidPassword);

        // Assert: Check that the login fails and returns 0
        assertEquals(0, result);
    }

    @Test
    public void testLogIn_CashierSuccess() {
        POSSystem posSystem = new POSSystem();
        posSystem.readFile(); // Populate employees
        
        int result = posSystem.logIn("110002", "lehigh2016"); // Cashier login
        assertEquals(1, result); // Expect Cashier status
    }

    @Test
    public void testLogIn_AdminSuccess() {
        POSSystem posSystem = new POSSystem();
        posSystem.readFile(); // Populate employees
        
        int result = posSystem.logIn("110001", "1"); // Admin login
        assertEquals(2, result); // Expect Admin status
    }

    
    @Test
    public void testLogOutToFile_FileNotFoundException() {
        POSSystem posSystem = new POSSystem();
        // Set an invalid path to trigger FileNotFoundException
        posSystem.unixOS = false;
        
        posSystem.logOutToFile("invalidUser", "Invalid Name", "Admin", Calendar.getInstance());
        // Check that no exceptions are thrown and log file doesn't crash
    }

    @Test
    public void testLogOutToFile_IOException() throws Exception {
        POSSystem posSystem = new POSSystem();
        File file = new File("Database/employeeLogfile.txt");
        file.setReadOnly(); // Simulate IOException by setting the file to read-only
        
        posSystem.logOutToFile("invalidUser", "Invalid Name", "Admin", Calendar.getInstance());
        // Check that no exceptions are thrown and log file doesn't crash
        
        file.setWritable(true); // Clean up by restoring write permissions
    }

    
    
    @Test
    public void testContinueFromTemp_Sale1() throws Exception {
        POSSystem posSystem = new POSSystem();
        // Create a mock temp file with "Sale" as content
        BufferedWriter writer = new BufferedWriter(new FileWriter("Database/temp.txt"));
        writer.write("Sale");
        writer.close();
        
        String result = posSystem.continueFromTemp(1234567890);
        assertEquals("Sale", result);
    }

    @Test
    public void testContinueFromTemp_Rental1() throws Exception {
        POSSystem posSystem = new POSSystem();
        // Create a mock temp file with "Rental" as content
        BufferedWriter writer = new BufferedWriter(new FileWriter("Database/temp.txt"));
        writer.write("Rental");
        writer.close();
        
        String result = posSystem.continueFromTemp(1234567890);
        assertEquals("Rental", result);
    }

    @Test
    public void testContinueFromTemp_Return1() throws Exception {
        POSSystem posSystem = new POSSystem();
        // Create a mock temp file with "Return" as content
        BufferedWriter writer = new BufferedWriter(new FileWriter("Database/temp.txt"));
        writer.write("Return");
        writer.close();
        
        String result = posSystem.continueFromTemp(1234567890);
        assertEquals("Return", result);
    }

    
    
    @Test
    public void testCheckTemp_True() throws Exception {
        POSSystem posSystem = new POSSystem();
        // Create a temp file to simulate the condition
        File tempFile = new File("Database/temp.txt");
        tempFile.createNewFile();
        
        assertTrue(posSystem.checkTemp()); // Expect true since the file exists
    }

    @Test
    public void testCheckTemp_False() {
        POSSystem posSystem = new POSSystem();
        // Ensure temp file is deleted to trigger the false condition
        File tempFile = new File("Database/temp.txt");
        tempFile.delete();
        
        assertFalse(posSystem.checkTemp()); // Expect false since the file doesn't exist
    }

    
    
    @Test
    public void testReadFile_FileNotFoundException() {
        POSSystem posSystem = new POSSystem();
        // Set an invalid path for employeeDatabase
        POSSystem.employeeDatabase = "invalid/path/employeeDatabase.txt";
        
        posSystem.readFile();
        // Check if the exception is caught and no crash occurs
    }

    @Test
    public void testReadFile_IOException() throws Exception {
        POSSystem posSystem = new POSSystem();
        File file = new File("Database/employeeDatabase.txt");
        file.setReadOnly(); // Simulate IOException by setting the file to read-only
        
        posSystem.readFile();
        // Ensure no crash or unhandled exception
        
        file.setWritable(true); // Restore write permissions
    }

    
    @Test
    public void testLogInToFile_FileNotFoundException() {
        POSSystem posSystem = new POSSystem();
        // Set an invalid path for log file
        POSSystem.employeeDatabase = "invalid/path/employeeLogfile.txt";
        
        posSystem.logInToFile("110001", "Harry Larry", "Admin", Calendar.getInstance());
        // Check that no crash or unhandled exception occurs
    }

    @Test
    public void testLogInToFile_IOException() throws Exception {
        POSSystem posSystem = new POSSystem();
        File file = new File("Database/employeeLogfile.txt");
        file.setReadOnly(); // Simulate IOException by setting the file to read-only
        
        posSystem.logInToFile("110001", "Harry Larry", "Admin", Calendar.getInstance());
        // Ensure no crash or unhandled exception
        
        file.setWritable(true); // Restore write permissions
    }
   
    
    
    @Test
    public void testReadFile_WindowsOS_FileNotFound() {
        POSSystem posSystem = new POSSystem() {
            @Override
            public void readFile() {
                System.setProperty("os.name", "Windows");
                super.readFile();
            }
        };

        // Redirect the file path to a non-existing file to trigger FileNotFoundException
        POSSystem.employeeDatabase = "non_existent_database.txt";
        posSystem.readFile();
    }

    @Test
    public void testReadFile_IOError() throws Exception {
        POSSystem posSystem = new POSSystem();
        File mockFile = new File(POSSystem.employeeDatabase);
        
        // Temporarily make the file non-readable to simulate an IOException
        if (mockFile.exists()) {
            mockFile.setReadable(false);
        }
        posSystem.readFile();
        
        // Restore file to readable
        if (mockFile.exists()) {
            mockFile.setReadable(true);
        }
    }

    @Test
    public void testLogInToFile_FileNotFound1() {
        POSSystem posSystem = new POSSystem();

        // Redirect the file path to a non-existent directory to trigger FileNotFoundException
        String username = "testUser";
        String name = "Test User";
        String position = "Cashier";
        Calendar cal = new GregorianCalendar();
        POSSystem.employeeDatabase = "non_existent_directory/logfile.txt";

        posSystem.logInToFile(username, name, position, cal);
    }

    @Test
    public void testLogInToFile_IOException1() {
        POSSystem posSystem = new POSSystem();
        
        try {
            // Temporarily make the file non-writable to simulate an IOException
            File mockFile = new File("Database/employeeLogfile.txt");
            if (mockFile.exists()) {
                mockFile.setWritable(false);
            }

            String username = "testUser";
            String name = "Test User";
            String position = "Cashier";
            Calendar cal = new GregorianCalendar();
            posSystem.logInToFile(username, name, position, cal);

            // Restore the file to writable
            if (mockFile.exists()) {
                mockFile.setWritable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCheckTemp_ReturnFalse() {
        POSSystem posSystem = new POSSystem();

        // Ensure the temp file doesn't exist to test the false return condition
        File tempFile = new File("Database/temp.txt");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        assertFalse(posSystem.checkTemp());
    }

    @Test
    public void testContinueFromTemp_FileNotFound() {
        POSSystem posSystem = new POSSystem();
        
        // Redirect to a non-existent temp file
        POSSystem.employeeDatabase = "non_existent_temp.txt";
        String result = posSystem.continueFromTemp(1234567890L);
        assertEquals("", result);  // Expecting an empty string since no temp file is found
    }

    @Test
    public void testContinueFromTemp_Sale11() throws Exception {
        POSSystem posSystem = new POSSystem();

        // Simulate a temp file with "Sale" as the content
        FileWriter writer = new FileWriter("Database/temp.txt");
        writer.write("Sale\n");
        writer.close();

        String result = posSystem.continueFromTemp(1234567890L);
        assertEquals("Sale", result);
    }

    @Test
    public void testContinueFromTemp_Rental11() throws Exception {
        POSSystem posSystem = new POSSystem();

        // Simulate a temp file with "Rental" as the content
        FileWriter writer = new FileWriter("Database/temp.txt");
        writer.write("Rental\n");
        writer.close();

        String result = posSystem.continueFromTemp(1234567890L);
        assertEquals("Rental", result);
    }

    @Test
    public void testContinueFromTemp_Return11() throws Exception {
        POSSystem posSystem = new POSSystem();

        // Simulate a temp file with "Return" as the content
        FileWriter writer = new FileWriter("Database/temp.txt");
        writer.write("Return\n");
        writer.close();

        String result = posSystem.continueFromTemp(1234567890L);
        assertEquals("Return", result);
    }

    @Test
    public void testLogOutToFile_FileNotFound1() {
        POSSystem posSystem = new POSSystem();

        // Redirect to a non-existent file to simulate FileNotFoundException
        String username = "testUser";
        String name = "Test User";
        String position = "Cashier";
        Calendar cal = new GregorianCalendar();
        POSSystem.employeeDatabase = "non_existent_directory/logfile.txt";

        posSystem.logOutToFile(username, name, position, cal);
    }

    @Test
    public void testLogOutToFile_IOException1() {
        POSSystem posSystem = new POSSystem();

        try {
            // Temporarily make the file non-writable to simulate an IOException
            File mockFile = new File("Database/employeeLogfile.txt");
            if (mockFile.exists()) {
                mockFile.setWritable(false);
            }

            String username = "testUser";
            String name = "Test User";
            String position = "Cashier";
            Calendar cal = new GregorianCalendar();
            posSystem.logOutToFile(username, name, position, cal);

            // Restore the file to writable
            if (mockFile.exists()) {
                mockFile.setWritable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLogIn_InvalidPassword() {
        POSSystem posSystem = new POSSystem();

        // Add mock employee data
        posSystem.employees.add(new Employee("testUser", "Test User", "Admin", "correctPassword"));

        // Try to log in with incorrect password
        int result = posSystem.logIn("testUser", "wrongPassword");
        assertEquals(0, result);
    }

    @Test
    public void testLogIn_Admin() {
        POSSystem posSystem = new POSSystem();

        // Add mock employee data
        posSystem.employees.add(new Employee("testUser", "Test User", "Admin", "correctPassword"));

        // Try to log in with correct password and expect Admin role
        int result = posSystem.logIn("testUser", "correctPassword");
        assertEquals(2, result);
    }

    @Test
    public void testLogIn_Cashier() {
        POSSystem posSystem = new POSSystem();

        // Add mock employee data
        posSystem.employees.add(new Employee("testUser", "Test User", "Cashier", "correctPassword"));

        // Try to log in with correct password and expect Cashier role
        int result = posSystem.logIn("testUser", "correctPassword");
        assertEquals(1, result);
    }

}
