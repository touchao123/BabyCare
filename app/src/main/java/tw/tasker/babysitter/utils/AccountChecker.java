package tw.tasker.babysitter.utils;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import tw.tasker.babysitter.R;
import tw.tasker.babysitter.UserType;
import tw.tasker.babysitter.layer.LayerImpl;

public class AccountChecker {

    public static boolean isNull(Object object) {
        if (object == null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMatching(String value1, String value2) {
        if (value1.equals(value2)) {
            return true;
        } else {
            return false;
        }
    }

    public static UserType getUserType() {
        UserType userType = UserType.LATER;
        if (isLogin()) {
            if (isSitter()) {
                userType = UserType.SITTER;
            } else {
                userType = UserType.PARENT;
            }
        }
        return userType;
    }

    public static boolean isLogin() {
        if (isNull(ParseUser.getCurrentUser())) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isSitter() {
        try {
            String userType = ParseUser.getCurrentUser().getString("userType");
            if (userType.equals("sitter")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) { // Default parent if userType is null.
            return false;
        }

    }

    public static void logout() {
        if (isLogin()) {
            ParseUser.logOut();
            LayerImpl.getLayerClient().deauthenticate();

            try {
                ParseObject.unpinAll();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static String isValidationError(Context context, String account, String password) {
        boolean validationError = false;
        StringBuilder mValidationErrorMessage;

        mValidationErrorMessage = new StringBuilder(context.getResources().getString(
                R.string.error_intro));

        if (account.isEmpty()) {
            validationError = true;
            mValidationErrorMessage.append(context.getResources().getString(
                    R.string.error_blank_username));
        }

        if (password.isEmpty()) {
            if (validationError) {
                mValidationErrorMessage.append(context.getResources().getString(
                        R.string.error_join));
            }
            validationError = true;
            mValidationErrorMessage.append(context.getResources().getString(
                    R.string.error_blank_password));
        }

        mValidationErrorMessage.append(context.getResources().getString(
                R.string.error_end));


        if (validationError) {
            return mValidationErrorMessage.toString();
        } else {
            return "";
        }
    }

    public static boolean isAccountOK(Context context, String account, String password, String passwordAgain) {
        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(context.getResources()
                .getString(R.string.error_intro));

        if (account.isEmpty()) {
            validationError = true;
            validationErrorMessage.append(context.getResources().getString(
                    R.string.error_blank_username));
        }

        if (password.isEmpty()) {
            if (validationError) {
                validationErrorMessage.append(context.getResources().getString(
                        R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(context.getResources().getString(
                    R.string.error_blank_password));
        }

        if (!AccountChecker.isMatching(password, passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(context.getResources().getString(
                        R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(context.getResources().getString(
                    R.string.error_mismatched_passwords));
        }

        validationErrorMessage.append(context.getResources().getString(
                R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            DisplayUtils.makeToast(context, validationErrorMessage.toString());
            return false;
        } else {
            return true;
        }
    }

}
