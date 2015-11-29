package buffalo.suny.software.atmedicine.activities;

import android.support.design.widget.TextInputLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;

import junit.framework.TestCase;

import org.junit.Test;

import buffalo.suny.software.atmedicine.R;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/28/15.
 */


public class ChangePasswordActivityTest extends ActivityUnitTestCase<ChangePasswordActivity> {

    private ChangePasswordActivity activity;
    public ChangePasswordActivityTest() {
        super(ChangePasswordActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
    }


    @Test
    public void testValidateCurrentPassword() throws Exception {

        assertEquals(true,activity.validateCurrentPassword("abcd"));
    }

    @Test
    public void testValidateNewPassword() throws Exception {
        assertEquals(true,activity.validateNewPassword("abcd"));
    }

    @Test
    public void testValidateConfirmPassword() throws Exception {
        assertEquals(true,activity.validateCurrentPassword("abcd"));

    }
}