package buffalo.suny.software.atmedicine.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/28/15.
 */
public class MedicalHistoryTest {
    MedicalHistory md = new MedicalHistory("flu", "October 27", "10-01-2015");

    @Test
    public void testGetSymptom() throws Exception {
        assertEquals(md.getSymptom(),"flu");

    }

    @Test
    public void testGetLastHappen() throws Exception {
        assertEquals(md.getLastHappen(),"October 27");

    }

    @Test
    public void testGetDate() throws Exception {
        assertEquals(md.getDate(), "10-01-2015");
    }
}