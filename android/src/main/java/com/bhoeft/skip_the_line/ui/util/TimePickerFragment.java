package com.bhoeft.skip_the_line.ui.util;

import android.app.*;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

/**
 * Fragment für die Auswahl des Benachrichtigungszeitraums
 *
 * @author Benedikt Höft on 23.06.16.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
  private ITimePickerCallback callback;

  public TimePickerFragment(ITimePickerCallback pCallback)
  {
    callback = pCallback;
  }

  /**
   * Initialisiert den Dialog mit der aktuellen Uhrzeit
   *
   * @param savedInstanceState
   * @return
   */
  @NotNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    final Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);
    return new TimePickerDialog(getActivity(), this, hour, minute,
                                DateFormat.is24HourFormat(getActivity()));
  }

  public void onTimeSet(TimePicker view, int hourOfDay, int minute)
  {
    callback.timePicked(hourOfDay, minute);
  }

  public interface ITimePickerCallback
  {
    void timePicked(int pHourOfDay, int pMinute);
  }
}
