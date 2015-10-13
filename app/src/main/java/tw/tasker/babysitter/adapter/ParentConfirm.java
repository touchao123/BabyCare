package tw.tasker.babysitter.adapter;

import android.view.View;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;

public class ParentConfirm implements Confirm {

    @Override
    public String getParticipatsTitle() {
        String participatsTitle = "保母:";
        return participatsTitle;
    }

    @Override
    public String getName(String conversationId) {
        for (BabysitterFavorite favorite : Config.favorites) {
            if (favorite.getConversationId().equals(conversationId)) {
                Babysitter sitter = favorite.getBabysitter();
                return sitter.getName();
            }
        }
        return "";
    }

    @Override
    public void updateConfirm(String conversationId) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.whereEqualTo("conversationId", conversationId);
        query.getFirstInBackground(new GetCallback<BabysitterFavorite>() {

            @Override
            public void done(BabysitterFavorite favorite, ParseException exception) {
                if (favorite != null) {
                    favorite.setIsParentConfirm(true);
                    favorite.saveInBackground();
                }
            }
        });

    }

    @Override
    public void loadStatus(ConversationQueryAdapter.ViewHolder viewHolder) {
        loadParentFavoriteData(Config.userInfo, viewHolder);
    }


    private void loadParentFavoriteData(UserInfo parent, final ConversationQueryAdapter.ViewHolder viewHolder) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();

//        if (AccountChecker.getUserType() == PARENT) {
        // TODO Need to change the flow.
        query.whereEqualTo("UserInfo", parent);
        query.include("Babysitter");
////        } else if (AccountChecker.getUserType() == UserType.SITTER) {
//        query.whereEqualTo("Babysitter", parent);
////        }

        query.findInBackground(new FindCallback<BabysitterFavorite>() {

            @Override
            public void done(List<BabysitterFavorite> favorites, ParseException e) {
                if (AccountChecker.isNull(favorites)) {
                    //Toast.makeText(getActivity(), "查不到你的資料!", Toast.LENGTH_SHORT).show();

                } else {
                    Config.favorites = favorites;

                    for (BabysitterFavorite favorite : favorites) {
                        favorite.getIsParentConfirm();
                        favorite.getIsSitterConfirm();
                    }

                    String conversationId = viewHolder.conversation.getId().toString();

                    if (isConfirmBothParentAndSitter(conversationId) || isUserSendRequest(conversationId)) {
                        hideButton(viewHolder);
                    } else {
                        showButton(viewHolder);
                    }

                }
            }
        });
    }

    @DebugLog
    private void hideButton(ConversationQueryAdapter.ViewHolder viewHolder) {
        viewHolder.match.setVisibility(View.GONE);
        viewHolder.cancel.setVisibility(View.GONE);
    }

    @DebugLog
    private void showButton(ConversationQueryAdapter.ViewHolder viewHolder) {
        viewHolder.match.setVisibility(View.VISIBLE);
        viewHolder.cancel.setVisibility(View.VISIBLE);
    }

    @DebugLog
    private boolean isConfirmBothParentAndSitter(String conversationId) {
        for (BabysitterFavorite favorite : Config.favorites) {
            String favoriteConversationId = favorite.getConversationId();

            if (favoriteConversationId.equals(conversationId) &&
                    favorite.getIsParentConfirm() && favorite.getIsSitterConfirm()) {
                return true;
            }
        }
        return false;

    }

    @DebugLog
    private boolean isUserSendRequest(String conversationId) {
        for (BabysitterFavorite favorite : Config.favorites) {
            String favoriteUserId = favorite.getUser().getObjectId();
            String favoriteConversationId = favorite.getConversationId();

            if (favoriteConversationId.equals(conversationId) &&
                    favoriteUserId.equals(ParseUser.getCurrentUser().getObjectId())) {
                return true;
            }
        }
        return false;
    }

}
