package buffalo.suny.software.atmedicine.utility;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.Display;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by raman on 10/27/15.
 */
public class Utility {
    public Utility() {
        super();
    }

    public int getScreenWidth(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public int nthLastIndexOf(String str, char c, int n) {
        if (str == null || n < 1)
            return -1;
        int pos = str.length();
        while (n-- > 0 && pos != -1)
            pos = str.lastIndexOf(c, pos - 1);
        return pos;
    }

    public boolean isNetworkAvailable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public boolean isValidEmail(String email) {
        boolean isValidEmail = false;

        String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValidEmail = true;
        }

        return isValidEmail;
    }

    public String getAndroidID(Context context) {
        if (context == null)
            return "";

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return android_id;
    }
}
