package tw.tasker.babysitter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.SortDescriptor;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import tw.tasker.babysitter.R;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;
import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.ParseHelper;

/*
 * MessageQueryAdapter.java
 * Drives the RecyclerView in the ConversationActivity class. Shows a list of all messages sorted
 *  by the message Position. For each Message, it shows the contents (assuming it is plain text),
 *  the sender, and the date received (downloaded from the server).
 *
 *  This is just one possible implementation. You can edit the message_item.xml view to change
 *   what is shown for each Message, including adding icons for each individual, allowing photos or
 *   other rich media content, etc.
 */

public class MessageQueryAdapter extends QueryAdapter<Message, MessageQueryAdapter.ViewHolder> {

    //Inflates the view associated with each Message object returned by the Query
    private final LayoutInflater mInflater;

    //The parent view is required to ensure proper formatting of the messages (messages sent from
    // the authenticated user are right aligned, and messages from other users are left aligned). In
    // this case, the parent view is the RecyclerView in the ConversationActivity class.
    private final ViewGroup mParentView;

    //Handle the callbacks when the Message item is actually clicked. In this case, the
    // ConversationActivity class implements the MessageClickHandler
    private final MessageClickHandler mMessageClickHandler;

    private int mLeftColor;
    private int mRightColor;

    //Constructor for the MessasgeQueryAdapter
    //Sorts all messages belonging to this conversation by its position. This will guarantee all
    // messages will appear "in order"
    public MessageQueryAdapter(Context context, LayerClient client, ViewGroup recyclerView, Conversation conversation, MessageClickHandler messageClickHandler, Callback callback) {
        super(client, getQuery(conversation), callback);

        //Sets the LayoutInflator, Click callback handler, and the view parent
        mInflater = LayoutInflater.from(context);
        mMessageClickHandler = messageClickHandler;
        mParentView = recyclerView;

        mLeftColor = context.getResources().getColor(R.color.gray_light);
        mRightColor = context.getResources().getColor(R.color.primary_dark);
    }

    private static Query<Message> getQuery(Conversation conversation) {
        return Query.builder(Message.class)
                .predicate(new Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO, conversation))
                .sortDescriptor(new SortDescriptor(Message.Property.POSITION, SortDescriptor.Order.ASCENDING))
                .build();
    }

    //When a Message is added to this conversation, a new ViewHolder is created
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //The message_item is just an example view you can use to display each message in a list
        View itemView = mInflater.inflate(R.layout.item_list_message, mParentView, false);

        //Tie the view elements to the fields in the actual view after it has been created
        ViewHolder holder = new ViewHolder(itemView, mMessageClickHandler);
        //holder.sender = (TextView) itemView.findViewById(R.id.senderID);
        holder.content = (TextView) itemView.findViewById(R.id.msgContent);

        holder.timeLeft = (TextView) itemView.findViewById(R.id.sendTime_left);
        holder.timeRight = (TextView) itemView.findViewById(R.id.sendTime_right);

        holder.contentLayout = (LinearLayout) itemView.findViewById(R.id.content_layout);
        holder.itemLayout = (LinearLayout) itemView.findViewById(R.id.item_layout);

        holder.avatar = (CircleImageView) itemView.findViewById(R.id.avatar);

        return holder;
    }

    //After the ViewHolder is created, we need to populate the fields with information from the Message
    public void onBindViewHolder(ViewHolder viewHolder, final Message message) {
        if (message == null) {
            // If the item no longer exists, the ID probably migrated.
            refresh();
            return;
        }

        String senderId = "";
        if (message != null)
            senderId = message.getSender().getUserId();

        //Set the content of the message, sender, and received time
        viewHolder.content.setText(LayerImpl.getMessageText(message));
        //viewHolder.sender.setText(ParseImpl.getUsername(senderId));
        viewHolder.timeRight.setText(" " + LayerImpl.getReceivedAtTime(message));
        viewHolder.timeLeft.setText(LayerImpl.getReceivedAtTime(message) + " ");

        viewHolder.message = message;

        //Right align if the authenticated user (local user) sent the message, otherwise left align
        // the message box
        LinearLayout.LayoutParams itemLayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (message != null && !senderId.equals(LayerImpl.getLayerClient().getAuthenticatedUserId())) {
            itemLayoutparams.gravity = Gravity.LEFT;

            viewHolder.content.setBackgroundColor(mLeftColor);
            //viewHolder.contentLayout.setBackgroundColor(mLeftColor);
            viewHolder.timeLeft.setVisibility(View.GONE);
            viewHolder.timeRight.setVisibility(View.VISIBLE);

            String imageUrl = "";
            if (AccountChecker.isSitter()) {
                UserInfo parent = ParseHelper.getParentFromCache();
                if (parent.getAvatarFile() != null) {
                    imageUrl = parent.getAvatarFile().getUrl();
                }
            } else {
                Babysitter sitter = ParseHelper.getSitterFromCache();
                if (sitter.getAvatarFile() != null) {
                    imageUrl = sitter.getAvatarFile().getUrl();
                }
            }
            DisplayUtils.loadAvatorWithUrl(viewHolder.avatar, imageUrl);

            viewHolder.avatar.setVisibility(View.VISIBLE);

            viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(message);
                }
            });
        } else {
            itemLayoutparams.gravity = Gravity.RIGHT;

            viewHolder.content.setBackgroundColor(mRightColor);
            //viewHolder.contentLayout.setBackgroundColor(mRightColor);
            viewHolder.timeLeft.setVisibility(View.VISIBLE);
            viewHolder.timeRight.setVisibility(View.GONE);

            viewHolder.avatar.setVisibility(View.GONE);
        }
        viewHolder.itemLayout.setLayoutParams(itemLayoutparams);

        LinearLayout.LayoutParams contentLayoutparams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        contentLayoutparams.weight = 1;
        viewHolder.contentLayout.setLayoutParams(contentLayoutparams);


    }

    //This example app only has one kind of Message type, but you could support different types
    // (such as images, location, audio, etc) if you wanted
    public int getItemViewType(int i) {
        return 1;
    }

    public static interface MessageClickHandler {
        void onMessageClick(Message message);

        boolean onMessageLongClick(Message message);
    }

    //For each Message item in the RecyclerView list, we show the sender, time, and contents of the
    // message. We also grab some layout items to help with right/left aligning the message
    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public final MessageClickHandler messageClickHandler;
        public LinearLayout contentLayout;
        public LinearLayout itemLayout;
        public CircleImageView avatar;
        public TextView sender;
        public TextView timeLeft;
        public TextView timeRight;
        public TextView content;
        public Message message;

        //Registers the click listener callback handler
        public ViewHolder(View itemView, MessageClickHandler messageClickHandler) {
            super(itemView);
            this.messageClickHandler = messageClickHandler;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        //Execute the callback when the message is clicked
        public void onClick(View v) {
            messageClickHandler.onMessageClick(message);
        }

        //Execute the callback when the conversation is long-clicked
        public boolean onLongClick(View v) {
            return messageClickHandler.onMessageLongClick(message);
        }
    }
}