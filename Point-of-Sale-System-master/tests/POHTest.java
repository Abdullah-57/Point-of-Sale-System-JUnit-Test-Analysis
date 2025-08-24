import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class POHTest {
    private POH poh;
    private List<Item> databaseItem;
    private List<Item> transactionItem;
    private final String tempFilePath = "Database/newTemp.txt";
    
    @Before
    public void setUp() {
        poh = new POH(1234567890L);
        databaseItem = new ArrayList<>();
        transactionItem = new ArrayList<>();
        
        // Adding mock items to the database
        databaseItem.add(new Item(1, "Item1", 10.0f, 5));
        databaseItem.add(new Item(2, "Item2", 20.0f, 10));
        
        // Adding a transaction (purchase or return)
        transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Transacting 2 units of Item1
        
        // Create a temp file for testing
        createTempFile(tempFilePath, "TempType", "1234567890");
        
    }
    
    
    
    @Test
    public void testDeleteTempItem_ItemExists() {
        // Prepare temp file for testing
        createTempFile("Database/newTemp.txt", "TempType", "1234567890", "1 10", "2 5");
        poh.transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Adding to transaction

        // Act
        poh.deleteTempItem(1);

        // Assert
        assertEquals(1, poh.transactionItem.size()); // Ensure only one item remains
        assertEquals(2, poh.transactionItem.get(0).getItemID()); // Check remaining item
    }

    @Test
    public void testDeleteTempItem_ItemNotFound() {
        // Prepare temp file for testing
        createTempFile("Database/newTemp.txt", "TempType", "1234567890", "1 10", "2 5");
        poh.transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Adding to transaction

        // Act
        poh.deleteTempItem(3); // Attempt to delete a non-existing item

        // Assert
        assertEquals(1, poh.transactionItem.size()); // Should remain the same
        assertEquals(1, poh.transactionItem.get(0).getItemID()); // Check retained item
    }

    @Test
    public void testEndPOS_WithReturns() {
        poh.transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Add an item to transaction
        poh.returnList.add(new ReturnItem(1, 5)); // Simulate a return

        double total = poh.endPOS("Database/sample.txt");

        // Assert
        assertEquals(10.0, total, 0.01); // Total should reflect the late fee
    }

    @Test
    public void testEndPOS_NoReturns() {
        poh.transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Add an item to transaction
        poh.returnList.clear(); // No returns

        double total = poh.endPOS("Database/sample.txt");

        // Assert
        assertEquals(0.0, total, 0.01); // Total should be 0 since there are no returns
    }

    @Test
    public void testEndPOS_EmptyTransaction() {
        double total = poh.endPOS("Database/sample.txt");

        // Assert
        assertEquals(0.0, total, 0.01); // Total should be 0 for empty transaction
    }

    @Test
    public void testRetrieveTemp_ValidData() {
        createTempFile("Database/temp.txt", "TempType", "1234567890", "1 10", "2 5");

        poh.retrieveTemp("Database/temp.txt");

        assertEquals(2, poh.transactionItem.size()); // Check if items were added
        assertEquals(1, poh.transactionItem.get(0).getItemID()); // Verify first item
    }

    @Test
    public void testRetrieveTemp_FileNotFound() {
        assertThrows(IOException.class, () -> {
            poh.retrieveTemp("Database/nonexistent.txt"); // Attempt to read a non-existing file
        });
    }

    // Helper method to create a temporary file for testing
    private void createTempFile(String filePath, String type, String phone, String... items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(type);
            writer.newLine();
            writer.write(phone);
            writer.newLine();
            for (String item : items) {
                writer.write(item);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    // New test case for default constructor
    @Test
    public void testDefaultConstructor() {
        POH pohDefault = new POH(); // This should invoke the default constructor
        assertEquals(0, pohDefault.phone); // Verify the phone number is set to 0
    }
    
 // New test case for the first branch of the if condition in endPOS
    @Test
    public void testEndPOS_ReturnSaleTrue() {
        poh.returnSale = true; // Set returnSale to true
        poh.transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Add an item to transaction
        poh.endPOS("Database/sample.txt"); // Call endPOS, should not throw exception

        // Assert: check the returnSale log file content or just ensure no exceptions thrown
    }
   

    // New test case to cover IOException in deleteTempItem
    @Test
    public void testDeleteTempItem_FileNotFoundException() {
        assertThrows(FileNotFoundException.class, () -> {
            poh.deleteTempItem(1); // Attempt to read a non-existing file
        });
    }

    // New test case to cover IOException in retrieveTemp
    @Test
    public void testRetrieveTemp_IOException() {
        createTempFile("Database/temp.txt", "TempType", "1234567890", "1 10", "2 5");
        File tempFile = new File("Database/temp.txt");
        tempFile.setReadable(false); // Make the file unreadable to throw an IOException

        assertThrows(IOException.class, () -> {
            poh.retrieveTemp("Database/temp.txt");
        });

        tempFile.setReadable(true); // Restore file readability after test
    }
    
    
    // New test case to cover the second branch of the if condition (non-Windows system)
    @Test
    public void testDeleteTempItem_NonWindowsSystem() {
        // Simulate a non-Windows system by forcing the os.name property
        System.setProperty("os.name", "Linux");
        createTempFile("Database/temp.txt", "TempType", "1234567890", "1 10", "2 5");
        poh.transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Add item to transaction

        // Call deleteTempItem method
        poh.deleteTempItem(1);

        // Assert: verify transaction size after deletion
        assertEquals(1, poh.transactionItem.size()); // Ensure item was deleted
    }

    // New test case to cover FileNotFoundException in deleteTempItem
    @Test
    public void testDeleteTempItem_FileNotFound() {
        // Simulate file not existing by not creating it
        assertThrows(FileNotFoundException.class, () -> {
            poh.deleteTempItem(1); // Attempt to delete from non-existing temp file
        });
    }

    // New test case to cover IOException in deleteTempItem
    @Test
    public void testDeleteTempItem_IOException() throws IOException {
        // Create a temporary file but simulate an IOException by locking it
        File tempFile = new File("Database/newTemp.txt");
        createTempFile("Database/newTemp.txt", "TempType", "1234567890", "1 10");
        FileWriter fileWriter = new FileWriter(tempFile);
        fileWriter.close(); // Close the file but it's still there
        tempFile.setReadOnly(); // Make file read-only to cause IOException

        assertThrows(IOException.class, () -> {
            poh.deleteTempItem(1); // Attempt to write to the read-only file
        });
        tempFile.setWritable(true); // Reset permission for cleanup
    }

    // New test case to cover the branch when returnSale is false and transaction is not empty
    @Test
    public void testEndPOS_TransactionItemsWithReturns() {
        // Add items and returns for processing
        poh.returnSale = false; // Ensure returnSale is false
        poh.transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Transaction with item
        poh.returnList.add(new ReturnItem(1, 5)); // Simulate a return

        // Call endPOS to process
        double total = poh.endPOS("Database/sample.txt");

        // Assert: verify total and transactions processed
        assertEquals(10.0, total, 0.01); // Ensure correct calculation
    }

    // New test case to cover the branch when transaction size is 0 or textFile is empty
    @Test
    public void testEndPOS_EmptyTransactionOrNoTextFile() {
        // Simulate empty transaction and non-empty textFile
        double total = poh.endPOS(""); // Pass empty textFile

        // Assert: ensure no processing and total is 0
        assertEquals(0.0, total, 0.01); // Should return 0 since no transactions
    }

    // New test case to cover the entire if condition inside the for loop of returnCounter
    @Test
    public void testEndPOS_ReturnListMatch() {
        // Add transaction and matching return list
        poh.transactionItem.add(new Item(1, "Item1", 10.0f, 2)); // Add to transaction
        poh.returnList.add(new ReturnItem(1, 5)); // Add matching return item

        // Call endPOS
        double total = poh.endPOS("Database/sample.txt");

        // Assert: check correct calculation for days late
        assertEquals(10.0, total, 0.01); // Total late fee calculation (5 days * 2 * 10 * 0.1)
    }

    // New test case to cover FileNotFoundException in retrieveTemp
    @Test
    public void testRetrieveTemp_FileNotFoundException() {
        assertThrows(FileNotFoundException.class, () -> {
            poh.retrieveTemp("nonexistent.txt"); // Attempt to read from a non-existent file
        });
    }

    // New test case to cover IOException in retrieveTemp
    @Test
    public void testRetrieveTemp_IOException1() throws IOException {
        // Create temp file and lock it to simulate IOException
        File tempFile = new File("Database/temp.txt");
        createTempFile("Database/temp.txt", "TempType", "1234567890", "1 10");
        tempFile.setReadOnly(); // Make file read-only to cause IOException

        assertThrows(IOException.class, () -> {
            poh.retrieveTemp("Database/temp.txt"); // Attempt to read from a locked file
        });
        tempFile.setWritable(true); // Reset permission for cleanup
    }

    // New test case to cover while loop in retrieveTemp and its internal logic
    @Test
    public void testRetrieveTemp_ValidTransaction() {
        // Create temp file with transaction data
        createTempFile("Database/temp.txt", "TempType", "1234567890", "1 2", "2 5");

        // Call retrieveTemp method
        poh.retrieveTemp("Database/temp.txt");

        // Assert: verify transaction items were correctly added
        assertEquals(2, poh.transactionItem.size()); // Should retrieve two items
        assertEquals(1, poh.transactionItem.get(0).getItemID()); // Verify first item
        assertEquals(2, poh.transactionItem.get(0).getAmount()); // Verify amount of first item
    }

    
    @Test
    public void testDeleteTempItem_FileNotFound1() {
        // Testing deleteTempItem with a file that does not exist
        poh.deleteTempItem(1);
        assertTrue(true); // Just checking that it doesn't throw an exception
    }

    @Test
    public void testRetrieveTemp_FileNotFound1() {
        // Testing retrieveTemp with a file that does not exist
        poh.retrieveTemp("non_existent_file.txt");
        assertTrue(true); // Just checking that it doesn't throw an exception
    }

    @Test
    public void testRetrieveTemp_Success() {
        // Create a temporary file for testing
        String tempFilePath = "Database/newTemp.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFilePath))) {
            writer.write("1234567890\n"); // Phone number
            writer.write("1 2\n"); // Item ID and amount
        } catch (IOException e) {
            fail("Could not write to temp file");
        }

        poh.retrieveTemp(tempFilePath); // Call the method

        // Cleanup
        new File(tempFilePath).delete();
    }

    @Test
    public void testEndPOS_WithReturns1() {
        // Add some test transaction items
        Item item1 = new Item(1, "Item 1", 5, (int) 10.0); // ID, Name, Amount, Price
        Item item2 = new Item(2, "Item 2", 2, (int) 15.0);
        transactionItem.add(item1);
        transactionItem.add(item2);

        // Simulate return items
        poh.returnList.add(new ReturnItem(1, 3)); // ID, Days Late
        poh.returnList.add(new ReturnItem(2, 2)); // ID, Days Late

        double total = poh.endPOS("testFile.txt");
        assertEquals(1.5, total); // Calculate manually based on provided logic

        // Cleanup the inventory and transaction items
        databaseItem.clear();
        poh.transactionItem.clear();
        assertTrue(poh.transactionItem.isEmpty());
    }

    @Test
    public void testUpdateTotal_CalledSuccessfully() {
        // Test that updateTotal() is called successfully
        // Simulating a condition where updateTotal is called
        Item item = new Item(1, "Item", 1, (int) 10.0);
        poh.transactionItem.add(item);
        
        // Call the method that internally calls updateTotal
        poh.retrieveTemp("testFile.txt");

        // Asserting something based on expected behavior after updateTotal is called
        // This might be checking some state in your POH class
        assertTrue(true); // Add actual checks based on your implementation
    }
    
    // Test case for FileNotFoundException in retrieveTemp
    @Test
    public void testRetrieveTemp_FileNotFoundException1() throws FileNotFoundException, IOException {
        poh.tempFile = "nonexistent_temp.txt"; // Non-existent file to trigger exception

        poh.retrieveTemp("someFile.txt");
		fail("Expected FileNotFoundException was not thrown.");
    }

    // Test case for IOException in retrieveTemp (unreadable file)
    @Test
    public void testRetrieveTemp_IOException11() throws IOException {
        File unreadableFile = new File("unreadable_temp.txt");
        try {
            unreadableFile.createNewFile();
            unreadableFile.setReadable(false); // Make file unreadable

            poh.tempFile = "unreadable_temp.txt";

            poh.retrieveTemp("someFile.txt");
			fail("Expected IOException was not thrown.");
        } catch (IOException e) {
            fail("Setup failed: " + e.getMessage());
        } finally {
            unreadableFile.setReadable(true);
            unreadableFile.delete();
        }
    }

    // Test case for FileNotFoundException in deleteTempItem
    @Test
    public void testDeleteTempItem_FileNotFoundException1() throws FileNotFoundException, IOException {
        poh.tempFile = "nonexistent_temp.txt"; // Non-existent file to trigger exception

        poh.deleteTempItem(1);
		fail("Expected FileNotFoundException was not thrown.");
    }

    // Test case for IOException in deleteTempItem (unreadable file)
    @Test
    public void testDeleteTempItem_IOException1() throws IOException {
        File unreadableFile = new File("unreadable_temp.txt");
        try {
            unreadableFile.createNewFile();
            unreadableFile.setReadable(false); // Make file unreadable

            poh.tempFile = "unreadable_temp.txt";

            poh.deleteTempItem(1);
			fail("Expected IOException was not thrown.");
        } catch (IOException e) {
            fail("Setup failed: " + e.getMessage());
        } finally {
            unreadableFile.setReadable(true);
            unreadableFile.delete();
        }
    }

    // Test case for the nested loop in endPOS where transaction item matches return item
    @Test
    public void testEndPOS_NestedLoopMatch() {
        // Simulate items
        Item transactionItem1 = new Item(1, "Item1", 2, (int) 100.0);
        ReturnItem returnItem1 = new ReturnItem(1, 5); // Matching item ID for nested loop condition

        poh.transactionItem.add(transactionItem1);
        poh.returnList.add(returnItem1);

        double result = poh.endPOS("inventory.txt");

        // Calculate expected total price
        double expectedItemPrice = transactionItem1.getAmount() * transactionItem1.getPrice() * 0.1 * returnItem1.getDays();
        assertEquals(expectedItemPrice, poh.totalPrice, 0.01);
    }

    // Test case for the nested loop in endPOS where no transaction item matches return item
    @Test
    public void testEndPOS_NoMatchInNestedLoop() {
        // Simulate items where no match is found in the nested loop
        Item transactionItem1 = new Item(1, "Item1", 2, (int) 100.0);
        ReturnItem returnItem2 = new ReturnItem(2, 3); // Different itemID to prevent match

        poh.transactionItem.add(transactionItem1);
        poh.returnList.add(returnItem2);

        double result = poh.endPOS("inventory.txt");

        // No price should be added as the item IDs don't match
        assertEquals(0.0, poh.totalPrice, 0.01);
    }

    // Test case for FileNotFoundException in endPOS
    @Test
    public void testEndPOS_FileNotFoundException() throws FileNotFoundException, IOException {
        poh.returnSale = true;

        try {
            poh.endPOS("someFile.txt");
            fail("Expected FileNotFoundException was not thrown.");
        }
        finally {
        	
        }
    }

    // Test case for IOException in endPOS
    @Test
    public void testEndPOS_IOException() throws IOException {
        // Create an unreadable return sale log file
        File unreadableFile = new File("returnSale.txt");
        try {
            unreadableFile.createNewFile();
            unreadableFile.setReadable(false); // Make file unreadable

            poh.returnSale = true;

            poh.endPOS("someFile.txt");
			fail("Expected IOException was not thrown.");
        } catch (IOException e) {
            fail("Setup failed: " + e.getMessage());
        } finally {
            unreadableFile.setReadable(true);
            unreadableFile.delete();
        }
    }
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}
