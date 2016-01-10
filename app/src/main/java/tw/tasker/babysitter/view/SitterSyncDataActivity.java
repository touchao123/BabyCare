package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import tw.tasker.babysitter.R;

public class SitterSyncDataActivity extends BaseActivity implements OnClickListener {
    private FragmentTransaction mFragmentTransaction;
    private Fragment mSitterSyncDataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        mSitterSyncDataFragment = SitterSyncDataFragment.newInstance(null);

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (savedInstanceState == null) {
            Fragment fragment = mSitterSyncDataFragment;
            mFragmentTransaction.add(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onClick(View v) {
    }

}
