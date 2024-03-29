package smu.techtown.withyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
    CameraSurfaceView cameraSurfaceView;
    FrameLayout previewFrame;
    SmsManager smsManager;
    List<Address> addresses;
    String phoneNumber;
    Button sendBtn;

    Uri outUri;
    String fileName;
    String longFileName;
    Bitmap resultBitmap;

    Uri audioUri;
    public static int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
        messageTextView = (TextView)findViewById(R.id.messageTextView);
        phoneNumber = PreferenceManager.getString(this,"phone number");

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setText("녹음중입니다");
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
            addresses = geocoder.getFromLocation(
                    currentLatitude,currentLongitude,1);
            Log.i("here",addresses.get(0).getAddressLine(0));
            if(sendLocation()) {
                Toast.makeText(this,"현재 위치가 전송되었습니다.",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("here","실패");
        }

        handler = new Handler();
        Recording recording = new Recording();
        recording.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        previewFrame = findViewById(R.id.previewFrame);
        cameraSurfaceView = new CameraSurfaceView(this);
        previewFrame.addView(cameraSurfaceView);

        Button captureBtn = findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra("address",phoneNumber);
                        intent.putExtra(Intent.EXTRA_STREAM,outUri);
                        intent.setType("image/*");
                        startActivity(intent);
                    }
                },1000);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendBtn.getText().equals("녹음 전송하기")) {
                    sendFile();
                }
            }
        });

    }

    public class Recording extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            File file = Environment.getExternalStorageDirectory();
            fileName = file.getAbsolutePath() + "/record" + num + ".mp3";
            startRecording(fileName);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecording();
                    messageTextView.setText("녹음이 완료되었습니다.");
                    sendBtn.setText("녹음 전송하기");
                }
            },10000);

            longFileName = file.getAbsolutePath() + "/long" + num + ".mp3";
            startRecording(longFileName);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mediaRecorder != null) {
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                    }
                }
            }, 50000);

            return null;
        }
    }

    private void startRecording(String fileName) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(fileName);
        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            ContentValues values = new ContentValues(5);
            values.put(MediaStore.MediaColumns.TITLE, "Recorded");
            values.put(MediaStore.Audio.Media.DISPLAY_NAME,"Recorded Audio");
            values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis()/1000);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4"); //미디어 파일의 포멧
            values.put(MediaStore.Audio.Media.DATA, fileName);
            audioUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,values);
            if(audioUri == null)
                Log.i("야야","실패");
            num += 1;
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,audioUri));

        }
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

    public void takePicture() {
        cameraSurfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90); //갤러리 사진 회전
                    resultBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight()
                    ,matrix,true);
                    String outUriStr = MediaStore.Images.Media.insertImage(
                            getContentResolver(),resultBitmap,"image","jpeg");
                    if(outUriStr == null) {
                        Log.i("captureiss", "image insert failed");
                        return;
                    } else {
                        outUri = Uri.parse(outUriStr);
                        sendBroadcast(new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outUri
                        ));
                    }
                    camera.startPreview();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    private boolean sendLocation(){
        try{
            smsManager = SmsManager.getDefault();
            String address = addresses.get(0).getAddressLine(0);
            PreferenceManager.setString(this,"address",address);
            smsManager.sendTextMessage(phoneNumber, null, address,
                    null,null);
            return true;
        }catch (IllegalArgumentException e){
            Toast.makeText(this,"긴급 번호를 설정해주세요",Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void sendFile() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("address",phoneNumber);
        intent.putExtra(Intent.EXTRA_STREAM,audioUri);
        intent.setType("audio/*");
        startActivity(intent);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥 레이어 클릭해도 안 닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

}
