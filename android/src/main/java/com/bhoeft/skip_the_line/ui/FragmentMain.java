package com.bhoeft.skip_the_line.ui;

import android.app.*;
import android.content.*;
import android.graphics.Typeface;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.bhoeft.skip_the_line.R;
import com.bhoeft.skip_the_line.communication.ServerInterface;
import com.bhoeft.skip_the_line.services.*;
import com.bhoeft.skip_the_line.ui.util.TimePickerFragment;
import com.bhoeft.skip_the_line.util.ServerException;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.*;
import util.Config;

import java.text.*;
import java.util.*;

/**
 * Fragment für die Hauptansicht der App. Zeigt den Graph der ausgewählten Warteschlange und die Benachrichtigungseinstellungen
 *
 * @author Benedikt Höft on 15.05.16.
 */
public class FragmentMain extends Fragment
{
  private static final int GRAPHMINY = 0;

  private View view;
  private GraphView graph;
  private Spinner dateSpinner;

  private Button timeButton;
  private Switch reminderSwitch;

  private int currentWeekDay;
  private long timePickerTime;

  private boolean showHistory = false;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    view = inflater.inflate(R.layout.fragment_main_layout, container, false);
    _initViews();

    currentWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

    _initGraphSettings();
    _initSpinnerNumber();
    _initSpinnerDate();
    _setLastRefresh();

