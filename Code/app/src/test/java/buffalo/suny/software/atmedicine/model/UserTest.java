package buffalo.suny.software.atmedicine.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/28/15.
 */
public class UserTest {
    private User user = new User(true, "Smith", "Will","7164231555", "Amex", "01/01/1983", "154", 60);

    @Test
    public void testIsMale() throws Exception {
        assertEquals(user.isMale(),true);

    }

    @Test
    public void testGetLastName() throws Exception {
        assertEquals(user.getLastName(),"Smith");
    }

    @Test
    public void testGetFirstName() throws Exception {
        assertEquals(user.getFirstName(),"Will");

    }

    @Test
    public void testGetPhoneNumber() throws Exception {
        assertEquals(user.getPhoneNumber(),"7164231555");
    }

    @Test
    public void testGetInsuranceProvider() throws Exception {
        assertEquals(user.getInsuranceProvider(),"Amex");

    }

    @Test
    public void testGetDateOfBirth() throws Exception {
        assertEquals(user.getDateOfBirth(), "01/01/1983");

    }

    @Test
    public void testGetHeight() throws Exception {
        assertEquals(user.getHeight(),"154");

    }

    @Test
    public void testGetWeightLbs() throws Exception {
        assertEquals(user.getWeightLbs(),60);

    }

    /*@Test
    public void testGetInterpretedBMI() throws Exception {
        assertEquals(user.getInterpretedBMI(),"123");

    }*/
}