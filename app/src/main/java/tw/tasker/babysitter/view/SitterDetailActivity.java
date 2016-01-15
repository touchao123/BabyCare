package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import tw.tasker.babysitter.R;
import tw.tasker.babysitter.utils.AccountChecker;

public class SitterDetailActivity extends BaseActivity  {
    private FragmentTransaction mFragmentTransaction;
    private Fragment mSitterDetailFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_stop);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
