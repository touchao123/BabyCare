package tw.tasker.babysitter.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.IntentUtil;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {

            if (AccountChecker.isSitter()) {
                startActivity(IntentUtil.startHomeActivity());
            } else {
                startActivity(IntentUtil.startConversationActivity());
            }

        } else {
            startActivity(new Intent(this, LogInActivity.class));
        }
    }

}
