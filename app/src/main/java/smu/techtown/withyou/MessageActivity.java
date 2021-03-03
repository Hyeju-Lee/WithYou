package smu.techtown.withyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MessageActivity extends Activity {
    Geocoder geocoder;
    LocationManager locationManager;
    Location location;
    double currentLatitude;
    double currentLongitude;
    MediaRecorder mediaRecorder;
    Handler handler;
    TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
        messageTextView = (TextView)findViewById(R.id.messageTextView);

        Button closeButton = (Button)findViewById(R.id.closeBtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getCurrentLocation();
        geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    currentLatitude,currentLongitude,1);
            Log.i("here",addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("here","실패");
        }

        File file = Environment.getExternalStorageDirectory();
        String fileName = file.getAbsolutePath()+"/"+"record.mp3";
        startRecording(fileName);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecording();
                messageTextView.setText("녹음이 완료되었습니다.\n긴급 문자가 전송되었습니다.");
            }
        },5000);

    }

    public void getCurrentLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    1);
        }
        else{
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1,
                    locationListener);
        }
    }

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void startRecording(String fileName) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//압축 형식 설정
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(fileName);
        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥 레이어 클릭해도 안 닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

}
