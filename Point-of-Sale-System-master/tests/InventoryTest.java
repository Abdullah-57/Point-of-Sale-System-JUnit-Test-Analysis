import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class InventoryTest {
    Inventory inventory;
    List<Item> databaseItem;
    List<Item> transactionItem;

    @Before
    public void setUp() {
        // Initialize inventory singleton and lists for items
        inventory = Inventory.getInstance();
        databaseItem = new ArrayList<>();
        transactionItem = new ArrayList<>();
        
        // Adding mock items to the database
        databaseItem.add(new Item(1000, "Potato", 1.0f, 249));
        databaseItem.add(new Item(1001, "PlasticCup", 0.5f, 376));
        
        // Adding a transaction (purchase or return)
        transactionItem.add(new Item(1000, "Potato", 1.0f, 2)); // Transacting 2 units of Potato
    }

    @Test
    public void testSingletonInstance() {
        // Test if the singleton pattern is implemented correctly
        Inventory anotherInventory = Inventory.getInstance();
        assertSame(inventory, anotherInventory); // Both should be the same instance
    }

    @Test
    public void testAccessInventorySuccess() throws IOException {
        // Prepare a mock file to simulate the database
        String mockDatabaseFile = "mockDatabase.txt";
        createMockDatabaseFile(mockDatabaseFile);
        
        // Test successful access to inventory
        boolean result = inventory.accessInventory(mockDatabaseFile, databaseItem);
        assertTrue(result);
        assertEquals(2, databaseItem.size()); // Should read two items from the mock database

        // Clean up
        deleteMockFile(mockDatabaseFile);
    }

    @Test
    public void testAccessInventoryFileNotFound() {
        // Test accessing inventory when the file does not exist
        boolean result = inventory.accessInventory("nonexistent.txt", databaseItem);
        assertFalse(result); // File not found
    }

    @Test
    public void testAccessInventoryEmptyFile() throws IOException {
        // Test accessing inventory from an empty file
        String emptyDatabaseFile = "emptyDatabase.txt";
        createEmptyFile(emptyDatabaseFile);
        
        boolean result = inventory.accessInventory(emptyDatabaseFile, databaseItem);
        assertTrue(result); // File exists but is empty
        assertEquals(0, databaseItem.size()); // No items should be added

        // Clean up
        deleteMockFile(emptyDatabaseFile);
    }

    @Test
    public void testAccessInventoryIOException() {
        // Simulate an IO exception by using a directory as a file
        String directoryAsFile = ".";
        boolean result = inventory.accessInventory(directoryAsFile, databaseItem);
        assertFalse(result); // IO error should occur
    }

    @Test
    public void testUpdateInventoryTakeFromInventory() {
        // Test updating inventory by taking items (simulating a sale or rental)
        inventory.updateInventory("mockDatabase.txt", transactionItem, databaseItem, true);
        
        // Verify that the amount of the first item in the database has decreased
        assertEquals(247, databaseItem.get(0).getAmount()); // Original amount was 249, now it's 247
    }

    @Test
    public void testUpdateInventoryReturnToInventory() {
        // Test updating inventory by adding items back (simulating a return)
        inventory.updateInventory("mockDatabase.txt", transactionItem, databaseItem, false);
        
        // Verify that the amount of the first item in the database has increased
        assertEquals(251, databaseItem.get(0).getAmount()); // Original amount was 249, now it's 251
    }

    @Test
    public void testUpdateInventoryItemNotFound() {
        // Test trying to update inventory with an item not in the database
        transactionItem.clear(); // Clear previous transaction items
        transactionItem.add(new Item(1010, "Applesauce", 2.5f, 2)); // This item is not in the database
        
        inventory.updateInventory("mockDatabase.txt", transactionItem, databaseItem, true);
        
        // Verify that the database remains unchanged since Applesauce does not exist
        assertEquals(249, databaseItem.get(0).getAmount());
        assertEquals(376, databaseItem.get(1).getAmount());
    }

    @Test
    public void testUpdateInventoryNoChangeNeeded() {
        // Test updating inventory where transaction item quantity is 0 (no change)
        transactionItem.clear();
        transactionItem.add(new Item(1000, "Potato", 1.0f, 0)); // Zero quantity
        
        inventory.updateInventory("mockDatabase.txt", transactionItem, databaseItem, true);
        
        // Verify that the amount remains the same
        assertEquals(249, databaseItem.get(0).getAmount()); // No change should occur
    }

    @Test
    public void testUpdateInventoryInvalidTransaction() {
        // Test updating inventory with invalid transaction (negative amount)
        transactionItem.clear();
        transactionItem.add(new Item(1000, "Potato", 1.0f, -5)); // Negative quantity
        
        inventory.updateInventory("mockDatabase.txt", transactionItem, databaseItem, true);
        
        // Verify that the amount remains the same, as negative transactions should be ignored
        assertEquals(249, databaseItem.get(0).getAmount());
    }

    // Helper method to create a mock database file
    private void createMockDatabaseFile(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("1000 Potato 1.0 249\n");
        bufferedWriter.write("1001 PlasticCup 0.5 376\n");
        bufferedWriter.close();
    }

    // Helper method to create an empty file
    private void createEmptyFile(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.close();
    }

    // Helper method to delete the mock file after tests
    private void deleteMockFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
}
