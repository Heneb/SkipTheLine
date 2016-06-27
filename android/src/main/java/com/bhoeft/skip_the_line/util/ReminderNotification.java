package com.bhoeft.skip_the_line.util;

import android.app.*;
import android.content.Context;
import android.media.RingtoneManager;
import com.bhoeft.skip_the_line.R;

/**
 * Klasse für die Erzeugung einer Benachrichtigung
 *
 * @author Benedikt Höft on 24.06.16.
 */
public final class ReminderNotification
{
  private static final long[] VIBRATE = new long[]{0, 500, 1000, 500, 1000};

  /**
   * Sendet eine neue Benachrichtigung
   *
   * @param pContext          Kontext
   * @param pNotificationBody Body-Nachricht für die Benachrichtigung
   */
  public static void createNotification(Context pContext, String pNotificationBody)
  {
    NotificationManager notificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification = _getNotification(pContext, pContext.getResources().getString(R.string.notificationTitle), pNotificationBody);
    notification.vibrate = VIBRATE;
    notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    notificationManager.notify(0, notification);
  }

  /**
   * Initialisiert eine Benachrichtigung
   *
   * @param pContext Kontext
   * @param pTitle   Titel der Benachrichtigung
   * @param pText    Text der Benachrichtigung
   * @return erzeugte Benachrichtigung
   */
  private static Notification _getNotification(Context pContext, String pTitle, String pText)
  {
    Notification.Builder builder = new Notification.Builder(pContext);
    builder.setContentTitle(pTitle);
    builder.setContentText(pText);
    builder.setSmallIcon(R.drawable.icon);
    return builder.build();
  }
}
