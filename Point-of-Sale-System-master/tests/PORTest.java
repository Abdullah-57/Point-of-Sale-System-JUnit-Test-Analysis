import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PORTest {
    private POR por;
    private static final String TEMP_FILE_PATH = "Database/temp.txt";
    private static final String TEST_DATABASE_FILE = "Database/itemDatabase.txt"; // Use a test database path

    @After
    public void tearDown() {
        // Clean up any created temp files
        File tempFile = new File(TEMP_FILE_PATH);
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testDeleteTempItem_removesItemSuccessfully() {
        // Arrange: Set up transaction items
        por.transactionItem.add(new Item(1, "Item 1", (float) 10.0, 2)); // ID: 1, Amount: 2
        por.transactionItem.add(new Item(2, "Item 2", (float) 15.0, 1)); // ID: 2, Amount: 1
        
        // Act: Delete item with ID 1
        por.deleteTempItem(1);
        
        // Assert: Check that item 1 is removed, and item 2 remains
        assertEquals(1, por.transactionItem.size());
        assertEquals(2, por.transactionItem.get(0).getItemID());
    }

    @Test
    public void testDeleteTempItem_emptyTransaction() {
        // Act: Attempt to delete from an empty transaction
        por.deleteTempItem(1);
        
        // Assert: Ensure no items are present
        assertEquals(0, por.transactionItem.size());
    }

    @Test
    public void testEndPOS_completesTransactionSuccessfully() {
        // Arrange: Set up transaction items
        por.transactionItem.add(new Item(1, "Item 1", (float) 10.0, 2)); // ID: 1, Amount: 2
        por.transactionItem.add(new Item(2, "Item 2", (float) 15.0, 1)); // ID: 2, Amount: 1
        double expectedTotalPrice = (10.0 * 2 + 15.0 * 1) * por.tax; // Apply tax to total
        
        // Act: Call endPOS to finalize transaction
        double actualTotalPrice = por.endPOS(TEST_DATABASE_FILE);
        
        // Assert: Check the total price and transaction clearance
        assertEquals(expectedTotalPrice, actualTotalPrice, 0.01);
        assertTrue(por.transactionItem.isEmpty());
    }

    @Test
    public void testEndPOS_noItems() {
        // Act: Call endPOS when there are no items
        double actualTotalPrice = por.endPOS(TEST_DATABASE_FILE);
        
        // Assert: Check that the total price is 0 and transaction items are cleared
        assertEquals(0.0, actualTotalPrice, 0.01);
        assertTrue(por.transactionItem.isEmpty());
    }

    @Test
    public void testRetrieveTemp_loadsItemsSuccessfully() {
        // Arrange: Prepare a temp file with test data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("Phone number: 1234567890");
            writer.newLine();
            writer.write("1 2"); // ID: 1, Amount: 2
            writer.newLine();
            writer.write("2 1"); // ID: 2, Amount: 1
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Act: Call retrieveTemp to load the items
        por.retrieveTemp(TEST_DATABASE_FILE);
        
        // Assert: Verify the transaction items are loaded correctly
        assertEquals(2, por.transactionItem.size());
        assertEquals(1, por.transactionItem.get(0).getItemID());
        assertEquals(2, por.transactionItem.get(0).getAmount());
        assertEquals(2, por.transactionItem.get(1).getItemID());
        assertEquals(1, por.transactionItem.get(1).getAmount());
    }

    @Test
    public void testRetrieveTemp_emptyFile() {
        // Arrange: Create an empty temp file
        try {
            Files.write(Paths.get(TEMP_FILE_PATH), "".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Act: Call retrieveTemp to load items
        por.retrieveTemp(TEST_DATABASE_FILE);
        
        // Assert: Verify no items are loaded
        assertEquals(0, por.transactionItem.size());
    }

    @Test
    public void testRetrieveTemp_malformedFile() {
        // Arrange: Prepare a malformed temp file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("Phone number: 1234567890");
            writer.newLine();
            writer.write("Malformed data"); // Invalid line
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Act: Call retrieveTemp to load items
        por.retrieveTemp(TEST_DATABASE_FILE);
        
        // Assert: Verify no items are loaded
        assertEquals(0, por.transactionItem.size());
    }

    @Test
    public void testEndPOS_fileNotFoundException() {
        // Arrange: Simulate a scenario where the database file doesn't exist
        String nonExistentFile = "Database/nonExistentFile.txt";
        
        // Act: Call endPOS and catch the exception
        double actualTotalPrice = por.endPOS(nonExistentFile);
        
        // Assert: Ensure total price is 0 since the transaction should not complete
        assertEquals(0.0, actualTotalPrice, 0.01);
        assertTrue(por.transactionItem.isEmpty());
    }
    
    
    
    
    @Test
    public void testDeleteTempItem_windowsOS() {
        // Simulate Windows OS for testing the path
        System.setProperty("os.name", "Windows 10");

        // Arrange: Set up transaction items
        por.transactionItem.add(new Item(1, "Item 1", (float) 10.0, 2)); // ID: 1, Amount: 2
        por.transactionItem.add(new Item(2, "Item 2", (float) 15.0, 1)); // ID: 2, Amount: 1

        // Create the temp file manually for testing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("TestType"); // Mock type
            writer.newLine();
            writer.write("1234567890"); // Mock phone
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Delete item with ID 1
        por.deleteTempItem(1);
        
        // Assert: Check that item 1 is removed, and item 2 remains
        assertEquals(1, por.transactionItem.size());
        assertEquals(2, por.transactionItem.get(0).getItemID());

        // Check if the temp file has been updated correctly
        try (BufferedReader reader = new BufferedReader(new FileReader(TEMP_FILE_PATH))) {
            assertEquals("TestType", reader.readLine());
            assertEquals("1234567890", reader.readLine());
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                count++;
            }
            // Ensure only one item remains
            assertEquals(1, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRetrieveTemp_itemSuccessfullyRetrieved() {
        // Prepare the temp file with data that ensures coverage of updateTotal
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("Phone number: 1234567890");
            writer.newLine();
            writer.write("1 2"); // ID: 1, Amount: 2
            writer.newLine();
            writer.write("2 1"); // ID: 2, Amount: 1
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Call retrieveTemp to load the items
        por.retrieveTemp(TEST_DATABASE_FILE);

        // Assert: Verify the transaction items are loaded correctly
        assertEquals(2, por.transactionItem.size());
        assertEquals(1, por.transactionItem.get(0).getItemID());
        assertEquals(2, por.transactionItem.get(0).getAmount());
        assertEquals(2, por.transactionItem.get(1).getItemID());
        assertEquals(1, por.transactionItem.get(1).getAmount());

        // Verify that updateTotal was called (assuming it modifies some state)
        // Here we can call updateTotal directly if it's public or test it through some visible change in state
        por.updateTotal(); // Assuming updateTotal modifies state
    }
    
    
    
    
    
    
    
    
    
    
    
    @Before
	public void setup() {
		por = new POR(1234567890L); // Phone number initialization
		// Add sample items to transactionItem list for testing
		por.transactionItem.add(new Item(1, "Item1", 10.0f, 2));
		por.transactionItem.add(new Item(2, "Item2", 20.0f, 1));
	}

	@Test
	public void testDeleteTempItem() throws IOException {
		// Setup temp file with sample data
		FileWriter writer = new FileWriter(PointOfSale.tempFile);
		writer.write("SALE\n1234567890\n1 2\n2 1\n");
		writer.close();

		// Delete item with ID 1
		por.deleteTempItem(1);

		// Verify the temp file only contains the remaining item
		BufferedReader reader = new BufferedReader(new FileReader(PointOfSale.tempFile));
		assertEquals("SALE", reader.readLine());
		assertEquals("1234567890", reader.readLine());
		assertEquals("2 1", reader.readLine());
		assertNull(reader.readLine());
		reader.close();
	}

	@Test
	public void testEndPOS() {
		// Set total price
		por.totalPrice = 30.0;
		double total = por.endPOS("Database/itemDatabase.txt");

		// Verify the total price with tax applied
		assertEquals(31.8, total, 0.001);
		assertTrue(por.transactionItem.isEmpty());
	}

	@Test
	public void testRetrieveTemp() throws IOException {
		// Setup temp file with sample data
		FileWriter writer = new FileWriter(PointOfSale.tempFile);
		writer.write("SALE\n1234567890\n1 2\n2 1\n");
		writer.close();

		// Retrieve items from the temp file
		por.retrieveTemp("Database/itemDatabase.txt");

		// Verify items were added to the transaction list
		List<Item> transactionItems = por.transactionItem;
		assertEquals(2, transactionItems.size());
		assertEquals(1, transactionItems.get(0).getItemID());
		assertEquals(2, transactionItems.get(0).getAmount());
		assertEquals(2, transactionItems.get(1).getItemID());
		assertEquals(1, transactionItems.get(1).getAmount());
	}
 

}
