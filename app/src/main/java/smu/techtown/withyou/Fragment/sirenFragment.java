package smu.techtown.withyou.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import smu.techtown.withyou.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class sirenFragment extends Fragment {


    public sirenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_siren, container, false);
    }

}
