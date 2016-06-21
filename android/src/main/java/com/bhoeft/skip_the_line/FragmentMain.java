package com.bhoeft.skip_the_line;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by HB on 15.05.16.
 */
public class FragmentMain extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_layout, container, false);

        final GraphView graph = (GraphView) view.findViewById(R.id.queueGraph);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graph.getViewport().setMinX(11);
        graph.getViewport().setMaxX(14);
        graph.getViewport().setMinY(0);
        graph.getViewport().setXAxisBoundsManual(true);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(11, 30),
                new DataPoint(11.5, 40),
                new DataPoint(12, 60)
        });
        series.setColor(getResources().getColor(R.color.graphActual));
        graph.addSeries(series);

        Spinner spinner = (Spinner) view.findViewById(R.id.statusReminderNumberSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.reminderNumberArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinner2 = (Spinner) view.findViewById(R.id.statusReminderTimeSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.reminderTitleArray, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        Spinner spinner3 = (Spinner) view.findViewById(R.id.dateSpinner);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(),
                R.array.pickerDates, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);
        spinner3.setSelection(1);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0 && MainActivity.showHistory)
                {
                    graph.removeAllSeries();
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                            new DataPoint(11, 60),
                            new DataPoint(11.5, 60),
                            new DataPoint(12, 80),
                            new DataPoint(13, 30),
                            new DataPoint(14, 20)
                    });
                    series.setColor(getResources().getColor(R.color.graphHistory));
                    graph.addSeries(series);

                    if(view != null)
                        view.invalidate();
                }
                else if(i == 1)
                {
                    graph.removeAllSeries();
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                            new DataPoint(11, 30),
                            new DataPoint(11.5, 40),
                            new DataPoint(12, 60)
                    });
                    series.setColor(getResources().getColor(R.color.graphActual));
                    graph.addSeries(series);

                    if(view != null)
                        view.invalidate();
                }
                else
                {
                    graph.removeAllSeries();
                    if(view != null)
                        view.invalidate();
                }

            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");

        TextView lastRefresh = (TextView) view.findViewById(R.id.statusRefreshTimeTextView);
        lastRefresh.setText(date.format(currentLocalTime));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
