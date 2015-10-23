package tw.tasker.babysitter.utils;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.Config;
import tw.tasker.babysitter.model.Babysitter;
import tw.tasker.babysitter.model.BabysitterFavorite;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UserInfo;

public class ParseHelper {

    // Only for signing up
    public static Babysitter mSitter;
    public static UserInfo mParent;

    public static void pinSitterToCache(Babysitter sitter) {
        mSitter = sitter;
    }

    public static Babysitter getSitterFromCache() {
        return mSitter;
    }

    public static void pinParentToCache(UserInfo parent) {
        mParent = parent;
    }

    public static UserInfo getParentFromCache() {
        return mParent;
    }


    public static boolean isSuccess(ParseException e) {
        if (e == null) {
            return true;
        } else {
            return false;
        }
    }

    public static void addUserToInstallation() {

        if (AccountChecker.isLogin()) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", ParseUser.getCurrentUser());
            installation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException parseException) {
                    if (ParseHelper.isSuccess(parseException)) {
                    } else {
                    }
                }
            });
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
                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });

    }

    @DebugLog
    public static void pinSitter(Babysitter sitter) {
        Config.sitterObjectId = sitter.getObjectId();
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

    @DebugLog
    public static Babysitter getSitter() {
        String sitterObjectId = Config.sitterObjectId;
        Babysitter sitter = new Babysitter();

        ParseQuery<Babysitter> query = Babysitter.getQuery();
        query.fromLocalDatastore();

        try {
            System.out.println(query.find().size());
            return query.get(sitterObjectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sitter;
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
                } else {
                    EventBus.getDefault().post(parseException);
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
                } else {
                    EventBus.getDefault().post(parseException);
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

    public static UserInfo getParent() {
        UserInfo parent = new UserInfo();

        ParseQuery<UserInfo> query = UserInfo.getQuery();
        query.fromLocalDatastore();

        try {
            return query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parent;
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
                }
            }
        });
    }

    @DebugLog
    public static List<String> findConversations() {
        final List<String> conversations = new ArrayList<>();

        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();

        try {
            List<BabysitterFavorite> favorites = query.find();
            for (BabysitterFavorite favorite : favorites) {
                conversations.add(favorite.getConversationId());
            }

        } catch (ParseException parseException) {

        }

        return conversations;
    }

    @DebugLog
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

    public static void runLogin(String account, String password) {
        ParseUser.logInInBackground(account, password, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException parseException) {
                if (ParseHelper.isSuccess(parseException)) {
                    EventBus.getDefault().post(user);
                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });

    }

    public static void doParentSignUp(String account, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(account);
        user.setPassword(account);
        user.put("userType", "parent");

        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException parseException) {
                if (isSuccess(parseException)) {
                    EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_PARENT_SIGNUP_DONE));
                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });
    }

    public static void doSitterSignUp(String account, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(account);
        user.setPassword(account);
        user.put("userType", "sitter");

        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException parseException) {
                if (isSuccess(parseException)) {
                    EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_SITTER_SIGNUP_DONE));
                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });
    }


    public static void addUserInfo(UserInfo userInfo) {

        userInfo.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException parseException) {
                if (isSuccess(parseException)) {
                    EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_ADD_PARENT_INFO_DOEN));
                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });
    }

    public static void addSittrInfo(Babysitter sitterInfo) {
        sitterInfo.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException parseException) {
                if (ParseHelper.isSuccess(parseException)) {
                    EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_ADD_SITTER_INFO_DOEN));
                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });
    }

    public static Babysitter getSitterWithConversationId(String conversationId) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("conversationId", conversationId);
        //query.include("UserInfo");

        BabysitterFavorite favorite = null;
        try {
            favorite = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (favorite == null) {
            return null;
        } else {
            return favorite.getBabysitter();
        }
    }

    public static UserInfo getParentrWithConversationId(String conversationId) {
        ParseQuery<BabysitterFavorite> query = BabysitterFavorite.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("conversationId", conversationId);
        //query.include("UserInfo");

        BabysitterFavorite favorite = null;
        try {
            favorite = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (favorite == null) {
            return null;
        } else {
            return favorite.getUserInfo();
        }
    }

}

