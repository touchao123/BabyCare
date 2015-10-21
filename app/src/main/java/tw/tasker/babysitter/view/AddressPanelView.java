package tw.tasker.babysitter.view;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.model.HomeEvent;

class AddressPanelView implements View.OnClickListener, View.OnFocusChangeListener, TextView.OnEditorActionListener {
    private final View mRootView;

    private LinearLayout mAddressPanel;
    private TextView mAddressText;
    private EditText mAddressEdit;
    private ImageView mLocation;
    private Button mCancel;
    private Button mAgree;

    public AddressPanelView(View rootView) {
        mRootView = rootView;

        initView();
        initListener();
        initData();
    }

    private void initView() {
        mAddressPanel = (LinearLayout) mRootView.findViewById(R.id.address_panel);

        mAddressText = (TextView) mRootView.findViewById(R.id.address_text);
        mAddressEdit = (EditText) mRootView.findViewById(R.id.address_edit);
        mAgree = (Button) mRootView.findViewById(R.id.agree);
        mCancel = (Button) mRootView.findViewById(R.id.cancel);
        mLocation = (ImageView) mRootView.findViewById(R.id.location);

    }

    private void initListener() {
        mAddressText.setOnClickListener(this);
        mAddressEdit.setOnFocusChangeListener(this);
        mAddressEdit.setOnEditorActionListener(this);
        mAgree.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    private void initData() {
        mAgree.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);
        mAddressEdit.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.address_text:
                changeToAddressEditMode();
                break;

            case R.id.agree:
                searchAddress();
                break;

            case R.id.cancel:
                mAddressEdit.setText("");
                searchAddress();
                //changeToAndressTextMode();
                break;

            default:
                break;
        }
    }

    private void changeToAndressTextMode() {
        mAddressText.setVisibility(View.VISIBLE);
        mAddressEdit.setVisibility(View.GONE);
        mAgree.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);
        mLocation.setVisibility(View.VISIBLE);

        EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_TOGGLE_KEYPAD));
    }

    private void changeToAddressEditMode() {
        mAddressText.setVisibility(View.GONE);
        mAddressEdit.setVisibility(View.VISIBLE);
        mLocation.setVisibility(View.GONE);
        mAddressEdit.requestFocus();
        mAgree.setVisibility(View.VISIBLE);
        mCancel.setVisibility(View.VISIBLE);

        EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_TOGGLE_KEYPAD));
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            changeToAndressTextMode();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        searchAddress();
        return true;
    }

    private void searchAddress() {
        String addr = mAddressEdit.getText().toString();

        if (addr.isEmpty()) {
            mAddressText.setText("範例：高雄市鳳山區");
        } else {
            mAddressText.setText(addr);
        }
        Config.keyWord = addr;

        mAddressText.setVisibility(View.VISIBLE);
        mAddressEdit.setVisibility(View.GONE);
        mLocation.setVisibility(View.VISIBLE);

        EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_TOGGLE_KEYPAD));
        EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_QUERY));

    }

    public void show() {
        mAddressPanel.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mAddressPanel.setVisibility(View.GONE);
    }

}
