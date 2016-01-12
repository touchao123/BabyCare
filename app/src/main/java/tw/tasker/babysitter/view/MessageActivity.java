package tw.tasker.babysitter.view;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.UserType;
import tw.tasker.babysitter.adapter.MessageQueryAdapter;
import tw.tasker.babysitter.adapter.QueryAdapter;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.parse.ParseImpl;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;


public class MessageActivity extends BaseActivity implements MessageQueryAdapter.MessageClickHandler, View.OnClickListener {

    private Conversation mConversation;
    private MessageQueryAdapter mMessagesAdapter;
    private RecyclerView mMessagesView;
    private Dialog mInfoDialog;

    private boolean mKeyboardListenersAttached = false;
    private ViewGroup targetView;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = targetView.getRootView().getHeight() - targetView.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            if (heightDiff <= contentViewTop) {
                onHideKeyboard();
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);
            }
        }
    };


    //Grab all the view objects on the message_screen layout when the Activity starts
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mMessagesView = (RecyclerView) findViewById(R.id.recycler_view);

        Button sendButton = (Button) findViewById(R.id.send_button);
        if (sendButton != null)
            sendButton.setOnClickListener(this);

        attachKeyboardListeners(mMessagesView);

    }

    protected void attachKeyboardListeners(ViewGroup group) {
        if (mKeyboardListenersAttached) {
            return;
        }

        targetView = group;
        if (targetView != null) {
            targetView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

            mKeyboardListenersAttached = true;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @DebugLog
    public void onEvent(Message message) {
        String conversationId = message.getConversation().getId().toString();

        UserType userType = AccountChecker.getUserType();
        if (userType == UserType.PARENT) { // 爸媽，抓保母資料
            mInfoDialog = DisplayUtils.getSitterDailog(this, conversationId);
        } else {
            mInfoDialog = DisplayUtils.getParentDailog(this, conversationId);
        }

        mInfoDialog.show();
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onResume() {
        super.onResume();

        //If the user is not Authenticated, check to see if we need to return to the Login Screen,
        // or if the User can be Authenticated silently in the background
        if (!LayerImpl.isAuthenticated()) {

            if (ParseImpl.getRegisteredUser() == null) {

                Intent intent = new Intent(MessageActivity.this, LogInActivity.class);
                startActivity(intent);

            } else {

                LayerImpl.authenticateUser();

            }

        } else {

            //Now check to see if this is a new Conversation, or if the Activity needs to render an
            // existing Conversation
            Uri conversationURI = getIntent().getParcelableExtra("conversation-id");
            if (conversationURI != null)
                mConversation = LayerImpl.getLayerClient().getConversation(conversationURI);

            //This is an existing Conversation, display the messages, otherwise, allow the user to
            // add/remove participants and create a new Conversation
            if (mConversation != null)
                setupMessagesView();
            else
                createNewConversationView();

        }
    }

    private void setupMessagesView() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMessagesView.setLayoutManager(layoutManager);

        createMessagesAdapter();

        setMessageTitle();
    }

    private void setMessageTitle() {

        if (AccountChecker.isSitter()) {
            setTitle(ParseHelper.getParentFromCache().getName());
        } else {
            setTitle(ParseHelper.getSitterFromCache().getName());
        }
    }

    private void createNewConversationView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMessagesView.setLayoutManager(layoutManager);
    }

    private void createMessagesAdapter() {

        mMessagesAdapter = new MessageQueryAdapter(getApplicationContext(), LayerImpl.getLayerClient(), mMessagesView, mConversation, this, new QueryAdapter.Callback() {

            public void onItemInserted() {
                mMessagesView.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });
        mMessagesView.setAdapter(mMessagesAdapter);

        mMessagesAdapter.refresh();

        mMessagesView.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    public void onMessageClick(Message message) {

    }

    public boolean onMessageLongClick(Message message) {
        return false;
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.send_button:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {

        //Grab the user's input
        EditText input = (EditText) findViewById(R.id.text_input);
        String text = getTextAsString(input);

        //If the input is valid, create a new Message and send it to the Conversation
        if (mConversation != null && text != null && text.length() > 0) {

            MessagePart part = LayerImpl.getLayerClient().newMessagePart(text);
            Message msg = LayerImpl.getLayerClient().newMessage(part);
            mConversation.send(msg);

            input.setText("");

        } else {
            DisplayUtils.makeToast(this, "請輸入要傳送的訊息。");
        }
    }

    protected String getTextAsString(EditText view) {

        if (view != null && view.getText() != null)
            return view.getText().toString();

        return "";
    }

    protected void onShowKeyboard(int keyboardHeight) {
        mMessagesView.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    protected void onHideKeyboard() {
        mMessagesView.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mKeyboardListenersAttached) {
            targetView.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }

}