package tw.tasker.babysitter.adapter;

/**
 * Created by vic on 10/9/15.
 */
public interface Confirm {
    void updateConfirm(String conversationId);

    void loadStatus(ConversationQueryAdapter.ViewHolder viewHolder);

    String getParticipatsTitle();

    String getName(String conversationId);
}
