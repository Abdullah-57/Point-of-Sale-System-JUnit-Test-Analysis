import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ReturnItemTest {
    
    private ReturnItem returnItem;

    @Before
    public void setUp() {
        // Set up a ReturnItem instance before each test
        returnItem = new ReturnItem(101, 5); // Example: Item ID 101, Days since return 5
    }

    @Test
    public void testReturnItemConstructor() {
        // Test the constructor to ensure it sets values correctly
        assertEquals(101, returnItem.getItemID());
        assertEquals(5, returnItem.getDays());
    }

    @Test
    public void testGetItemID() {
        // Test the getItemID method
        assertEquals(101, returnItem.getItemID());
    }

    @Test
    public void testGetDays() {
        // Test the getDays method
        assertEquals(5, returnItem.getDays());
    }

    @Test
    public void testDifferentValues() {
        // Test with different values
        ReturnItem anotherReturnItem = new ReturnItem(202, 10);
        assertEquals(202, anotherReturnItem.getItemID());
        assertEquals(10, anotherReturnItem.getDays());
    }
}
