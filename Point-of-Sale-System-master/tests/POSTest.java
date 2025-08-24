import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class POSTest {
    private POS pos;
    private static final String TEMP_FILE_PATH = "Database/temp.txt";
    private static final String TEST_DATABASE_FILE = "Database/itemDatabase.txt"; // Use a test database path
    private static final String INVOICE_RECORD_FILE = "Database/saleInvoiceRecord.txt";

    @After
    public void tearDown() {
        // Clean up any created temp files
        File tempFile = new File(TEMP_FILE_PATH);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        
        // Clean up invoice record file
        File invoiceFile = new File(INVOICE_RECORD_FILE);
        if (invoiceFile.exists()) {
            invoiceFile.delete();
        }

    }

    @Test
    public void testDeleteTempItem_removesItemSuccessfully() {
        // Arrange: Set up transaction items
        pos.transactionItem.add(new Item(1, "Item 1", (float) 10.0, 2)); // ID: 1, Amount: 2
        pos.transactionItem.add(new Item(2, "Item 2", (float) 15.0, 1)); // ID: 2, Amount: 1

        // Act: Delete item with ID 1
        pos.deleteTempItem(1);

        // Assert: Check that item 1 is removed, and item 2 remains
        assertEquals(1, pos.transactionItem.size());
        assertEquals(2, pos.transactionItem.get(0).getItemID());
    }

    @Test
    public void testDeleteTempItem_removesNonExistentItem() {
        // Arrange: Set up transaction items
        pos.transactionItem.add(new Item(1, "Item 1", (float) 10.0, 2)); // ID: 1, Amount: 2
        pos.transactionItem.add(new Item(2, "Item 2", (float) 15.0, 1)); // ID: 2, Amount: 1

        // Act: Attempt to delete a non-existent item (ID 3)
        pos.deleteTempItem(3);

        // Assert: Check that no items are removed
        assertEquals(2, pos.transactionItem.size());
    }

    @Test
    public void testEndPOS_completesTransactionSuccessfully() {
        // Arrange: Set up transaction items
        pos.transactionItem.add(new Item(1, "Item 1", (float) 10.0, 2)); // ID: 1, Amount: 2
        pos.transactionItem.add(new Item(2, "Item 2", (float) 15.0, 1)); // ID: 2, Amount: 1
        double expectedTotalPrice = (10.0 * 2 + 15.0 * 1) * pos.tax; // Apply tax to total

        // Act: Call endPOS to finalize transaction
        double actualTotalPrice = pos.endPOS(TEST_DATABASE_FILE);

        // Assert: Check the total price and transaction clearance
        assertEquals(expectedTotalPrice, actualTotalPrice, 0.01);
        assertTrue(pos.transactionItem.isEmpty());
    }

    @Test
    public void testEndPOS_noItemsTransaction() {
        // Act: Call endPOS without any transaction items
        double actualTotalPrice = pos.endPOS(TEST_DATABASE_FILE);

        // Assert: The total price should be zero and transaction items should be cleared
        assertEquals(0.0, actualTotalPrice, 0.01);
        assertTrue(pos.transactionItem.isEmpty());
    }

    @Test
    public void testRetrieveTemp_loadsItemsSuccessfully() {
        // Arrange: Prepare a temp file with test data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write("Type: Rental");
            writer.newLine();
            writer.write("1 2"); // ID: 1, Amount: 2
            writer.newLine();
            writer.write("2 1"); // ID: 2, Amount: 1
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Call retrieveTemp to load the items
        pos.retrieveTemp(TEST_DATABASE_FILE);

        // Assert: Verify the transaction items are loaded correctly
        assertEquals(2, pos.transactionItem.size());
        assertEquals(1, pos.transactionItem.get(0).getItemID());
        assertEquals(2, pos.transactionItem.get(0).getAmount());
        assertEquals(2, pos.transactionItem.get(1).getItemID());
        assertEquals(1, pos.transactionItem.get(1).getAmount());
    }

    @Test
    public void testRetrieveTemp_emptyFile() {
        // Arrange: Create an empty temp file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            // Intentionally left empty
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act: Call retrieveTemp to load the items
        pos.retrieveTemp(TEST_DATABASE_FILE);

        // Assert: Verify no items are loaded
        assertTrue(pos.transactionItem.isEmpty());
    }

    @Test
    public void testRetrieveTemp_fileNotFound() {
        // Act: Call retrieveTemp when the file does not exist
        // Ensure no exception is thrown and transaction items remain empty
        pos.retrieveTemp("NonExistentFile.txt");

        // Assert: Verify no items are loaded
        assertTrue(pos.transactionItem.isEmpty());
    }
    
    
    

	@Before
	public void setup() {
		pos = new POS(); // Instantiate POS object
		// Add sample items to transactionItem list for testing
		pos.transactionItem.add(new Item(1, "Item1", 10.0f, 2));
		pos.transactionItem.add(new Item(2, "Item2", 20.0f, 1));
	}

	@Test
	public void testDeleteTempItem() throws IOException {
		// Setup temp file with sample data
		FileWriter writer = new FileWriter(PointOfSale.tempFile);
		writer.write("SALE\n1 2\n2 1\n");
		writer.close();

		// Delete item with ID 1
		pos.deleteTempItem(1);

		// Verify the temp file only contains the remaining item
		BufferedReader reader = new BufferedReader(new FileReader(PointOfSale.tempFile));
		assertEquals("SALE", reader.readLine());
		assertEquals("2 1", reader.readLine());
		assertNull(reader.readLine());
		reader.close();
	}

	@Test
	public void testEndPOS() throws IOException {
		// Set total price
		pos.totalPrice = 30.0;
		// Assuming tax = 1.06 (6%)
		pos.tax = 1.06;
		double total = pos.endPOS("Database/itemDatabase.txt");

		// Verify the total price with tax applied
		assertEquals(31.8, total, 0.001);

		// Verify the invoice log was created and contains expected data
		BufferedReader reader = new BufferedReader(new FileReader("Database/saleInvoiceRecord.txt"));
		String line;
		boolean foundTotal = false;
		while ((line = reader.readLine()) != null) {
			if (line.contains("Total with tax: 31.8")) {
				foundTotal = true;
				break;
			}
		}
		reader.close();
		assertTrue(foundTotal);
		assertTrue(pos.transactionItem.isEmpty());
		assertTrue(pos.databaseItem.isEmpty());
	}

	@Test
	public void testRetrieveTemp() throws IOException {
		// Setup temp file with sample data
		FileWriter writer = new FileWriter(PointOfSale.tempFile);
		writer.write("SALE\n1 2\n2 1\n");
		writer.close();

		// Retrieve items from the temp file
		pos.retrieveTemp("Database/itemDatabase.txt");

		// Verify items were added to the transaction list
		List<Item> transactionItems = pos.transactionItem;
		assertEquals(2, transactionItems.size());
		assertEquals(1, transactionItems.get(0).getItemID());
		assertEquals(2, transactionItems.get(0).getAmount());
		assertEquals(2, transactionItems.get(1).getItemID());
		assertEquals(1, transactionItems.get(1).getAmount());
	}
}
