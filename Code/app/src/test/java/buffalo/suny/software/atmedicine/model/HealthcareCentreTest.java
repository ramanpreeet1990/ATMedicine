package buffalo.suny.software.atmedicine.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/28/15.
 */
public class HealthcareCentreTest {
    private HealthcareCentre hcc;

    public HealthcareCentreTest(){
        hcc = new HealthcareCentre("abcd", "abcd", "71622222", 12.111, 13.11);
    }

    @Test
    public void testGetName() throws Exception {
        assertNotNull(hcc.getName());
    }

    @Test
    public void testGetAddress() throws Exception {
        assertEquals(hcc.getAddress(),"abcd");
    }

    @Test
    public void testGetPhoneNumber() throws Exception {
        assertEquals(hcc.getPhoneNumber(),"71622222");

    }

    @Test
    public void testGetLatitude() throws Exception {
        assertEquals(hcc.getLatitude(),12.111,0);

    }

    @Test
    public void testGetLongitude() throws Exception {
        assertEquals(hcc.getLongitude(),13.11,0);

    }


}