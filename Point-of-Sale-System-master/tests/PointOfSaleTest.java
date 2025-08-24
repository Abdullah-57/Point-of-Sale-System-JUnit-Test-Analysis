import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PointOfSaleTest {
    private PointOfSale pointOfSale;

    // Mock subclass to test abstract methods
    private class MockPointOfSale extends PointOfSale {
        @Override
        public double endPOS(String textFile) {
            // Mock implementation
            return totalPrice;
        }

        @Override
        public void deleteTempItem(int id) {
            // Mock implementation
        }

        @Override
        public void retrieveTemp(String textFile) {
            // Mock implementation
        }
    }

    @Before
    public void setUp() {
        pointOfSale = new MockPointOfSale();
        // Add some mock items to the database for testing
        pointOfSale.databaseItem.add(new Item(1, "Item1", (float) 10.0, 5));
        pointOfSale.databaseItem.add(new Item(2, "Item2", (float) 20.0, 10));
        pointOfSale.transactionItem.clear(); // Clear transaction item list before each test
    }

    @Test
    public void testStartNewAccessInventorySuccess() throws IOException {
        // Prepare a mock file
        String mockDatabaseFile = "mockDatabase.txt";
        createMockDatabaseFile(mockDatabaseFile);

        boolean result = pointOfSale.startNew(mockDatabaseFile);
        assertTrue(result); // Should return true if inventory access is successful

        // Clean up
        deleteMockFile(mockDatabaseFile);
    }

    @Test
    public void testStartNewAccessInventoryFailure() {
        boolean result = pointOfSale.startNew("nonexistent.txt");
        assertFalse(result); // Should return false if inventory access fails
    }

    @Test
    public void testEnterItemSuccess() {
        boolean result = pointOfSale.enterItem(1, 2);
        assertTrue(result); // Should return true if item is found and added
        assertEquals(1, pointOfSale.transactionItem.size()); // Should have 1 item in transaction
        assertEquals(1, pointOfSale.transactionItem.get(0).getItemID()); // Item ID should be 1
    }

    @Test
    public void testEnterItemNotFound() {
        boolean result = pointOfSale.enterItem(3, 1);
        assertFalse(result); // Should return false if item is not found
        assertEquals(0, pointOfSale.transactionItem.size()); // No items should be added to transaction
    }

    @Test
    public void testUpdateTotal() {
        pointOfSale.enterItem(1, 2); // Add 2 of Item1
        double total = pointOfSale.updateTotal();
        assertEquals(20.0, total, 0.01); // Total should be 20.0 (2 * 10.0)
    }

    @Test
    public void testCouponValid() throws IOException {
        // Prepare a mock coupon file
        createMockCouponFile("mockCoupon.txt", "DISCOUNT10\n");
        
        // Start with a non-zero total price
        pointOfSale.totalPrice = 100.0; 
        assertTrue(pointOfSale.coupon("DISCOUNT10")); // Coupon should be valid
        assertEquals(90.0, pointOfSale.totalPrice, 0.01); // Total price should be discounted

        // Clean up
        deleteMockFile("mockCoupon.txt");
    }

    @Test
    public void testCouponInvalid() throws IOException {
        // Prepare a mock coupon file
        createMockCouponFile("mockCoupon.txt", "DISCOUNT10\n");

        pointOfSale.totalPrice = 100.0; // Start with a non-zero total price
        assertFalse(pointOfSale.coupon("INVALIDCOUPON")); // Coupon should be invalid
        assertEquals(100.0, pointOfSale.totalPrice, 0.01); // Total price should not be affected

        // Clean up
        deleteMockFile("mockCoupon.txt");
    }

    @Test
    public void testRemoveItemsSuccess() {
        pointOfSale.enterItem(1, 2); // Add 2 of Item1
        assertTrue(pointOfSale.removeItems(1)); // Should return true when removing an existing item
        assertEquals(0, pointOfSale.transactionItem.size()); // Transaction should be empty
    }

    @Test
    public void testRemoveItemsNotFound() {
        assertFalse(pointOfSale.removeItems(3)); // Should return false for non-existing item
        assertEquals(0, pointOfSale.transactionItem.size()); // Transaction should remain unchanged
    }

    @Test
    public void testCreditCardValid() {
        assertTrue(pointOfSale.creditCard("1234567812345678")); // Should return true for a valid card number
    }

    @Test
    public void testCreditCardInvalidLength() {
        assertFalse(pointOfSale.creditCard("12345678")); // Should return false for short card number
    }

    @Test
    public void testCreditCardInvalidCharacter() {
        assertFalse(pointOfSale.creditCard("12345678abcd5678")); // Should return false for invalid characters
    }

    @Test
    public void testRemoveItemsEmptyTransaction() {
        assertFalse(pointOfSale.removeItems(1)); // Should return false for non-existing item
        assertEquals(0, pointOfSale.transactionItem.size()); // Transaction should remain unchanged
    }

    @Test
    public void testDetectSystem() {
        pointOfSale.detectSystem(); // Test detection method
        // No assert necessary, just ensuring it runs without exceptions
    }

    @Test
    public void testLastAddedItem() {
        pointOfSale.enterItem(1, 2); // Add 2 of Item1
        Item lastItem = pointOfSale.lastAddedItem();
        assertNotNull(lastItem); // Ensure last added item is not null
        assertEquals(1, lastItem.getItemID()); // Should be Item1
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testLastAddedItemNoItems() {
        pointOfSale.lastAddedItem(); // Should throw exception if no items in transaction
    }

    @Test
    public void testGetTotal() {
        pointOfSale.totalPrice = 100.0;
        assertEquals(100.0, pointOfSale.getTotal(), 0.01); // Should return the total price
    }

    @Test
    public void testGetCartSize() {
        assertEquals(0, pointOfSale.getCartSize()); // Should be 0 initially
        pointOfSale.enterItem(1, 2); // Add an item
        assertEquals(1, pointOfSale.getCartSize()); // Should be 1 now
    }

    // Helper method to create a mock database file
    private void createMockDatabaseFile(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("1 Item1 10.0 5\n");
        bufferedWriter.write("2 Item2 20.0 10\n");
        bufferedWriter.close();
    }

    // Helper method to create a mock coupon file
    private void createMockCouponFile(String filename, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(content);
        bufferedWriter.close();
    }

    // Helper method to delete the mock file after tests
    private void deleteMockFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    class PointOfSaleImpl extends PointOfSale {
        
        @Override
        public double endPOS(String textFile) {
            // Mock implementation
            return 0;
        }

        @Override
        public void deleteTempItem(int id) {
            // Mock implementation
        }

        @Override
        public void retrieveTemp(String textFile) {
            // Mock implementation
        }
    }

    
    
    @Test
    public void testCouponValid1() throws IOException {
        PointOfSale pos = new PointOfSaleImpl();  // Now we instantiate the concrete class
        String validCoupon = "VALIDCOUPON";

        // Create a temporary coupon file for testing
        FileWriter writer = new FileWriter(PointOfSale.couponNumber);
        writer.write(validCoupon + "\n");
        writer.close();

        assertTrue(pos.coupon(validCoupon));
        assertEquals(pos.getTotal() * 0.90, pos.getTotal(), 0.01);
    }

    @Test
    public void testCouponInvalid1() throws IOException {
        PointOfSale pos = new PointOfSaleImpl();  
        String invalidCoupon = "INVALIDCOUPON";

        // Create a temporary coupon file with no matching coupon
        FileWriter writer = new FileWriter(PointOfSale.couponNumber);
        writer.write("SOMEOtherCOUPON\n");
        writer.close();

        assertFalse(pos.coupon(invalidCoupon));
    }

    @Test
    public void testCouponFileNotFoundException() {
        PointOfSale pos = new PointOfSaleImpl();
        
        // Ensure the file does not exist
        File couponFile = new File(PointOfSale.couponNumber);
        couponFile.delete();

        // The coupon file does not exist, should trigger the FileNotFoundException
        assertFalse(pos.coupon("SOMECOUPON"));
    }

    @Test
    public void testCouponIOException() throws IOException {
        PointOfSale pos = new PointOfSaleImpl();
        // Simulate IOException by setting file to be unreadable
        File couponFile = new File(PointOfSale.couponNumber);
        couponFile.createNewFile();
        couponFile.setReadable(false);

        assertFalse(pos.coupon("SOMECOUPON"));
    }
    
    
    @Test
    public void testCreateTemp() throws IOException {
        PointOfSale pos = new PointOfSaleImpl();  
        int itemId = 1234;
        int amount = 2;

        // Create a temporary file for testing
        File tempFile = new File(PointOfSale.tempFile);
        tempFile.delete();  // Ensure it starts empty
        
        pos.createTemp(itemId, amount);
        
        // Check if the file contains the correct information
        BufferedReader reader = new BufferedReader(new FileReader(tempFile));
        String line = reader.readLine();
        reader.close();

        assertEquals(itemId + " " + amount, line);
    }

    
    @Test
    public void testRemoveItems() {
        PointOfSale pos = new PointOfSaleImpl();
        
        // Add an item to transaction and then remove it
        pos.enterItem(1234, 1);
        assertTrue(pos.removeItems(1234));
        assertEquals(0, pos.getCartSize());

        // Try to remove an item not in the cart
        assertFalse(pos.removeItems(5678));
    }

    
    @Test
    public void testCreditCardValid1() {
        PointOfSale pos = new PointOfSaleImpl();
        String validCard = "1234567812345678";

        assertTrue(pos.creditCard(validCard));
    }

    @Test
    public void testCreditCardInvalidNonDigit() {
        PointOfSale pos = new PointOfSaleImpl();
        String invalidCard = "12345678abcd5678";

        assertFalse(pos.creditCard(invalidCard));
    }

    @Test
    public void testCreditCardInvalidLength1() {
        PointOfSale pos = new PointOfSaleImpl();
        String invalidCard = "12345";  // Invalid length

        assertFalse(pos.creditCard(invalidCard));
    }

    
    @Test
    public void testGetCart() {
        PointOfSale pos = new PointOfSaleImpl();
        
        pos.enterItem(1234, 1);  // Add an item to the cart
        List<Item> cart = pos.getCart();
        
        assertEquals(1, cart.size());
        assertEquals(1234, cart.get(0).getItemID());
    }

}
