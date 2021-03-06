package tw.tasker.babysitter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.layer.sdk.LayerClient;
import com.layer.sdk.LayerClient.DeletionMode;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.SortDescriptor;
import com.parse.ParseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.UserType;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.parse.ParseImpl;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;

/*
 * ConversationQueryAdapter.java
 * Drives the RecyclerView in the ConversationsActivity class. Shows a list of conversations sorted
 *  by the last message received. For each Conversation, it shows the participants (not including the
 *  locally authenticated user), the time of the last message received, and the contents of the last
 *  message.
 *
 *  This is just one possible implementation. You can edit the conversation_item.xml view to change
 *   what is shown for each Conversation, including adding icons for individual or group messages,
 *   indicating whether the latest message in a Conversation is unread, etc.
 */


public class ConversationQueryAdapter extends QueryAdapter<Conversation, ConversationQueryAdapter.ViewHolder> {

    //Inflates the view associated with each Conversation object returned by the Query
    private final LayoutInflater mInflater;

    //Handle the callbacks when the Conversation item is actually clicked. In this case, the
    // ConversationsActivity class implements the ConversationClickHandler
    private final ConversationClickHandler mConversationClickHandler;
    private Confirm mConfirm;

    //Constructor for the ConversationQueryAdapter
    //Sorts all conversations by last message received (ie, downloaded to the device)
    public ConversationQueryAdapter(Context context, LayerClient client, ConversationClickHandler conversationClickHandler, Callback callback) {
        super(client, getQuery(), callback);

        //Sets the LayoutInflator and Click callback handler
        mInflater = LayoutInflater.from(context);
        mConversationClickHandler = conversationClickHandler;

        UserType userType = AccountChecker.getUserType();
        switch (userType) {
            case PARENT:
                mConfirm = new ParentConfirm();
                break;

            case SITTER:
                mConfirm = new SitterConfirm();
                break;

            default:
                mConfirm = new ParentConfirm();
                break;
        }
    }

    private static Query<Conversation> getQuery() {
        Predicate predicate = new Predicate(
                Conversation.Property.ID,
                Predicate.Operator.IN,
                ParseHelper.findConversations()
        );

        SortDescriptor sortDescriptor = new SortDescriptor(
                Conversation.Property.LAST_MESSAGE_RECEIVED_AT,
                SortDescriptor.Order.DESCENDING
        );

        return Query.builder(Conversation.class)
                .predicate(predicate)
                .sortDescriptor(sortDescriptor)
                .build();
    }


    //When a new Conversation is created (ie, either locally, or by another user), a new ViewHolder is created
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //The conversation_item is just an example view you can use to display each Conversation in a list
        View itemView = mInflater.inflate(R.layout.item_list_conversation, null);

        //Tie the view elements to the fields in the actual view after it has been created
        ViewHolder holder = new ViewHolder(itemView, mConversationClickHandler);
        holder.avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
        holder.title1 = (TextView) itemView.findViewById(R.id.title1);
        holder.title2 = (TextView) itemView.findViewById(R.id.title2);
        holder.note = (TextView) itemView.findViewById(R.id.note);
        holder.time = (TextView) itemView.findViewById(R.id.time);
        holder.lastMsgContent = (TextView) itemView.findViewById(R.id.message);

        holder.match = (Button) itemView.findViewById(R.id.match);
        holder.cancel = (Button) itemView.findViewById(R.id.cancel);

        return holder;
    }

    //After the ViewHolder is created, we need to populate the fields with information from the Conversation
    public void onBindViewHolder(final ViewHolder viewHolder, Conversation conversation) {
        if (conversation == null) {
            // If the item no longer exists, the ID probably migrated.
            refresh();
            return;
        }

        Log.d("Activity", "binding conversation: " + conversation.getId() + " with participants: " + conversation.getParticipants().toString());

        //viewHolder.favorite = ParseHelper.getFavorite(conversation.getId().toString());

        //Set the Conversation (so when this item is clicked, we can start a ConversationActivity and
        // show all the messages associated with it)
        viewHolder.conversation = conversation;
        mConfirm.loadStatus(viewHolder);

        //Go through all the User IDs in the Conversation and find the matching human readable
        // handles from Parse
        String participants = "";
        List<String> users = conversation.getParticipants();
        for (int i = 0; i < users.size(); i++) {
            if (!users.get(i).equals(ParseUser.getCurrentUser().getObjectId())) {
                //Format the String so there is a comma after every account
                if (participants.length() > 0)
                    participants += ", ";

                //Add the human readable account to the String
                participants += ParseImpl.getUsername(users.get(i));
            }
        }

        DisplayUtils.loadAvatorWithUrl(viewHolder.avatar, mConfirm.getAvatarUrl());

        viewHolder.title1.setText(mConfirm.getTitle1());

        //Grab the last message in the conversation and show it in the format "sender: last message content"
        final Message message = conversation.getLastMessage();
        if (message != null) {
            viewHolder.lastMsgContent.setText(LayerImpl.getMessageText(message));
        } else {
            viewHolder.lastMsgContent.setText("");
        }

        viewHolder.title2.setText(mConfirm.getTitle2());
        //Draw the date the last message was received (downloaded from the server)
        viewHolder.time.setText(LayerImpl.getReceivedAtTime(message));
        viewHolder.note.setText(mConfirm.getNote());

        viewHolder.match.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MessagePart part = LayerImpl.getLayerClient().newMessagePart("接受托育，開始聊天吧~");
                Message msg = LayerImpl.getLayerClient().newMessage(part);
                viewHolder.conversation.send(msg);

                String conversationId = viewHolder.conversation.getId().toString();
                mConfirm.agree(conversationId);

                viewHolder.onConfirmClick();
                //viewHolder.isConfirm = true;

            }
        });

        viewHolder.cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String conversationId = viewHolder.conversation.getId().toString();
                mConfirm.cancel(conversationId);

                viewHolder.conversation.delete(DeletionMode.ALL_PARTICIPANTS);
            }
        });
    }

    //This example app only has one kind of view type, but you could support different TYPES of
    // Conversations if you were so inclined
    public int getItemViewType(int i) {
        return 1;
    }

    public static interface ConversationClickHandler {
        public void onConversationClick(Conversation conversation);

        public boolean onConversationLongClick(Conversation conversation);

        public void onConfirmClick(Conversation conversation);
    }

    //The fields in the ViewHolder reflect the conversation_item view
    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements OnClickListener, View.OnLongClickListener {

        public final ConversationClickHandler conversationClickHandler;
        //For each Conversation item in the RecyclerView list, we show the participants, time,
        // contents of the last message, and have a reference to the conversation so when it is
        // clicked we can start the ConversationActivity
        public CircleImageView avatar;
        public TextView title1;
        public TextView title2;
        public TextView time;
        public TextView note;
        public TextView lastMsgContent;
        public Conversation conversation;
        public Button match;
        public Button cancel;
        //public BabysitterFavorite favorite;

        //Registers the click listener callback handler
        public ViewHolder(View itemView, ConversationClickHandler conversationClickHandler) {
            super(itemView);
            this.conversationClickHandler = conversationClickHandler;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        //Execute the callback when the conversation is clicked
        public void onClick(View v) {
            conversationClickHandler.onConversationClick(conversation);
        }

        //Execute the callback when the conversation is long-clicked
        public boolean onLongClick(View v) {
            return conversationClickHandler.onConversationLongClick(conversation);
        }

        public void onConfirmClick() {
            conversationClickHandler.onConfirmClick(conversation);
        }
    }
}