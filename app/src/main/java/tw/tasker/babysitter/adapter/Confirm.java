package tw.tasker.babysitter.adapter;

import com.parse.ParseUser;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.model.BabysitterFavorite;

public abstract class Confirm {
    abstract void updateConfirm(String conversationId);

    abstract void loadStatus(ConversationQueryAdapter.ViewHolder viewHolder);

    abstract String getParticipatsTitle();

    abstract String getName();

    @DebugLog
    boolean isConfirmBothParentAndSitter(BabysitterFavorite favorite) {

        if (favorite.getIsParentConfirm() && favorite.getIsSitterConfirm()) {
            return true;
        }
        return false;
    }

    @DebugLog
    boolean isUserSendRequest(BabysitterFavorite favorite) {
        String favoriteUserId = favorite.getUser().getObjectId();

        if (favoriteUserId.equals(ParseUser.getCurrentUser().getObjectId())) {
            return true;
        }
        return false;
    }
}
