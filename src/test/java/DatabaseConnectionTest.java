import java.sql.Connection;

import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseConnectionTest {

    @Test
    public void testGetConnection() {
        Connection connection = DatabaseConnection.getConnection();
        assertNotNull(connection);

        // Ensure it's not closed
        try {
            assertFalse(connection.isClosed());
        } catch (Exception e) {
            fail("Failed due to an exception: " + e.getMessage());
        }
    }
}
