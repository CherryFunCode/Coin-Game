import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class LoginPanelTest {

    @Test
    public void testLoginComponentsExistence() {
        LoginPanel loginPanel = new LoginPanel();

        assertNotNull(loginPanel.getComponentByName("loginUsernameField"));
        assertNotNull(loginPanel.getComponentByName("loginPasswordField"));
    }

    @Test
    public void testRegisterComponentsExistence() {
        LoginPanel loginPanel = new LoginPanel();

        assertNotNull(loginPanel.getComponentByName("registerUsernameField"));
        assertNotNull(loginPanel.getComponentByName("registerPasswordField"));
    }

}

