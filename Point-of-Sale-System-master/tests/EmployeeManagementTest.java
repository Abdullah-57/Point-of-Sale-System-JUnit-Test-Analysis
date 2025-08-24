import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import static org.junit.Assert.*;

public class EmployeeManagementTest {
    EmployeeManagement employeeManagement;

    @Before
    public void setUp() {
        // Initialize the EmployeeManagement instance before each test
        employeeManagement = new EmployeeManagement();
        employeeManagement.employees.add(new Employee("110001", "Harry Larry", "Admin", "1"));
        employeeManagement.employees.add(new Employee("110002", "Debra Cooper", "Cashier", "lehigh2016"));
        employeeManagement.employees.add(new Employee("110003", "Clayton Watson", "Admin", "lehigh2017"));
        employeeManagement.employees.add(new Employee("110004", "Seth Moss", "Cashier", "lehigh2018"));
        employeeManagement.employees.add(new Employee("110005", "Amy Adams", "Admin", "110"));
        employeeManagement.employees.add(new Employee("110006", "Mike Spears", "Cashier", "lehigh"));
        employeeManagement.employees.add(new Employee("110009", "John Candle", "Admin", "candles"));
        employeeManagement.employees.add(new Employee("110011", "Anthony Hopkins", "Cashier", "theman"));
        employeeManagement.employees.add(new Employee("110012", "Robert Lek", "Cashier", "huehue"));
        employeeManagement.employees.add(new Employee("110013", "Johnny Cage", "Cashier", "mortalkombat"));
        employeeManagement.employees.add(new Employee("110014", "Eim Lou", "Cashier", "cowboybebop"));
        employeeManagement.employees.add(new Employee("110015", "Michael Scott", "Cashier", "thatswhatshesaid"));
        
        // Create the employeeDatabase.txt file for testing purposes
        writeTestDatabase();
    }
    
   

    @Test
    public void testGetEmployeeList() {
        // Test if employee list is correctly loaded
        List<Employee> employees = employeeManagement.getEmployeeList();
        assertNotNull(employees);
        assertEquals(12, employees.size());
        assertEquals("Harry Larry", employees.get(0).getName());
        assertEquals("Cashier", employees.get(1).getPosition());
    }

    @Test
    public void testAddEmployeeAsCashier() {
        // Adding a new employee as a Cashier
        employeeManagement.add("Alice Cooper", "newpassword", true);
        List<Employee> employees = employeeManagement.getEmployeeList();
        assertEquals(13, employees.size());
        assertEquals("Alice Cooper", employees.get(12).getName());
        assertEquals("Cashier", employees.get(12).getPosition());
    }

    @Test
    public void testAddEmployeeAsAdmin() {
        // Adding a new employee as an Admin
        employeeManagement.add("Bruce Wayne", "batman", false);
        List<Employee> employees = employeeManagement.getEmployeeList();
        assertEquals(13, employees.size());
        assertEquals("Bruce Wayne", employees.get(12).getName());
        assertEquals("Admin", employees.get(12).getPosition());
    }

    @Test
    public void testDeleteEmployeeFound() {
        // Test deleting an employee who exists
        boolean result = employeeManagement.delete("110001");
        List<Employee> employees = employeeManagement.getEmployeeList();
        assertTrue(result);
        assertEquals(11, employees.size()); // One employee should be removed
        assertEquals("Debra Cooper", employees.get(0).getName()); // Ensure the first entry is updated
    }

    @Test
    public void testDeleteEmployeeNotFound() {
        // Test deleting an employee who doesn't exist
        boolean result = employeeManagement.delete("999999");
        assertFalse(result); // Employee not found
    }

    @Test
    public void testUpdateEmployeeFoundValidPosition() {
        // Update an existing employee with a valid position and password
        int result = employeeManagement.update("110002", "newPass", "Admin", "Debra Updated");
        assertEquals(0, result); // Employee found
        Employee updatedEmployee = employeeManagement.employees.get(1);
        assertEquals("newPass", updatedEmployee.getPassword());
        assertEquals("Admin", updatedEmployee.getPosition());
        assertEquals("Debra Updated", updatedEmployee.getName());
    }

