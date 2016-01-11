package tw.tasker.babysitter;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import tw.tasker.babysitter.model.HomeEvent;
import tw.tasker.babysitter.model.UploadImage;


public class UploadService extends IntentService {
    public static String NAMESPACE = "cc.babycare";
    private static final String SERVICE_NAME = UploadService.class.getName();

    private static final int UPLOAD_NOTIFICATION_ID = 1234;
    private static final int UPLOAD_NOTIFICATION_ID_DONE = 1235; // Something unique

    private static int PERCENT_TOTAL = 100;
    private static final String ACTION_UPLOAD_SUFFIX = ".uploadservice.action.upload";
    private static final String BROADCAST_ACTION_SUFFIX = ".uploadservice.broadcast.status";
    public static final String PARAM_PATHS = "paths";

    private NotificationManager notificationManager;
    private Builder notification;
    private PowerManager.WakeLock wakeLock;
    private UploadNotificationConfig notificationConfig;
    private long lastProgressNotificationTime;

    /**
     * The minimum interval between progress reports in milliseconds.
     * If the upload Tasks report more frequently, we will throttle notifications.
     * We aim for 6 updates per second.
     */
    protected static final long PROGRESS_REPORT_INTERVAL = 166;

    public UploadService() {
        super(SERVICE_NAME);
    }

    public static String getActionUpload() {
        return NAMESPACE + ACTION_UPLOAD_SUFFIX;
    }

