package buffalo.suny.software.atmedicine.activities;

import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import buffalo.suny.software.atmedicine.R;

import static org.junit.Assert.*;

/**
 * Created by alokasok on 11/28/15.
 */
public class CreateAccountActivityTest extends ActivityUnitTestCase<CreateAccountActivity> {

    private CreateAccountActivity activity;
    public CreateAccountActivityTest() {
        super(CreateAccountActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
    }

    @SmallTest
    public void testLayoutExists(){
        assertNotNull(activity.findViewById(R.id.txt_input_email));
        assertNotNull(activity.findViewById(R.id.txt_input_password));
        assertNotNull(activity.findViewById(R.id.btn_change_password));

    }


    @Test
    public void testGetAccount() throws Exception {
        assertNotNull(activity.getAccount());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}