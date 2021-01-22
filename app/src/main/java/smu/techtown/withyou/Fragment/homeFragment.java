package smu.techtown.withyou.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import smu.techtown.withyou.MainActivity;
import smu.techtown.withyou.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapView;


/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends Fragment {
    View view;
    public homeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        //카카오맵 배경으로 띄우기
        MapView mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.map);
        mapViewContainer.addView(mapView);

       return view;

    }

}