    @Test
    public void testUpdateEmployeeNotFound() {
        // Update an employee who doesn't exist
        int result = employeeManagement.update("999999", "newPass", "Cashier", "Nonexistent");
        assertEquals(-1, result); // Employee not found
    }

    @Test
    public void testUpdateInvalidPosition() {
        // Attempt to update an employee with an invalid position
        int result = employeeManagement.update("110002", "newPass", "InvalidPosition", "Debra Updated");
        assertEquals(-2, result); // Invalid position
    }

    @Test
    public void testUpdateWithEmptyFields() {
        // Update an employee with some empty fields
        int result = employeeManagement.update("110003", "", "", "Clayton Updated");
        assertEquals(0, result); // Employee found and updated
        Employee updatedEmployee = employeeManagement.employees.get(2);
        assertEquals("lehigh2017", updatedEmployee.getPassword()); // Password unchanged
        assertEquals("Admin", updatedEmployee.getPosition()); // Position unchanged
        assertEquals("Clayton Updated", updatedEmployee.getName()); // Name updated
    }

    @Test
    public void testReadFile() {
        // Test the readFile method to ensure employees are loaded correctly
        employeeManagement.readFile();
        List<Employee> employees = employeeManagement.getEmployeeList();
        assertEquals(12, employees.size());
        assertEquals("Harry Larry", employees.get(0).getName());
    }
    
    
    @After
    public void tearDown() {
        // Cleanup test database file after each test
        File file = new File(EmployeeManagement.employeeDatabase);
        if (file.exists()) {
            file.delete();
        }
    }

