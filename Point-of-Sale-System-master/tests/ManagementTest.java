import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ManagementTest {

    private Management management;
    private static final String TEST_USER_DB = "Database/userDatabase.txt";

    @Before
    public void setUp() throws IOException {
        management = new Management();
        // Create a mock user database file for testing
        new File("Database").mkdir();
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(TEST_USER_DB)));
        out.println("User Database\n1234567890 1022,12/30/22,false");
        out.close();
    }

    @After
    public void tearDown() {
        File file = new File(TEST_USER_DB);
        file.delete();
    }

    @Test
    public void testCheckUserExists() {
        assertTrue(management.checkUser(1234567890L));
    }

    @Test
    public void testCheckUserNotExists() {
        assertFalse(management.checkUser(9876543210L));
    }

    @Test
    public void testCheckUserFileNotFoundException() {
        // Rename the file to force FileNotFoundException
        new File(TEST_USER_DB).renameTo(new File("Database/userDatabase_backup.txt"));
        assertFalse(management.checkUser(1234567890L));
        // Restore the file
        new File("Database/userDatabase_backup.txt").renameTo(new File(TEST_USER_DB));
    }

    @Test
    public void testCheckUserIOException() throws IOException {
        // Create a file with restricted access to trigger IOException
        File file = new File(TEST_USER_DB);
        file.setReadable(false);
        assertFalse(management.checkUser(1234567890L));
        // Restore permissions
        file.setReadable(true);
    }

    @Test
    public void testGetLatestReturnDateNoOutstandingReturns() {
        List<ReturnItem> returns = management.getLatestReturnDate(9876543210L);
        assertTrue(returns.isEmpty());
    }

    @Test
    public void testGetLatestReturnDateOutstandingReturns() {
        List<ReturnItem> returns = management.getLatestReturnDate(1234567890L);
        assertEquals(1, returns.size());
        assertEquals(1022, returns.get(0).getItemID());
    }

    @Test
    public void testGetLatestReturnDateFileNotFoundException() {
        // Rename the file to force FileNotFoundException
        new File(TEST_USER_DB).renameTo(new File("Database/userDatabase_backup.txt"));
        List<ReturnItem> returns = management.getLatestReturnDate(1234567890L);
        assertTrue(returns.isEmpty());
        // Restore the file
        new File("Database/userDatabase_backup.txt").renameTo(new File(TEST_USER_DB));
    }

    @Test
    public void testCreateUser() {
        boolean created = management.createUser(9876543210L);
        assertTrue(created);
        assertTrue(management.checkUser(9876543210L));
    }

    @Test
    public void testCreateUserIOException() throws IOException {
        // Create a file with restricted access to trigger IOException
        File file = new File(TEST_USER_DB);
        file.setWritable(false);
        assertFalse(management.createUser(9876543210L));
        // Restore permissions
        file.setWritable(true);
    }

    @Test
    public void testAddRental() {
        List<Item> rentalList = new ArrayList<>();
        rentalList.add(new Item(1023, "Item2", 10.0f, 1));

        Management.addRental(1234567890L, rentalList);
        List<ReturnItem> returnItems = management.getLatestReturnDate(1234567890L);

        assertFalse(returnItems.isEmpty());
        assertEquals(1023, returnItems.get(1).getItemID());
    }

    @Test
    public void testAddRentalFileNotFoundException() {
        // Rename the file to force FileNotFoundException
        new File(TEST_USER_DB).renameTo(new File("Database/userDatabase_backup.txt"));
        List<Item> rentalList = new ArrayList<>();
        rentalList.add(new Item(1023, "Item2", 10.0f, 1));

        Management.addRental(1234567890L, rentalList);
        // No exception thrown but no rentals added
        List<ReturnItem> returnItems = management.getLatestReturnDate(1234567890L);
        assertTrue(returnItems.isEmpty());
        // Restore the file
        new File("Database/userDatabase_backup.txt").renameTo(new File(TEST_USER_DB));
    }

    @Test
    public void testUpdateRentalStatus() {
        List<ReturnItem> returnedItems = new ArrayList<>();
        returnedItems.add(new ReturnItem(1022, 2));

        management.updateRentalStatus(1234567890L, returnedItems);
        List<ReturnItem> outstandingReturns = management.getLatestReturnDate(1234567890L);

        assertTrue(outstandingReturns.isEmpty());
    }

    @Test
    public void testUpdateRentalStatusFileNotFoundException() {
        // Rename the file to force FileNotFoundException
        new File(TEST_USER_DB).renameTo(new File("Database/userDatabase_backup.txt"));
        List<ReturnItem> returnedItems = new ArrayList<>();
        returnedItems.add(new ReturnItem(1022, 2));

        management.updateRentalStatus(1234567890L, returnedItems);
        List<ReturnItem> outstandingReturns = management.getLatestReturnDate(1234567890L);
        // No exception thrown but no status updated
        assertFalse(outstandingReturns.isEmpty());
        // Restore the file
        new File("Database/userDatabase_backup.txt").renameTo(new File(TEST_USER_DB));
    }

    @Test
    public void testUpdateRentalStatusIOException() throws IOException {
        // Create a file with restricted access to trigger IOException
        File file = new File(TEST_USER_DB);
        file.setWritable(false);
        List<ReturnItem> returnedItems = new ArrayList<>();
        returnedItems.add(new ReturnItem(1022, 2));

        management.updateRentalStatus(1234567890L, returnedItems);
        List<ReturnItem> outstandingReturns = management.getLatestReturnDate(1234567890L);
        // No status should be updated due to IOException
        assertFalse(outstandingReturns.isEmpty());
        // Restore permissions
        file.setWritable(true);
    }
}
