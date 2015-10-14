package tw.tasker.babysitter.utils;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.UserInfo;

public class ParseHelper {
    public static boolean isSuccess(ParseException e) {
        if (e == null) {
            return true;
        } else {
            return false;
        }
    }

    public static void addUserToInstallation() {
        if (ParseUser.getCurrentUser() != null) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", ParseUser.getCurrentUser());
            installation.saveInBackground();
        }
    }

    // Sitter
    public static void loadSitterProfileData() {
        ParseQuery<Babysitter> query = Babysitter.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<Babysitter>() {

            @Override
            public void done(Babysitter sitter, ParseException parseException) {
                if (isSuccess(parseException)) {
                    EventBus.getDefault().post(sitter);

                    // Config.sitterInfo = sitter;
                    // loadSitterFavoriteData(sitter);

                } else {
                    EventBus.getDefault().post(parseException);
                    // DisplayUtils.makeToast(getActivity(), "查不到你的資料!");
                }
            }
        });

    }

    public static void pinSitter(Babysitter sitter) {
        sitter.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (isSuccess(parseException)) {

                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });
    }

    public static void loadSitterFavoriteData(Babysitter sitter) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.whereEqualTo("Babysitter", sitter);
        query.include("UserInfo");
        query.findInBackground(new FindCallback<BabysitterFavorite>() {

            @Override
            public void done(List<BabysitterFavorite> favorites, ParseException parseException) {
                if (isSuccess(parseException)) {
                    EventBus.getDefault().post(favorites);
                    //Config.favorites = favorites;
                } else {
                    EventBus.getDefault().post(parseException);
                    //DisplayUtils.makeToast(this, "查不到你的資料!");
                }
            }
        });
    }

    // Parent
    public static void loadParentsProfileData() {
        ParseQuery<UserInfo> query = UserInfo.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<UserInfo>() {

            @Override
            public void done(UserInfo parent, ParseException parseException) {
                if (isSuccess(parseException)) {
                    EventBus.getDefault().post(parent);
                    //Config.userInfo = userInfo;
                    //loadParentFavoriteData(userInfo);
                } else {
                    EventBus.getDefault().post(parseException);
                    // DisplayUtils.makeToast(getActivity(), "查不到你的資料!");
                }
            }
        });
    }

    public static void pinParent(UserInfo parent) {
        parent.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (isSuccess(parseException)) {

                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });

    }

    public static void loadParentFavoriteData(UserInfo parent) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.whereEqualTo("UserInfo", parent);
        query.whereEqualTo("isSitterConfirm", true);
        query.include("Babysitter");
        query.findInBackground(new FindCallback<BabysitterFavorite>() {

            @Override
            public void done(List<BabysitterFavorite> favorites, ParseException parseException) {
                if (isSuccess(parseException)) {
                    EventBus.getDefault().post(favorites);
                } else {
                    EventBus.getDefault().post(favorites);
                    //Config.favorites = favorites;
                    //addConversations(favorites.size());
                    //DisplayUtils.makeToast(this, "查不到你的資料!");
                }
            }
        });
    }

    @DebugLog
    public static List<String> findConversations() {
        final List<String> conversations = new ArrayList<>();

        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<BabysitterFavorite>() {
            @Override
            public void done(List<BabysitterFavorite> favorites, ParseException parseException) {
                if (isSuccess(parseException)) {

                    for (BabysitterFavorite favorite : Config.favorites) {
                        conversations.add(favorite.getConversationId());
                        //Config.conversations = conversations;
                    }

                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });

        return conversations;
    }


    public static void pinFavorites(List<BabysitterFavorite> favorites) {
        ParseObject.pinAllInBackground(favorites, new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (isSuccess(parseException)) {

                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });
    }

}

