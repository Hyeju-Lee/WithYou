package smu.techtown.withyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import smu.techtown.withyou.Fragment.HomeFragment;
import smu.techtown.withyou.Fragment.NaviFragment;
import smu.techtown.withyou.Fragment.SettingFragment;
import smu.techtown.withyou.Fragment.SirenFragment;
import smu.techtown.withyou.Fragment.TaxiFragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final long FINISH_INTERVAL_TIME = 2000;//for 뒤로 가기 버튼 이벤트
    private static final int SHAKE_THRESHOLD = 1000;
    private long backPressedTime = 0;

    BottomNavigationView bottomNavigationView;
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

    int shakeCount = 0;
    int count = shakeCount;


    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //아래 탭 보여주기
        bottomNavigationView = findViewById(R.id.navigationView);
        menu=bottomNavigationView.getMenu();
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(doOnNavigationItemSelectedListener);
        //Fragment들 선언
        homeFragment = new HomeFragment();
        taxiFragment = new TaxiFragment();
        sirenFragment = new SirenFragment();
        naviFragment = new NaviFragment();
        settingFragment = new SettingFragment();
        //첫 화면은 home으로 보이도록
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,homeFragment).commit();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
                    shakeCount = count++;
                    Toast.makeText(getApplicationContext(),"와아아아",Toast.LENGTH_SHORT).show();
                }

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];
            }
        }
    }
}
