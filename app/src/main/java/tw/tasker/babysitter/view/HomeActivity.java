package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import tw.tasker.babysitter.R;

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        if (savedInstanceState == null) {

            Fragment fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
        }
        //getActionBar().setDisplayShowHomeEnabled(false);
        ParseAnalytics.trackAppOpened(getIntent());
        // 後續看要不要放在ActionBar之類的
        // mUserInfo.setText("使用者資訊(" + user.getObjectId() + ")："+
        // user.getUsername() );

        addUserToInstallation();
    }

    private void addUserToInstallation() {
        if (ParseUser.getCurrentUser() != null) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", ParseUser.getCurrentUser());
            installation.saveInBackground();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initLocation();
    }


}