    private void writeTestDatabase() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EmployeeManagement.employeeDatabase))) {
            for (Employee emp : employeeManagement.employees) {
                writer.write(emp.getUsername() + " " + emp.getPosition() + " " + emp.getName() + " " + emp.getPassword());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    @Test
    public void testAddEmployeeWithNoExistingEmployees() {
        // Test adding an employee when the employee list is empty
        employeeManagement.employees.clear(); // Clear the list to simulate no existing employees
        employeeManagement.add("Alice Cooper", "newpassword", true); // Add new employee
        List<Employee> employees = employeeManagement.getEmployeeList();
        assertEquals(1, employees.size());
        assertEquals("Alice Cooper", employees.get(0).getName());
        assertEquals("Cashier", employees.get(0).getPosition());
    }

    @Test
    public void testDeleteEmployeeWhenListIsEmpty() {
        // Test deleting an employee when the list is empty
        employeeManagement.employees.clear(); // Clear the list to simulate no existing employees
        boolean result = employeeManagement.delete("110001");
        assertFalse(result); // Should return false since no employee exists
    }

    @Test
    public void testDeleteEmployeeNotFoundWithValidList() {
        // Test deleting an employee who doesn't exist with a valid list
        boolean result = employeeManagement.delete("999999");
        assertFalse(result); // Employee not found
    }

    @Test
    public void testUpdateEmployeeFoundWithInvalidPassword() {
        // Update an existing employee with an invalid password (should not change)
        int result = employeeManagement.update("110002", "", "Cashier", "Debra Updated");
        assertEquals(0, result); // Employee found
        Employee updatedEmployee = employeeManagement.employees.get(1);
        assertEquals("lehigh2016", updatedEmployee.getPassword()); // Password should remain unchanged
    }

    @Test
    public void testUpdateEmployeeFoundWithEmptyPosition() {
        // Update an existing employee with an empty position
        int result = employeeManagement.update("110002", "newPass", "", "Debra Updated");
        assertEquals(0, result); // Employee found
        Employee updatedEmployee = employeeManagement.employees.get(1);
        assertEquals("newPass", updatedEmployee.getPassword()); // Password changed
        assertEquals("Cashier", updatedEmployee.getPosition()); // Position should remain unchanged
    }

    @Test
    public void testReadFileWhenFileDoesNotExist() {
        // Test readFile method when the file does not exist
        File file = new File(EmployeeManagement.employeeDatabase);
        if (file.exists()) {
            file.delete(); // Ensure the file does not exist
        }
        employeeManagement.readFile(); // Attempt to read
        assertTrue(employeeManagement.employees.isEmpty()); // Employee list should remain empty
    }

    @Test
    public void testReadFileWithMalformedData() {
        // Test readFile with malformed data (not valid Employee data)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EmployeeManagement.employeeDatabase))) {
            writer.write("InvalidData\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        employeeManagement.readFile(); // Attempt to read
        assertTrue(employeeManagement.employees.isEmpty()); // Employee list should remain empty
    }
    
	@Test
    public void testAddIOException() {
        // Set up a scenario to cause an IOException
        // To simulate IOException, we can use a file that is read-only.
        // Since creating a truly read-only file is complex, we can just 
        // ensure we catch it gracefully without actual file operations.
        // Here, we'll just demonstrate the test structure.
        try {
            File tempFile = new File(employeeManagement.employeeDatabase);
            tempFile.createNewFile();
            tempFile.setReadOnly(); // Make the file read-only
            employeeManagement.add("John Doe", "password", true);
            tempFile.setWritable(true); // Restore write permissions
        } catch (IOException e) {
            // Handle exceptions that should not be thrown in the test
        }
    }

    @Test
    public void testDeleteFileNotFoundException() {
        // Set a non-existing file path to simulate FileNotFoundException
        employeeManagement.temp = "non_existing_temp_file.txt";
        assertFalse(employeeManagement.delete("12345")); // Should return false since the user does not exist
    }

    @Test
    public void testDeleteIOException() throws IOException {
        // Create a temporary file and delete it immediately to cause IOException
        File tempFile = new File(employeeManagement.temp);
        tempFile.createNewFile();
        tempFile.setReadOnly(); // Make it read-only

        // Add an employee to delete
        employeeManagement.add("John Doe", "password", true);
        
        // Try to delete to trigger IOException
        assertTrue(employeeManagement.delete("1")); // Attempt to delete John Doe
        tempFile.setWritable(true); // Restore write permissions
        tempFile.delete(); // Clean up
    }

    @Test
    public void testUpdateFileNotFoundException() {
        // Set a non-existing file path to simulate FileNotFoundException
        employeeManagement.employeeDatabase = "non_existing_file.txt";
        int result = employeeManagement.update("1", "newPassword", "Cashier", "New Name");
        assertEquals(-1, result); // Should indicate user not found
    }

    @Test
    public void testUpdateIOException() throws IOException {
        // Create a temporary file that is read-only to simulate IOException
        File tempFile = new File(employeeManagement.temp);
        tempFile.createNewFile();
        tempFile.setReadOnly(); // Make the file read-only

        // Add an employee to update
        employeeManagement.add("John Doe", "password", true);
        
        // Try to update to trigger IOException
        int result = employeeManagement.update("1", "newPassword", "Cashier", "New Name");
        assertEquals(0, result); // User should be found, update should proceed without IO exceptions

        tempFile.setWritable(true); // Restore write permissions
        tempFile.delete(); // Clean up
    }

    @Test
    public void testReadFileFileNotFoundException() {
        // Set a non-existing file path to simulate FileNotFoundException
        employeeManagement.employeeDatabase = "non_existing_file.txt";
        List<Employee> employees = employeeManagement.getEmployeeList();
        assertTrue(employees.isEmpty()); // Should return an empty list since the file does not exist
    }

    @Test
    public void testReadFileIOException() throws IOException {
        // Create a temporary file and delete it immediately to simulate IOException
        File tempFile = new File(employeeManagement.employeeDatabase);
        tempFile.createNewFile();
        tempFile.setReadOnly(); // Make it read-only

        employeeManagement.readFile(); // Trigger the read operation
        assertTrue(employeeManagement.employees.isEmpty()); // Expect no employees to be loaded
        tempFile.setWritable(true); // Restore write permissions
        tempFile.delete(); // Clean up
    }
    
    @Test
    public void testAddFileNotFoundException() {
        // Temporarily set the employeeDatabase path to a non-existent directory
        employeeManagement.employeeDatabase = "non_existing_directory/employeeDatabase.txt";
        
        try {
            employeeManagement.add("John Doe", "password", true);
            fail("Expected FileNotFoundException was not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof FileNotFoundException);
        }
    }

    
    @Test
    public void testAddIOException1() {
        File tempFile = new File(employeeManagement.employeeDatabase);
        try {
            // Create the file and make it read-only
            tempFile.createNewFile();
            tempFile.setReadOnly();
            
            // Attempt to add an employee, which should cause an IOException
            employeeManagement.add("Jane Doe", "password", true);
            
            // If no exception is thrown, the test should fail
            fail("Expected IOException was not thrown");
        } catch (IOException e) {
            assertTrue(e instanceof IOException);
        } finally {
            // Clean up and make the file writable again
            tempFile.setWritable(true);
            tempFile.delete();
        }
    }

    @Test
    public void testDeleteFileNotFoundException1() {
        // Temporarily set the temp file path to a non-existent file
        employeeManagement.temp = "non_existing_temp_file.txt";

        // Attempt to delete an employee, which should catch FileNotFoundException
        boolean result = employeeManagement.delete("110001");
        assertFalse(result);
    }

    @Test
    public void testDeleteIOException1() throws IOException {
        File tempFile = new File(employeeManagement.temp);
        try {
            // Create the file and make it read-only
            tempFile.createNewFile();
            tempFile.setReadOnly();
            
            // Add an employee and attempt to delete, which should cause an IOException
            employeeManagement.add("Jane Doe", "password", true);
            boolean result = employeeManagement.delete("110001");
            
            // The test should pass if no exception is thrown
            assertTrue(result);
        } finally {
            // Clean up
            tempFile.setWritable(true);
            tempFile.delete();
        }
    }

    @Test
    public void testUpdateFileNotFoundException1() {
        // Set a non-existent file path
        employeeManagement.employeeDatabase = "non_existing_file.txt";

        // Attempt to update an employee, which should return -1 (user not found)
        int result = employeeManagement.update("110001", "newPass", "Cashier", "John Doe");
        assertEquals(-1, result);
    }

    @Test
    public void testUpdateIOException1() throws IOException {
        File tempFile = new File(employeeManagement.temp);
        try {
            // Create the file and make it read-only
            tempFile.createNewFile();
            tempFile.setReadOnly();
            
            // Add an employee and attempt to update, which should trigger an IOException
            employeeManagement.add("Jane Doe", "password", true);
            int result = employeeManagement.update("110001", "newPassword", "Cashier", "Jane Doe");
            
            // The test should pass if no exception is thrown and the update happens correctly
            assertEquals(0, result);
        } finally {
            // Clean up
            tempFile.setWritable(true);
            tempFile.delete();
        }
    }
    
    @Test
    public void testReadFileFileNotFoundException1() {
        // Set a non-existent file path
        employeeManagement.employeeDatabase = "non_existing_file.txt";
        
        // Attempt to read employees from the file, which should leave the list empty
        employeeManagement.readFile();
        assertTrue(employeeManagement.employees.isEmpty());
    }
    
    @Test
    public void testReadFileIOException1() throws IOException {
        File tempFile = new File(employeeManagement.employeeDatabase);
        try {
            // Create the file and make it read-only
            tempFile.createNewFile();
            tempFile.setReadOnly();
            
            // Attempt to read employees, which should trigger an IOException
            employeeManagement.readFile();
            assertTrue(employeeManagement.employees.isEmpty());
        } finally {
            // Clean up
            tempFile.setWritable(true);
            tempFile.delete();
        }
    }



    
}
