package com.openshutters.justcalmdown;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SetTimeLimitFragment extends Fragment {

    private SharedPreferences prfs;

    public static Fragment newInstance() {
        return new SetTimeLimitFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_set_time, container, false);
        setup(view);
        return view;
    }

    private void setup(View view) {
        prfs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String prefTimeLimitInSeconds = prfs.getString(getString(R.string.pref_time), getString(R.string.pref_time_default_value));

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.time_radio);

        String[] choices = getResources().getStringArray(R.array.pref_time_array);
        String[] choicesKey = getResources().getStringArray(R.array.pref_time_values);

        for (int i = 0; i < choices.length; i++) {
            RadioButton radioButtonView = new RadioButton(getActivity());
            radioButtonView.setText(choices[i]);
            if (choicesKey[i].equals(prefTimeLimitInSeconds)) {
                radioGroup.check(radioButtonView.getId());
            }
            radioButtonView.setId(Integer.valueOf(choicesKey[i]));
            radioGroup.addView(radioButtonView);
        }
        radioGroup.check(Integer.valueOf(prefTimeLimitInSeconds));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updatePrefs(checkedId);
            }
        });
    }

    private void updatePrefs(int selectedTime) {
        prfs.edit().putString(getString(R.string.pref_time), String.valueOf(selectedTime)).apply();
    }
}
