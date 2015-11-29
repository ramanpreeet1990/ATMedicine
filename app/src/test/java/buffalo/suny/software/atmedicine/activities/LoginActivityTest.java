package buffalo.suny.software.atmedicine.activities;

import android.test.ActivityUnitTestCase;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/28/15.
 */
public class LoginActivityTest extends ActivityUnitTestCase<LoginActivity> {
    private LoginActivity activity;
    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
    }

    @Test
    public void testValidateEmail() throws Exception {
        assertEquals(activity.validateEmail("abc@gmail.com"),true);

    }

    @Test
    public void testValidatePassword() throws Exception {
        assertEquals(activity.validatePassword("abcdef"),true);

    }

    @Test
    public void testGetAccount() throws Exception {

        assertNotNull(activity.getAccount());
    }

}