    return view;
  }

  /**
   * Initialisiert UI-Komponenten
   */
  private void _initViews()
  {
    graph = (GraphView) view.findViewById(R.id.queueGraph);
    reminderSwitch = (Switch) view.findViewById(R.id.statusReminderSwitch);
    reminderSwitch.setOnClickListener(new _ReminderSwitchListener());
    Button refreshButton = (Button) view.findViewById(R.id.statusRefreshButton);
    refreshButton.setOnClickListener(new _RefreshButtonListener());
    timeButton = (Button) view.findViewById(R.id.statusReminderTimeButton);
    timeButton.setOnClickListener(new _TimeButtonListener());
  }

  /**
   * Zeichnet einen neuen Graphen, falls Daten in der Liste(n) vorhanden sind
   *
   * @param pStatsEntriesHistory Liste der durchschnittlichen Länge der Warteschlange eines vergangenen Tages
   * @param pStatsEntriesCurrent Liste der Länge der Warteschlange des aktuellen Tages
   */
  public void drawNewGraph(@Nullable List<Integer> pStatsEntriesHistory, @Nullable List<Integer> pStatsEntriesCurrent)
  {
    graph.removeAllSeries();
    _setLastRefresh();

    if (pStatsEntriesHistory != null)
      graph.addSeries(_getSeries(pStatsEntriesHistory, getResources().getColor(R.color.graphHistory)));
    if (pStatsEntriesCurrent != null)
      graph.addSeries(_getSeries(pStatsEntriesCurrent, getResources().getColor(R.color.graphCurrent)));
  }

  /**
   * Erzeugt eine Serie von Datenpunkten aus einer gegebenen Liste und setzt eine gegebene Farbe
   *
   * @param pStatsEntries Liste der Warteschlangendaten
   * @param pColor        Farbe in Abhängigkeit der übergebenen Daten
   * @return Serie von Datenpunkten
   */
  private LineGraphSeries<DataPoint> _getSeries(List<Integer> pStatsEntries, int pColor)
  {
    List<DataPoint> dataPoints = new ArrayList<>();
    double start = Config.getPropertyDouble("hawla_start_time");

    for (Integer statsEntry : pStatsEntries)
    {
      dataPoints.add(new DataPoint(start, statsEntry));
      start += Config.getPropertyDouble("hawla_step");
    }

    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
    series.setColor(pColor);

    return series;
  }

  /**
   * Initialisiert den Graph mit gegebenen Einstellungen
   */
  private void _initGraphSettings()
  {
    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
    staticLabelsFormatter.setHorizontalLabels(Config.getPropertyStringArray("hawla_mensa_time"));
    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    String[] mensaTime = Config.getPropertyStringArray("hawla_mensa_time");
    graph.getViewport().setMinX(Double.parseDouble(mensaTime[0].split(":")[0]));
    graph.getViewport().setMaxX(Double.parseDouble(mensaTime[mensaTime.length - 1].split(":")[0]));
    graph.getViewport().setMinY(GRAPHMINY);
    graph.getViewport().setXAxisBoundsManual(true);
  }

  /**
   * Initialisiert die Auswahl des Tages für die Darstellung im Graph
   */
  private void _initSpinnerDate()
  {
    dateSpinner = (Spinner) view.findViewById(R.id.dateSpinner);
    dateSpinner.setAdapter(new _WeekDayAdapter(getContext()));
    dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
    {
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
      {
        new _FetchGraphData(position).execute();
      }

      @Override
      public void onNothingSelected(AdapterView<?> pAdapterView)
      {
      }
    });
    dateSpinner.setSelection(currentWeekDay);
  }

  /**
   * Initialisiert die Auswahl der gewünschten Länge der Warteschlange
   */
  private void _initSpinnerNumber()
  {
    Spinner spinner = (Spinner) view.findViewById(R.id.statusReminderNumberSpinner);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                                                                         R.array.reminderNumberArray, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
  }

  /**
   * Setzt die Zeit der letzten Aktuallisierung der Daten
   */
  private void _setLastRefresh()
  {
    Calendar cal = Calendar.getInstance();
    Date currentLocalTime = cal.getTime();
    DateFormat date = new SimpleDateFormat("HH:mm");

    TextView lastRefresh = (TextView) view.findViewById(R.id.statusRefreshTimeTextView);
    lastRefresh.setText(date.format(currentLocalTime));
  }

  /**
   * Legt fest, ob die Durschnitts-Daten gezeichnet werden sollen
   * Zeichnet den Graphen neu
   *
   * @param pShowHistory Boolean, ob alte Daten zusätzlich angezeigt werden sollen
   */
  public void setShowHistory(boolean pShowHistory)
  {
    showHistory = pShowHistory;
    new _FetchGraphData(dateSpinner.getSelectedItemPosition()).execute();
  }

  @Override
  public void onResume()
  {
    super.onResume();
  }

  /**
   * Zeichnet einen Graphen und zeigt in der Zwischenzeit einen Ladebalken an
   */
  private class _FetchGraphData extends AsyncTask<Void, Void, Void>
  {
    private int weekDay;

    public _FetchGraphData(int pWeekDay)
    {
      weekDay = pWeekDay;
    }

    @Override
    protected Void doInBackground(Void... pVoids)
    {
      final List<Integer> statsEntriesCurrent;
      final List<Integer> statsEntriesHistory;
      try
      {
        statsEntriesHistory = showHistory ? ServerInterface.getHistoryGraphData(weekDay) : null;
        statsEntriesCurrent = (weekDay == currentWeekDay) ? ServerInterface.getCurrentGraphData() : null;
      }
      catch (ServerException pE)
      {
        getActivity().runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            Toast.makeText(getContext(), getContext().getString(R.string.error_fetch_history), Toast.LENGTH_SHORT).show();
          }
        });
        return null;
      }

      getActivity().runOnUiThread(new Runnable()
      {
        @Override
        public void run()
        {
          drawNewGraph(statsEntriesHistory, statsEntriesCurrent);
        }
      });

      return null;
    }

    @Override
    protected void onPreExecute()
    {
      super.onPreExecute();
      getActivity().findViewById(R.id.drawGraphProgress).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void pVoid)
    {
      super.onPostExecute(pVoid);
      getActivity().findViewById(R.id.drawGraphProgress).setVisibility(View.INVISIBLE);
    }
  }

  /**
   * Listener holt aktuelle Daten vom Server und lässt den dazugehörigen Graphen zeichnen
   */
  private class _RefreshButtonListener implements View.OnClickListener
  {
    @Override
    public void onClick(View pView)
    {
      dateSpinner.setSelection(currentWeekDay);
      new _FetchGraphData(currentWeekDay).execute();
    }
  }

  /**
   * Listener holt die eingestellte Zeit für die Benachrichtigung
   */
  private class _TimeButtonListener implements View.OnClickListener
  {
    @Override
    public void onClick(View pView)
    {
      TimePickerFragment.ITimePickerCallback callback = new TimePickerFragment.ITimePickerCallback()
      {
        @Override
        public void timePicked(int pHourOfDay, int pMinute)
        {
          Calendar calendar = Calendar.getInstance();
          calendar.setTimeInMillis(System.currentTimeMillis());
          calendar.set(Calendar.HOUR_OF_DAY, pHourOfDay);
          calendar.set(Calendar.MINUTE, pMinute);
          timePickerTime = calendar.getTimeInMillis();
          timeButton.setText(String.format("%02d:%02d", pHourOfDay, pMinute));
        }
      };

      DialogFragment dialogFragment = new TimePickerFragment(callback);
      dialogFragment.show(getFragmentManager(), "TimePicker");
    }
  }

  /**
   * Adapter für Wochentagspinner, der den aktuellen Tag hervorhebt
   */
  private class _WeekDayAdapter extends ArrayAdapter<String>
  {

    public _WeekDayAdapter(Context pContext)
    {
      super(pContext, android.R.layout.simple_spinner_dropdown_item);
      addAll(getResources().getStringArray(R.array.pickerDates));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
      return _checkView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
      return _checkView(position, convertView, parent);
    }

    private View _checkView(int position, View convertView, ViewGroup parent)
    {
      TextView view = (TextView) super.getView(position, convertView, parent);

      if (position == currentWeekDay)
        view.setTypeface(null, Typeface.BOLD);
      else
        view.setTypeface(null, Typeface.NORMAL);

      return view;
    }
  }

  /**
   * Listener, der die Benachrichtigung (de)aktiviert und den AlarmManager und den AmountPoller verwaltet
   */
  private class _ReminderSwitchListener implements View.OnClickListener
  {
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private AmountPoller amountPoller;

    @Override
    public void onClick(View pView)
    {
      if (reminderSwitch.isChecked())
      {
        Runnable callback = new Runnable()
        {
          @Override
          public void run()
          {
            getActivity().runOnUiThread(new Runnable()
            {
              @Override
              public void run()
              {
                reminderSwitch.setChecked(false);
              }
            });
            alarmManager.cancel(pendingIntent);
          }
        };

        Spinner numberSpinner = (Spinner) getActivity().findViewById(R.id.statusReminderNumberSpinner);
        numberSpinner.setEnabled(false);
        Button timeButton = (Button) getActivity().findViewById(R.id.statusReminderTimeButton);
        timeButton.setEnabled(false);
        int waitForAmount = Integer.parseInt(numberSpinner.getSelectedItem().toString());
        Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);

        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timePickerTime, pendingIntent);

        amountPoller = new AmountPoller(getContext(), waitForAmount, callback);
        amountPoller.start();
      }
      else
      {
        Spinner numberSpinner = (Spinner) getActivity().findViewById(R.id.statusReminderNumberSpinner);
        numberSpinner.setEnabled(true);
        Button timeButton = (Button) getActivity().findViewById(R.id.statusReminderTimeButton);
        timeButton.setEnabled(true);

        if (amountPoller != null)
          amountPoller.stop();

        if (alarmManager != null && pendingIntent != null)
          alarmManager.cancel(pendingIntent);
      }
    }

  }
}
