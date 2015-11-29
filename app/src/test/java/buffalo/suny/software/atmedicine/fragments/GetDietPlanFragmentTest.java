package buffalo.suny.software.atmedicine.fragments;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/28/15.
 */
public class GetDietPlanFragmentTest {

    private GetDietPlanFragment test = new GetDietPlanFragment();
    @Test
    public void testCalculateBMI() throws Exception {
        float a=20f;
        float b= 30f;
        assertEquals(test.calculateBMI(a,b),0.108444444835186,0);

    }

    @Test
    public void testInterpretBMI() throws Exception {
        assertEquals(test.interpretBMI(12.11f), "Severely underweight");
        assertEquals(test.interpretBMI(30f), "Obese");
        assertEquals(test.interpretBMI(20f), "Normal");
        assertEquals(test.interpretBMI(17f), "Underweight");
        assertEquals(test.interpretBMI(29f), "Overweight");

    }
}