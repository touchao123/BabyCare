package tw.tasker.babysitter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.tasker.babysitter.utils.DisplayUtils;
import tw.tasker.babysitter.utils.LogUtils;
import tw.tasker.babysitter.view.SitterInputCodeFragment;

// adb shell am broadcast -a android.provider.Telephony.SMS_RECEIVED
public class SmsReceiver extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    private SitterInputCodeFragment mFragment;
    private Context mContext;

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        LogUtils.LOGD("vic", "SmsReceiver onReceiver");
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    setVerifyCode(message);

                } // end for loop
            } else { // bundle is null
                String message = "您的BabyCare驗證碼為[" + mFragment.mVerifyCodeNumber + "]";
                LogUtils.LOGD("vic", "message: " + message);
                setVerifyCode(message);
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

    private void setVerifyCode(String message) {
        //Toast toast = Toast.makeText(context, "senderNum: "+ senderNum + ", message: " + message, duration);
        DisplayUtils.makeToast(mContext, "*" + message);

        Pattern p = Pattern.compile("\\d{6}");
        Matcher m = p.matcher(message);
        if (m.find()) {
            mFragment.mVerifyCode.setText(m.group());
            mFragment.mConfirm.setVisibility(View.VISIBLE);
        }
    }

    public void setFragment(SitterInputCodeFragment verifyCodeFragment) {
        mFragment = verifyCodeFragment;
    }

}
