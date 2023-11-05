import org.junit.Test;
import javax.swing.*;
import static org.junit.Assert.*;

public class MainFrameTest {

    @Test
    public void testMainFrameVisibility() {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            assertTrue(mainFrame.isVisible());
        });
    }

    @Test
    public void testMainFrameTitle() {
        MainFrame mainFrame = new MainFrame();
        assertEquals("Two-up Game", mainFrame.getTitle());
    }

}
