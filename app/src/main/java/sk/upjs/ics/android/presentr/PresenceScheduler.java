package sk.upjs.ics.android.presentr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class PresenceScheduler {
    private static final int SERVICE_REQUEST_CODE = 0;
    private static final int NO_FLAGS = 0;

    public static void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, PresenceService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, SERVICE_REQUEST_CODE, intent, NO_FLAGS);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 7 * 1000, pendingIntent);
    }
}
