package tw.tasker.babysitter.view;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.ParseHelper;

class TalkToSitter {
    // private ParentHomeFragment parentHomeFragment;
    // message
    private ArrayList<String> mTargetParticipants;
    //The owning conversation
    private Conversation mConversation;

    public TalkToSitter() {
    }

    public void send(Babysitter sitter) {
        String pushMessage = "家長[" + ParseUser.getCurrentUser().getUsername() + "]，想找你帶小孩唷~";
        ParseHelper.pushTextToSitter(sitter, pushMessage);
        newConversationWithSitter(sitter);
    }

    protected void newConversationWithSitter(Babysitter sitter) {

        if (mTargetParticipants == null)
            mTargetParticipants = new ArrayList<>();

        mTargetParticipants.add(sitter.getUser().getObjectId());
        mTargetParticipants.add(ParseUser.getCurrentUser().getObjectId());

        //First Check to see if we have a valid Conversation object
        if (mConversation == null) {
            //Make sure there are valid participants. Since the Authenticated user will always be
            // included in a new Conversation, we check to see if there is more than one target participant
            if (mTargetParticipants.size() > 1) {

                //Create a new conversation, and tie it to the QueryAdapter
                mConversation = LayerImpl.getLayerClient().newConversation(mTargetParticipants);
                //createMessagesAdapter();

                addFavorite(sitter.getObjectId());

                //Once the Conversation object is created, we don't allow changing the Participant List
                // Note: this is an implementation choice. It is always possible to add/remove participants
                // after a Conversation has been created
                //hideAddParticipantsButton();

            } else {
                //showAlert("Send Message Error","You need to specify at least one participant before sending a message.");
                return;
            }
        }

        String text = "向您提出托育請求";

        //If the input is valid, create a new Message and send it to the Conversation
        if (mConversation != null && text != null && text.length() > 0) {

            MessagePart part = LayerImpl.getLayerClient().newMessagePart(text);
            Message msg = LayerImpl.getLayerClient().newMessage(part);
            mConversation.send(msg);
        } else {
            //showAlert("Send Message Error","You cannot send an empty message.");
        }

    }

    private void addFavorite(String sitterObjectId) {
        Babysitter babysitter = ParseObject.createWithoutData(Babysitter.class, sitterObjectId);
        UserInfo userInfo = ParseObject.createWithoutData(UserInfo.class, ParseHelper.getParent().getObjectId());

        BabysitterFavorite babysitterfavorite = new BabysitterFavorite();

        //mBabysitterFavorite = babysitterfavorite;

        // favorite.put("baby", mBaby);
        babysitterfavorite.setBabysitter(babysitter);
        babysitterfavorite.setUserInfo(userInfo);

        babysitterfavorite.put("user", ParseUser.getCurrentUser());
        babysitterfavorite.setIsParentConfirm(true);
        babysitterfavorite.setIsSitterConfirm(false);
        babysitterfavorite.setConversationId(mConversation.getId().toString());

        babysitterfavorite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (parseException == null) {
                    EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_SEND));
                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });
    }

}
