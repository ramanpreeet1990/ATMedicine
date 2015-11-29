package buffalo.suny.software.atmedicine.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

import org.junit.Test;
import static org.junit.Assert.assertTrue;


import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.activities.ForgotPasswordActivity;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/27/15.
 */
public class ForgotPasswordActivityTest extends ActivityUnitTestCase<ForgotPasswordActivity> {

    public ForgotPasswordActivity mFirstTestActivity;


    public ForgotPasswordActivityTest() {
        super(ForgotPasswordActivity.class);
    }


    @Test
    public void testValidateEmail() throws Exception {
        /*mFirstTestActivity = getActivity();
        TextView mFirstTestText;
        mFirstTestText = (TextView) mFirstTestActivity.findViewById(R.id.txt_input_email);
        mFirstTestText*/
        //System.out.println("Output is " + mFirstTestActivity.validateEmail("testgmail.com"));
        //assertEquals(true, mFirstTestActivity.validateEmail("test@gmail.com"));
        //assertEquals(true, mFirstTestActivity.validateEmail("testgmail.com"));
        //assertTrue(false);
        //assertEquals(false,mFirstTestActivity.validateEmail("testgmail.com"));
        assertEquals(mFirstTestActivity.validateEmail("testgmail.com"),true);
        assertFalse(true);

    }

    @Test
    public void testgetAccount() throws Exception{
        assertEquals(mFirstTestActivity.getAccount(), true);
    }


}