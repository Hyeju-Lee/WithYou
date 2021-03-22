package smu.techtown.withyou.Fragment;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import smu.techtown.withyou.PreferenceManager;
import smu.techtown.withyou.R;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class SirenFragment extends Fragment {
    Button sirenStart;
    Button sirenStop;
    MediaPlayer mediaPlayer;
    String sound;

    public SirenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_siren, container, false);
        sirenStart = view.findViewById(R.id.sirenStart);
        sirenStop = view.findViewById(R.id.sirenStop);
        sound = PreferenceManager.getString(getActivity(), "sound");
        if(sound.equals(""))
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.policesiren);
        else {
            switch (sound) {
                case "police":
                    mediaPlayer = MediaPlayer.create(getActivity(), R.raw.policesiren);
                    break;
                case "fireStation":
                    mediaPlayer = MediaPlayer.create(getActivity(), R.raw.fire);
                    break;
                case "ambulance":
                    mediaPlayer = MediaPlayer.create(getActivity(), R.raw.ambulance);
                    break;
            }
        }


        sirenStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sirenStart.setBackgroundResource(R.drawable.sirenon);
                mediaPlayer.start();
            }
        });

        sirenStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sirenStart.setBackgroundResource(R.drawable.sirenoff);
                mediaPlayer.stop();
                try{
                    mediaPlayer.prepare();
                }catch (IOException io){}
            }
        });
        return view;
    }

}
