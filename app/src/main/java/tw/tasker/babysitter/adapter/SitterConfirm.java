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
import tw.tasker.babysitter.adapter.ConversationQueryAdapter.ViewHolder;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;
import tw.tasker.babysitter.utils.AccountChecker;

public class SitterConfirm implements Confirm {

    @Override
    public String getParticipatsTitle() {
        String participatsTitle = "爸媽:";
        return participatsTitle;
    }

    @Override
    public String getName(String conversationId) {
        for (BabysitterFavorite favorite : Config.favorites) {
            if (favorite.getConversationId().equals(conversationId)) {
                UserInfo parent = favorite.getUserInfo();
                return parent.getName();
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
                    favorite.setIsSitterConfirm(true);
                    favorite.saveInBackground();
                }
            }
        });
    }

    @Override
    public void loadStatus(ViewHolder viewHolder) {
        loadSitterFavoriteData(Config.sitterInfo, viewHolder);
    }

    private void loadSitterFavoriteData(Babysitter sitter, final ViewHolder viewHolder) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();

//        if (AccountChecker.getUserType() == PARENT) {
//            query.whereEqualTo("UserInfo", Config.userInfo);
//        } else if (AccountChecker.getUserType() == UserType.SITTER) {
        query.whereEqualTo("Babysitter", sitter);
        query.include("UserInfo");
//        }

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
    private void hideButton(ViewHolder viewHolder) {
        viewHolder.match.setVisibility(View.GONE);
        viewHolder.cancel.setVisibility(View.GONE);
    }

    @DebugLog
    private void showButton(ViewHolder viewHolder) {
        viewHolder.match.setVisibility(View.VISIBLE);
        viewHolder.cancel.setVisibility(View.VISIBLE);
    }

    @DebugLog
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
