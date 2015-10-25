package tw.tasker.babysitter.adapter;

import com.parse.ParseUser;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.model.BabysitterFavorite;

public abstract class Confirm {
    abstract void agree(String conversationId);

    abstract void cancel(String conversationId);

    abstract void loadStatus(ConversationQueryAdapter.ViewHolder viewHolder);

    public abstract String getTitle1();

    public abstract String getTitle2();

    public abstract String getNote();

    public abstract String getAvatarUrl();

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
