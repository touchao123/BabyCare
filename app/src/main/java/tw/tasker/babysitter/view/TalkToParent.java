package tw.tasker.babysitter.view;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.layer.LayerImpl;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.ParseHelper;

public class TalkToParent {
    // message
    private ArrayList<String> mTargetParticipants;
    //The owning conversation
    private Conversation mConversation;

    public void send(UserInfo parent) {
        String pushMessage = "保母[" + ParseUser.getCurrentUser().getUsername() + "]，想幫您帶小孩唷~";
        ParseHelper.pushTextToParent(parent, pushMessage);
        newConversationWithParent(parent);

        parent.incrementTotalContact();
        ParseHelper.pinDataLocal(parent);
    }

    @DebugLog
    protected void newConversationWithParent(UserInfo parent) {

        if (mTargetParticipants == null)
            mTargetParticipants = new ArrayList<>();

        mTargetParticipants.add(parent.getUser().getObjectId());
        mTargetParticipants.add(ParseUser.getCurrentUser().getObjectId());

        //First Check to see if we have a valid Conversation object
        if (mConversation == null) {
            //Make sure there are valid participants. Since the Authenticated user will always be
            // included in a new Conversation, we check to see if there is more than one target participant
            if (mTargetParticipants.size() > 1) {

                //Create a new conversation, and tie it to the QueryAdapter
                mConversation = LayerImpl.getLayerClient().newConversation(mTargetParticipants);
                //createMessagesAdapter();

                addFavorite(parent.getObjectId());

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

    private void addFavorite(String parentObjectId) {

        Babysitter babysitter = ParseObject.createWithoutData(Babysitter.class, ParseHelper.getSitter().getObjectId());
        UserInfo userInfo = ParseObject.createWithoutData(UserInfo.class, parentObjectId);

        BabysitterFavorite babysitterfavorite = new BabysitterFavorite();

        //mBabysitterFavorite = babysitterfavorite;

        // favorite.put("baby", mBaby);
        babysitterfavorite.setBabysitter(babysitter);
        babysitterfavorite.setUserInfo(userInfo);

        babysitterfavorite.put("user", ParseUser.getCurrentUser());
        babysitterfavorite.setIsParentConfirm(false);
        babysitterfavorite.setIsSitterConfirm(true);
        babysitterfavorite.setConversationId(mConversation.getId().toString());

        ParseHelper.pinDataLocal(babysitterfavorite);

    }

}
