package smu.techtown.withyou.Fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import smu.techtown.withyou.PreferenceManager;
import smu.techtown.withyou.R;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.mainFrame,new MyPreferenceFragment())
                .commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        public static final String editTextKey = "phoneNumber";

        public static EditTextPreference phoneNumEditText;
        private PreferenceScreen screen;
        String phoneNumber;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            screen = getPreferenceScreen();
            phoneNumEditText = (EditTextPreference)screen.findPreference(editTextKey);
            phoneNumber = phoneNumEditText.getText();
            PreferenceManager.setString(getActivity(),"phone number",phoneNumber);
            phoneNumEditText.setOnPreferenceChangeListener(this);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String value = (String)newValue;
            if(preference == phoneNumEditText)
                phoneNumEditText.setSummary(value);

            return true;
        }

        public void onResume(){
            super.onResume();
            updateSummary();
        }

        public void updateSummary(){
            phoneNumEditText.setSummary(phoneNumEditText.getText());
        }
    }


}
