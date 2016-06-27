package com.bhoeft.skip_the_line.services;

import android.content.*;
import com.bhoeft.skip_the_line.R;
import com.bhoeft.skip_the_line.util.ReminderNotification;

/**
 * Erzeugt eine Benachrichtigung für ein den Fall, dass die angegebene Zeit erreicht wurde
 *
 * @author Benedikt Höft on 22.06.16.
 */
public class NotificationPublisher extends BroadcastReceiver
{
  public void onReceive(Context context, Intent intent)
  {
    ReminderNotification.createNotification(context, context.getResources().getString(R.string.notificationBodyTime));
  }
}
