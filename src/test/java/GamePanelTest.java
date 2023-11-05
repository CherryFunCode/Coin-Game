import static org.junit.Assert.*;
import javax.swing.*;
import org.junit.Test;
import org.junit.Before;
import java.awt.Component;
import java.awt.Container;



public class GamePanelTest {

    private GamePanel gamePanel;

    @Before
    public void setUp() {
        // Initialize the GamePanel before each test
        gamePanel = new GamePanel("DemoUser");
    }

    @Test
    public void testBetListener() {
        JButton btnHead = (JButton) TestUtils.getChildNamed(gamePanel, "btnHead");
        assertNotNull("Head button not initialized", btnHead);

        btnHead.doClick();
        JLabel lblResult = (JLabel) TestUtils.getChildNamed(gamePanel, "lblResult");

        // Add a print statement for debugging
        System.out.println("Retrieved lblResult: " + lblResult);

        assertNotNull("lblResult should not be null", lblResult);
        assertEquals("BetListener not functioning correctly", "Bet set to: Head", lblResult.getText());
    }

    public static class TestUtils {
        public static Component getChildNamed(Component parent, String name) {
            if (name.equals(parent.getName())) {
                return parent;
            }

            if (parent instanceof Container) {
                for (Component child : ((Container) parent).getComponents()) {
                    Component foundChild = getChildNamed(child, name);
                    if (foundChild != null) {
                        return foundChild;
                    }
                }
            }
            return null;
        }
    }
}

