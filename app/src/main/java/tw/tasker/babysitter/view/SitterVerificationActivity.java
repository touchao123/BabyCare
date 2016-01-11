package tw.tasker.babysitter.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import tw.tasker.babysitter.R;

public class SitterVerificationActivity extends BaseActivity implements OnClickListener {
    public static final int STEP_SYNC_DATA = 0;
    public static final int STEP_VERIFY_CODE = 1;

    private SignUpListener mListener = new Listener();

    private FragmentTransaction mFragmentTransaction;
    private Fragment mSitterSyncDataFragment;
    private Fragment mSitterVerifyCodeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        mSitterSyncDataFragment = SitterSyncDataFragment.newInstance(mListener);
        mSitterVerifyCodeFragment = VerifyCodeFragment.newInstance(mListener);

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (savedInstanceState == null) {
            Fragment fragment = mSitterSyncDataFragment;
            mFragmentTransaction.add(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onClick(View v) {
    }

    private final class Listener implements SignUpListener {

        @Override
        public void onSwitchToNextFragment(int type) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            switch (type) {
                case STEP_SYNC_DATA:
                    ft.replace(R.id.container, mSitterSyncDataFragment).commit();
                    break;

                case STEP_VERIFY_CODE:
                    ft.replace(R.id.container, mSitterVerifyCodeFragment).commit();
                    break;
            }

        }

    }


}
