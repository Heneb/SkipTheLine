package com.bhoeft.skip_the_line;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

/**
 * @author Benedikt Höft, 15.05.16.
 */
public class FragmentSettings extends Fragment
{
    OnFavoriteSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnFavoriteSelectedListener {
        public void onCanteenSelected(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.fragment_settings_layout, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.settingsFavoriteSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.favoriteRestaurantArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onCanteenSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCallback.onCanteenSelected(0);
            }
        });

        Switch history = (Switch) view.findViewById(R.id.settingsHistorySwitch);
        history.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    MainActivity.showHistory = false;
                else
                    MainActivity.showHistory = true;
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFavoriteSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFavoriteSelectedListener");
        }
    }
}
