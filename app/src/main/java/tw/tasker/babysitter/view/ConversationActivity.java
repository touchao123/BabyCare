package tw.tasker.babysitter.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.messaging.Conversation;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.adapter.ConversationQueryAdapter;
import tw.tasker.babysitter.adapter.QueryAdapter;
import tw.tasker.babysitter.layer.LayerCallbacks;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.parse.ParseImpl;
import tw.tasker.babysitter.utils.LogUtils;

public class ConversationActivity extends ActionBarActivity implements LayerCallbacks, ConversationQueryAdapter.ConversationClickHandler {
    public static final String PARSE_DATA_KEY = "com.parse.Data";
    private TextView mInfo;
    private Button mOk;
    private UserInfo mUserInfo;
    private ListView mListView;
    //The Query Adapter that grabs all Conversations and displays them based on the last lastMsgContent
    private ConversationQueryAdapter mConversationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        initLayer();
    }

    private void initLayer() {
        //Initializes and connects the LayerClient if it hasn't been created already
        LayerImpl.initialize(getApplicationContext());

        //Registers the activity so callbacks are executed on the correct class
        LayerImpl.setContext(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Registers the activity so callbacks are executed on the correct class
        LayerImpl.setContext(this);

        //Runs a Parse Query to return all users registered with the app
        ParseImpl.cacheAllUsers();


        //If the user is not authenticated, make sure they are logged in, and if they are, re-authenticate
        if (!LayerImpl.isAuthenticated()) {

            if (ParseImpl.getRegisteredUser() == null) {

                LogUtils.LOGD("Activity", "User is not authenticated or logged in - returning to login screen");
                Intent intent = new Intent(ConversationActivity.this, LogInActivity.class);
                startActivity(intent);

            } else {

                LogUtils.LOGD("Activity", "User is not authenticated, but is logged in - re-authenticating user");
                LayerImpl.authenticateUser();

            }

            //Everything is set up, so start populating the Conversation list
        } else {

            LogUtils.LOGD("Activity", "Starting conversation view");
            setupConversationView();
        }

    }

    //Set up the Query Adapter that will drive the RecyclerView on the conversations_screen
    private void setupConversationView() {

        LogUtils.LOGD("Activity", "Setting conversation view");

        //Grab the Recycler View and list all conversation objects in a vertical list
        RecyclerView conversationsView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        conversationsView.setLayoutManager(layoutManager);

        //The Query Adapter drives the recycler view, and calls back to this activity when the user
        // taps on a Conversation
        mConversationsAdapter = new ConversationQueryAdapter(getApplicationContext(), LayerImpl.getLayerClient(), this, new QueryAdapter.Callback() {
            @Override
            public void onItemInserted() {
                //Log.d("Activity", "Conversation Adapter, new conversation inserted");
            }
        });

        //Attach the Query Adapter to the Recycler View
        conversationsView.setAdapter(mConversationsAdapter);

        //Execute the Query
        mConversationsAdapter.refresh();
    }

    //Layer events
    @Override
    public void onLayerConnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLayerDisconnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLayerConnectionError(LayerException e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUserAuthenticated(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUserAuthenticatedError(LayerException e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUserDeauthenticated() {
        // TODO Auto-generated method stub

    }

    // Conversation
    @Override
    public void onConversationClick(Conversation conversation) {
        //If the Conversation is valid, start the MessageActivity and pass in the Conversation ID
        if (conversation != null && conversation.getId() != null && !conversation.isDeleted()
                && isConfirmBothParentAndSitter(conversation.getId().toString())
                ) {
            Intent intent = new Intent(ConversationActivity.this, MessageActivity.class);
            intent.putExtra("conversation-id", conversation.getId());


            getSitterInfoWithConversation(conversation.getId().toString());

            startActivity(intent);
        }

    }

    @DebugLog
    private void getSitterInfoWithConversation(String conversationId) {
        for (BabysitterFavorite favorite : Config.favorites) {
            if (favorite.getConversationId().equals(conversationId)) {
                Config.sitterInfo = favorite.getBabysitter();
            }
        }

    }


    private boolean isConfirmBothParentAndSitter(String conversationId) {
        for (BabysitterFavorite favorite : Config.favorites) {
//			/String favoriteUserId = favorite.getUser().getObjectId();
            String favoriteConversationId = favorite.getConversationId();

            if (favoriteConversationId.equals(conversationId) &&
                    favorite.getIsParentConfirm() && favorite.getIsSitterConfirm()) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean onConversationLongClick(Conversation conversation) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onConfirmClick(Conversation conversation) {
        //If the Conversation is valid, start the MessageActivity and pass in the Conversation ID
        if (conversation != null && conversation.getId() != null && !conversation.isDeleted()) {
            Intent intent = new Intent(ConversationActivity.this, MessageActivity.class);
            intent.putExtra("conversation-id", conversation.getId());
            startActivity(intent);
        }
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.push_info, menu);
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
