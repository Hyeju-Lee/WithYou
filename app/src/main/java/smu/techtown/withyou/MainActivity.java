package smu.techtown.withyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import smu.techtown.withyou.Fragment.HomeFragment;
import smu.techtown.withyou.Fragment.NaviFragment;
import smu.techtown.withyou.Fragment.SettingFragment;
import smu.techtown.withyou.Fragment.SirenFragment;
import smu.techtown.withyou.Fragment.TaxiFragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Menu menu;
    Fragment homeFragment;
    Fragment sirenFragment;
    Fragment naviFragment;
    Fragment settingFragment;
    Fragment taxiFragment;

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



}
