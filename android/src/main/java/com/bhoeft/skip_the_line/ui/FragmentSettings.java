package com.bhoeft.skip_the_line.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.bhoeft.skip_the_line.R;
import com.bhoeft.skip_the_line.ui.util.ISettingsChangedCallback;

/**
 * Fragment für die Ansicht der Einstellungen für die favorisierte Mensa und die Anzeige der Durchschnittsdaten im Graph
 *
 * @author Benedikt Höft, 15.05.16.
 */
public class FragmentSettings extends Fragment
{
  private ISettingsChangedCallback callback;

  public FragmentSettings(ISettingsChangedCallback pCallback)
  {
    callback = pCallback;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_settings_layout, container, false);

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                                                                         R.array.favoriteRestaurantArray, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    Spinner spinner = (Spinner) view.findViewById(R.id.settingsFavoriteSpinner);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
    {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
      {
        callback.canteenChanged(position + 1);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent)
      {
      }
    });

    Switch history = (Switch) view.findViewById(R.id.settingsHistorySwitch);
    history.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        callback.historySettingsChanged(isChecked);
      }
    });

    return view;
  }
}
