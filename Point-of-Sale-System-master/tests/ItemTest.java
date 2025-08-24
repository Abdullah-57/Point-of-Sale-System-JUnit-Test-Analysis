import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class ItemTest {
    private Item item;

    @Before
    public void setUp() {
        // Initialize an Item instance before each test
        item = new Item(1, "Sample Item", 10.0f, 5);
    }

    @Test
    public void testConstructor() {
        // Test the constructor and ensure the object is not null
        assertNotNull("Item object should be created", item);
        assertEquals(1, item.getItemID());
        assertEquals("Sample Item", item.getItemName());
        assertEquals(10.0f, item.getPrice(), 0.01);
        assertEquals(5, item.getAmount());
    }

    @Test
    public void testGetItemName() {
        // Test the getItemName method
        assertEquals("Sample Item", item.getItemName());
    }

    @Test
    public void testGetItemID() {
        // Test the getItemID method
        assertEquals(1, item.getItemID());
    }

    @Test
    public void testGetPrice() {
        // Test the getPrice method
        assertEquals(10.0f, item.getPrice(), 0.01);
    }

    @Test
    public void testGetAmount() {
        // Test the getAmount method
        assertEquals(5, item.getAmount());
    }

    @Test
    public void testUpdateAmount() {
        // Test the updateAmount method
        item.updateAmount(10);
        assertEquals(10, item.getAmount());
        
        // Test updating to zero
        item.updateAmount(0);
        assertEquals(0, item.getAmount());
        
        // Test updating to a negative value (if applicable, 
        // but this depends on your business logic if negatives are allowed)
        item.updateAmount(-5);
        assertEquals(-5, item.getAmount());
    }
}