    public static String getActionBroadcast() {
        return NAMESPACE + BROADCAST_ACTION_SUFFIX;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(this);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, SERVICE_NAME);
    }

    @DebugLog
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (getActionUpload().equals(action)) {
                ArrayList<String> paths = intent.getStringArrayListExtra(PARAM_PATHS);
                String type = intent.getStringExtra("type");
                PERCENT_TOTAL = paths.size();
                notificationConfig = new UploadNotificationConfig(
                        R.drawable.ic_launcher,
                        getString(R.string.app_name),
                        getString(R.string.uploading),
                        getString(R.string.upload_success),
                        getString(R.string.upload_error),
                        false);

//                String type = intent.getStringExtra(PARAM_TYPE);
//                if (UPLOAD_MULTIPART.equals(type)) {
//                    currentTask = new MultipartUploadTask(this, intent);
//                } else if (UPLOAD_BINARY.equals(type)) {
//                    currentTask = new BinaryUploadTask(this, intent);
//                } else {
//                    return;
//                }

                lastProgressNotificationTime = 0;
                wakeLock.acquire();

                createNotification();


                startUpload(paths, type);


//                currentTask.run();
            }
        }
    }

    private void startUpload(ArrayList<String> paths, String type) {
        int count = 1;
        for(String path : paths) {
            final String uploadId = UUID.randomUUID().toString();

            try {

                Uri uri = Uri.fromFile(new File(path));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] image = stream.toByteArray();


                String fileName = ParseUser.getCurrentUser().getUsername();
                final ParseFile parseFile = new ParseFile(fileName + ".jpg", image);

                try {
                    parseFile.save();
                    saveToParse(uploadId, parseFile, type, count);

                } catch (ParseException parseException) {
                    broadcastError(uploadId, parseException);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            count++;

        }
        broadcastCompleted("");
    }

    @DebugLog
    private void saveToParse(final String uploadId, ParseFile parseFile, String type, int count) {
        UploadImage uploadImage = new UploadImage();

        uploadImage.setImageFile(parseFile);
        uploadImage.setUser(ParseUser.getCurrentUser());
        uploadImage.setType(type);

        try {
            uploadImage.save();
            broadcastProgress(uploadId, count);
        } catch (ParseException parseException) {
            broadcastError(uploadId, parseException);
        }
    }

    private void createNotification() {
        notification.setContentTitle(notificationConfig.getTitle())
                .setContentText(notificationConfig.getMessage())
                .setContentIntent(notificationConfig.getPendingIntent(this))
                .setSmallIcon(notificationConfig.getIconResourceID())
                .setProgress(100, 0, true).setOngoing(true);

        startForeground(UPLOAD_NOTIFICATION_ID, notification.build());
    }

    @DebugLog
    void broadcastProgress(final String uploadId, final Integer percentDone) {

        long currentTime = System.currentTimeMillis();
        if (currentTime < lastProgressNotificationTime + PROGRESS_REPORT_INTERVAL) {
            return;
        }

        lastProgressNotificationTime = currentTime;

        updateNotificationProgress(percentDone);

//        final Intent intent = new Intent(getActionBroadcast());
//        intent.putExtra(UPLOAD_ID, uploadId);
//        intent.putExtra(STATUS, STATUS_IN_PROGRESS);

//        final int percentsProgress = (int) (uploadedBytes * 100 / totalBytes);
//        intent.putExtra(PROGRESS, percentsProgress);

//        intent.putExtra(PROGRESS_UPLOADED_BYTES, uploadedBytes);
//        intent.putExtra(PROGRESS_TOTAL_BYTES, totalBytes);
//        sendBroadcast(intent);
    }

    @DebugLog
    void broadcastCompleted(final String uploadId) {

//        final String filteredMessage;
//        if (responseMessage == null) {
//            filteredMessage = "";
//        } else {
//            filteredMessage = responseMessage;
//        }
//
//        if (responseCode >= 200 && responseCode <= 299)
            updateNotificationCompleted();
//        else
//            updateNotificationError();

//        final Intent intent = new Intent(getActionBroadcast());
//        intent.putExtra(UPLOAD_ID, uploadId);
//        intent.putExtra(STATUS, STATUS_COMPLETED);
//        intent.putExtra(SERVER_RESPONSE_CODE, responseCode);
//        intent.putExtra(SERVER_RESPONSE_MESSAGE, filteredMessage);
//        sendBroadcast(intent);
        wakeLock.release();
    }

    @DebugLog
    void broadcastError(final String uploadId, final Exception exception) {

        updateNotificationError();

//        final Intent intent = new Intent(getActionBroadcast());
//        intent.setAction(getActionBroadcast());
//        intent.putExtra(UPLOAD_ID, uploadId);
//        intent.putExtra(STATUS, STATUS_ERROR);
//        intent.putExtra(ERROR_EXCEPTION, exception);
//        sendBroadcast(intent);
        wakeLock.release();
    }


    private void updateNotificationProgress(int percentDone) {

        notification.setContentTitle(notificationConfig.getTitle())
                .setContentText(notificationConfig.getMessage() + " ("+percentDone+"/"+PERCENT_TOTAL+")")
                .setContentIntent(notificationConfig.getPendingIntent(this))
                .setSmallIcon(notificationConfig.getIconResourceID())
                .setProgress(PERCENT_TOTAL, percentDone, false)
                .setOngoing(true);

        startForeground(UPLOAD_NOTIFICATION_ID, notification.build());
    }

    private void updateNotificationCompleted() {
        stopForeground(notificationConfig.isAutoClearOnSuccess());

        if (!notificationConfig.isAutoClearOnSuccess()) {
            notification.setContentTitle(notificationConfig.getTitle())
                    .setContentText(notificationConfig.getCompleted())
                    .setContentIntent(notificationConfig.getPendingIntent(this))
                    .setSmallIcon(notificationConfig.getIconResourceID())
                    .setProgress(0, 0, false)
                    .setOngoing(false);
            setRingtone();
            notificationManager.notify(UPLOAD_NOTIFICATION_ID_DONE, notification.build());

            EventBus.getDefault().post(new HomeEvent(HomeEvent.ACTION_UPLOAD_IMAGE_DONE));

        }
    }

    private void setRingtone() {

        if(notificationConfig.isRingTone()) {
            notification.setSound(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
            notification.setOnlyAlertOnce(false);
        }

    }

    private void updateNotificationError() {
        stopForeground(false);

        notification.setContentTitle(notificationConfig.getTitle())
                .setContentText(notificationConfig.getError())
                .setContentIntent(notificationConfig.getPendingIntent(this))
                .setSmallIcon(notificationConfig.getIconResourceID())
                .setProgress(0, 0, false).setOngoing(false);
        setRingtone();
        notificationManager.notify(UPLOAD_NOTIFICATION_ID_DONE, notification.build());
    }
}
