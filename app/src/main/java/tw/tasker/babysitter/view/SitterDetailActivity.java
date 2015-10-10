package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import tw.tasker.babysitter.R;
import tw.tasker.babysitter.utils.AccountChecker;

public class SitterDetailActivity extends BaseActivity implements OnClickListener {
    private FragmentTransaction mFragmentTransaction;
    private Fragment mSitterDetailFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        mSitterDetailFragment = SitterDetailFragment.newInstance(null);

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (savedInstanceState == null) {
            Fragment fragment = null;
            if (AccountChecker.isSitter()) {
                fragment = mSitterDetailFragment;
            } else {
                fragment = mSitterDetailFragment;
            }

            mFragmentTransaction.add(R.id.container, fragment).commit();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
    }


}
