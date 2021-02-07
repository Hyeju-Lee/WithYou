package smu.techtown.withyou.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import smu.techtown.withyou.PasswordActivity;
import smu.techtown.withyou.PreferenceManager;
import smu.techtown.withyou.R;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


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
        public static final String preferenceKey = "password";

        public static EditTextPreference phoneNumEditText;
        Preference passwordPreference;
        private PreferenceScreen screen;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            screen = getPreferenceScreen();
            phoneNumEditText = (EditTextPreference)screen.findPreference(editTextKey);
            phoneNumEditText.setOnPreferenceChangeListener(this);
            passwordPreference = screen.findPreference(preferenceKey);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String value = (String)newValue;
            if(preference == phoneNumEditText) {
                PreferenceManager.setString(getActivity(), "phone number", value);
                phoneNumEditText.setSummary(value);
            }
            return true;
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(preference == passwordPreference){
                Intent intent = new Intent(getActivity(), PasswordActivity.class);
                if(PreferenceManager.getString(getActivity(),"password").equals("")){ //설정된 비밀번호 없을때
                    intent.putExtra("pw","not exist");
                }
                else {
                    intent.putExtra("pw","exist");
                }
                startActivity(intent);
                return true;
            }
            return false;
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
