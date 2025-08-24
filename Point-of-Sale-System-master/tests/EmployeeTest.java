import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class EmployeeTest {
    private Employee employee;

    @Before
    public void setUp() {
        // Initialize an Employee instance before each test
        employee = new Employee("user123", "John Doe", "Manager", "password123");
    }

    @Test
    public void testConstructor() {
        // Test the constructor and ensure the object is not null
        assertNotNull("Employee object should be created", employee);
        assertEquals("user123", employee.getUsername());
        assertEquals("John Doe", employee.getName());
        assertEquals("Manager", employee.getPosition());
        assertEquals("password123", employee.getPassword());
    }

    @Test
    public void testGetUsername() {
        // Test the getUsername method
        assertEquals("user123", employee.getUsername());
    }

    @Test
    public void testGetName() {
        // Test the getName method
        assertEquals("John Doe", employee.getName());
    }

    @Test
    public void testGetPosition() {
        // Test the getPosition method
        assertEquals("Manager", employee.getPosition());
    }

    @Test
    public void testGetPassword() {
        // Test the getPassword method
        assertEquals("password123", employee.getPassword());
    }

    @Test
    public void testSetName() {
        // Test the setName method
        employee.setName("Jane Doe");
        assertEquals("Jane Doe", employee.getName());
    }

    @Test
    public void testSetPosition() {
        // Test the setPosition method
        employee.setPosition("Cashier");
        assertEquals("Cashier", employee.getPosition());
    }

    @Test
    public void testSetPassword() {
        // Test the setPassword method
        employee.setPassword("newpassword456");
        assertEquals("newpassword456", employee.getPassword());
    }

    @Test
    public void testSetUsername() {
        // Uncommenting this method in Employee.java would allow testing setUsername
        // employee.setUsername("newuser123");
        // assertEquals("newuser123", employee.getUsername());
    }
}
