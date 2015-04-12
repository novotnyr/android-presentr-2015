package sk.upjs.ics.android.presentr;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

public class PresenceService extends IntentService {
    public static final String WORKER_THREAD_NAME = "PresenceService";
    public static final int USER_LIST_NOTIFICATION_ID = 0;
    public static final String PRESENCE_INTENT_ACTION = PresenceService.class.getPackage().getName() + ".Presence";
    public static final String PRESENCE_INTENT_EXTRAS = "userList";

    private PresenceDao presenceDao = new PresenceDao();

    public PresenceService() {
        super(WORKER_THREAD_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(getClass().getName(), "Presence service created");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<String> users = presenceDao.loadUsers();
        Log.i(getClass().getName(), "Downloaded user list: " + users.size() + " are present");
        triggerNotification(users);
        broadcastPresence(users);
    }

    @Override
    public void onDestroy() {
        Log.i(getClass().getName(), "Presence service destroyed");
        super.onDestroy();
    }


    private void broadcastPresence(List<String> users) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(PRESENCE_INTENT_ACTION);
        intent.putExtra(PRESENCE_INTENT_EXTRAS, (Serializable) users);

        broadcastManager.sendBroadcast(intent);
    }


    private void triggerNotification(List<String> users) {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Presentr")
                .setContentText("Počet ľudí v miestnosti: " + users.size())
                .setContentIntent(getEmptyNotificationContentIntent())
                .setTicker("Presentr")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .getNotification();

        NotificationManager notificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(USER_LIST_NOTIFICATION_ID, notification);
    }

    public PendingIntent getEmptyNotificationContentIntent() {
        int REQUEST_CODE = 0;
        int NO_FLAGS = 0;

        PendingIntent contentIntent = PendingIntent.getActivity(this, REQUEST_CODE, new Intent(), NO_FLAGS);
        return contentIntent;
    }
}
