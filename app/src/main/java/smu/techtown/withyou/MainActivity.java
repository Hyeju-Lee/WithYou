package smu.techtown.withyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import smu.techtown.withyou.Fragment.HomeFragment;
import smu.techtown.withyou.Fragment.NaviFragment;
import smu.techtown.withyou.Fragment.SettingFragment;
import smu.techtown.withyou.Fragment.SirenFragment;
import smu.techtown.withyou.Fragment.TaxiFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final long FINISH_INTERVAL_TIME = 2000;//for 뒤로 가기 버튼 이벤트
    private static final int SHAKE_THRESHOLD = 1000;
    public static final int REQUEST_CODE = 1;
    private long backPressedTime = 0;

    public static BottomNavigationView bottomNavigationView;
    Menu menu;
    Fragment homeFragment;
    Fragment sirenFragment;
    Fragment naviFragment;
    Fragment settingFragment;
    Fragment taxiFragment;


    private long lastTime, currentTime, gabOfTime;
    private float speed;
    private float lastX, lastY, lastZ;
    private float x, y, z;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    MediaPlayer mediaPlayer;
    SmsManager smsManager;

    long[] timeArray;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkDangerousPermissions();
        //아래 탭 보여주기
        bottomNavigationView = findViewById(R.id.navigationView);
        menu=bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(doOnNavigationItemSelectedListener);

        homeFragment = new HomeFragment();
        taxiFragment = new TaxiFragment();
        sirenFragment = new SirenFragment();
        naviFragment = new NaviFragment();
        settingFragment = new SettingFragment();
        //첫 화면은 home으로 보이도록
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,homeFragment).commit();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        timeArray = new long[10];
        for(int j = 0; j < 10; j++)
            timeArray[j] = 0;
    }

    //탭 선택에 따라 fragment들을 바꿔가며 보여준다
    private BottomNavigationView.OnNavigationItemSelectedListener doOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,homeFragment).commit();
                    return true;
                case R.id.navigation:
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,naviFragment).commit();
                    return true;
                case R.id.taxi:
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,taxiFragment).commit();
                    return true;
                case R.id.siren:
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,sirenFragment).commit();
                    return true;
                case R.id.setting:
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,settingFragment).commit();
                    return true;
                    
            }
            return false;
        }
    };
    //뒤로 가기 두번 클릭 시 종료
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if(0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed();
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(),"한번 더 누르면 종료",Toast.LENGTH_SHORT).show();
        }
    }
    // 휴대폰 흔들면 event 발생
    @Override
    protected void onStart() {
        super.onStart();
        if (accelerometerSensor != null)
            sensorManager.registerListener(this, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    } //반드시 override 필요

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && PreferenceManager.getBoolean(this,"shake")) {
            currentTime = System.currentTimeMillis();
            gabOfTime = (currentTime - lastTime);
            if(gabOfTime > 100) { //0.1sec
                lastTime = currentTime;
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ)
                        / gabOfTime * 10000;
                if(speed > SHAKE_THRESHOLD) { //흔들림 감지되는 경우
                    timeArray[i] = currentTime;
                    if(i != 9){
                        long countGab = timeArray[i] - timeArray[i+1];
                        if(countGab > 0 && countGab < 5000) {
                            for(int j = 0; j < 10; j++)
                                timeArray[j] = 0; //mediaplayer 중복 재생 방지
                            if(isPasswordExist()) {
                                String sound = PreferenceManager.getString(this, "sound");
                                if(sound.equals(""))
                                    mediaPlayer = MediaPlayer.create(this, R.raw.policesiren);
                                else {
                                    switch (sound) {
                                        case "police":
                                            mediaPlayer = MediaPlayer.create(this,R.raw.policesiren);
                                            break;
                                        case "fireStation":
                                            mediaPlayer = MediaPlayer.create(this,R.raw.fire);
                                            break;
                                        case "ambulance":
                                            mediaPlayer = MediaPlayer.create(this,R.raw.ambulance);
                                            break;
                                    }
                                }
                                mediaPlayer.start();
                                showPasswordPopup();
                                if(sendLocation())
                                    Toast.makeText(this,"현재 위치가 전송되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        long countGab = timeArray[9] - timeArray[0];
                        if(0 <  countGab && countGab <5000) {
                            for(int j = 0; j < 10; j++)
                                timeArray[j] = 0; //mediaplayer 중복 재생 방지
                            if(isPasswordExist()) {
                                String sound = PreferenceManager.getString(this, "sound");
                                if(sound.equals(""))
                                    mediaPlayer = MediaPlayer.create(this, R.raw.policesiren);
                                else {
                                    switch (sound) {
                                        case "police":
                                            mediaPlayer = MediaPlayer.create(this,R.raw.policesiren);
                                            break;
                                        case "fireStation":
                                            mediaPlayer = MediaPlayer.create(this,R.raw.fire);
                                            break;
                                        case "ambulance":
                                            mediaPlayer = MediaPlayer.create(this,R.raw.ambulance);
                                            break;
                                    }
                                }
                                mediaPlayer.start();
                                showPasswordPopup();
                                if(sendLocation())
                                    Toast.makeText(this,"현재 위치가 전송되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if(i>=0 && i<9){
                        i++;
                    }
                    else
                        i = 0;
                }

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];
            }
        }
    }

    private boolean isPasswordExist() {
        String password = PreferenceManager.getString(this,"password");
        Log.i("비밀",password);
        if(password.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("비밀 번호 설정 필요").setMessage("setting 메뉴에서 비밀번호를 입력해주세요");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        else {
            return true;
        }
    }

    public void showPasswordPopup() {
        Intent intent = new Intent(this, SirenOffActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }

    private boolean sendLocation(){
        try{
            String phoneNumber = PreferenceManager.getString(this, "phone number");
            smsManager = SmsManager.getDefault();
            String address = PreferenceManager.getString(this,"address");
            smsManager.sendTextMessage(phoneNumber, null, address,
                    null,null);
            return true;
        }catch (IllegalArgumentException e){
            Toast.makeText(this,"긴급 번호를 설정해주세요",Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void checkDangerousPermissions(){
        String[] permissions = {//접근 권한 확인
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_MMS
        };
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for(int i=0; i<permissions.length;i++){
            permissionCheck = ContextCompat.checkSelfPermission(this,permissions[i]);
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                break;
            }
        }

        if(permissionCheck==PackageManager.PERMISSION_GRANTED){

        }
        else{
            Toast.makeText(this,"권한 없음",Toast.LENGTH_LONG).show();
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[0])){
                Toast.makeText(this,"권한 설명 필요함",Toast.LENGTH_LONG).show();
            }else {
                ActivityCompat.requestPermissions(this,permissions,1);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode,String[] permissions,
                                           int[] grantResults){
        if(requestCode == 1){
            for(int i=0;i<permissions.length;i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                   // Toast.makeText(this,permissions[i]+"권한이 승인됨",
                            //Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,permissions[i]+"권한이 승인되지 않음",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
