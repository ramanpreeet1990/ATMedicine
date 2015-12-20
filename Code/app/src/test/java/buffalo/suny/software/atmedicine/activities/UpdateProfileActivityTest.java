package buffalo.suny.software.atmedicine.activities;

import android.test.ActivityUnitTestCase;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/28/15.
 */
public class UpdateProfileActivityTest extends ActivityUnitTestCase<UpdateProfileActivity> {
    private UpdateProfileActivity activity;
    public UpdateProfileActivityTest() {
        super(UpdateProfileActivity.class);
    }

    @Test
    public void testValidateLastName() throws Exception {
        assertEquals(activity.validateLastName("Smith"),true);

    }

    @Test
    public void testValidateFirstName() throws Exception {
        assertEquals(activity.validateFirstName("Will"),true);

    }

    @Test
    public void testValidatePassword() throws Exception {
        assertEquals(activity.validatePassword("abcdef"),true);

    }
}