package com.bhoeft.skip_the_line.services;

import android.content.Context;
import com.bhoeft.skip_the_line.R;
import com.bhoeft.skip_the_line.communication.ServerInterface;
import com.bhoeft.skip_the_line.util.ReminderNotification;

import java.util.concurrent.*;

/**
 * Schickt in bestimmten Zeitabständen an den Server eine Anfrage, ob die angegebene Anzahl der Wartenden unterschritten wurde
 *
 * @author Benedikt Höft on 24.06.16.
 */
public class AmountPoller
{
  private final static int POLLERTIME = 30;

  private Context context;
  private int waitForAmount;
  private Runnable callback;
  private ScheduledExecutorService service;
  private ScheduledFuture scheduledFuture;

  public AmountPoller(Context pContext, int pWaitForAmount, Runnable pCallback)
  {
    context = pContext;
    waitForAmount = pWaitForAmount;
    callback = pCallback;
    service = Executors.newScheduledThreadPool(5);
  }

  public void start()
  {
    scheduledFuture = service.schedule(new Callable()
                                       {
                                         @Override
                                         public Object call() throws Exception
                                         {
                                           if (ServerInterface.getCurrentAmount() <= waitForAmount)
                                           {
                                             callback.run();
                                             ReminderNotification.createNotification(context, context.getResources().getString(R.string.notifcationBodyAmount));
                                           }

                                           return null;
                                         }
                                       },
                                       POLLERTIME,
                                       TimeUnit.SECONDS);
  }

  public void stop()
  {
    scheduledFuture.cancel(true);
  }
}
