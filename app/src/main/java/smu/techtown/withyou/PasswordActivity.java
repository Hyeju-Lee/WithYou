package smu.techtown.withyou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends Activity {
    TextView textView;
    EditText passwordEditText;
    Button setBtn;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //title bar 없애기
        setContentView(R.layout.activity_password);

        textView = (TextView)findViewById(R.id.textView);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        setBtn = (Button)findViewById(R.id.setBtn);

        Intent intent = getIntent();
        String isExist = intent.getStringExtra("pw");

        if(isExist.equals("not exist")){
            i = 1;
            setBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(i == 1){
                        String str = passwordEditText.getText().toString();
                        PreferenceManager.setString(getApplicationContext(),"candidate password",str);
                        textView.setText("비밀번호 한번 더 입력");
                        passwordEditText.setText(null);
                        i++;
                    }
                    else{
                        String str1 = passwordEditText.getText().toString();
                        if(str1.equals(PreferenceManager.getString(getApplicationContext(),"candidate password"))){
                            PreferenceManager.setString(getApplicationContext(),"password",str1);
                            Toast.makeText(getApplicationContext(),"비밀번호가 설정되었습니다.",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            textView.setText("다시 입력해주세요");
                            passwordEditText.setText(null);
                        }
                    }

                }
            });
        }

        else{
            i = 0;
            textView.setText("기존 비밀번호를 입력해주세요");
            setBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(i == 0){
                        String str = passwordEditText.getText().toString();
                        if(str.equals(PreferenceManager.getString(getApplicationContext(),"password"))){
                            textView.setText("새로운 비밀번호를 입력해주세요");
                            passwordEditText.setText(null);
                            i = 1;
                        }
                        else{
                            textView.setText("다시 입력해주세요");
                            passwordEditText.setText(null);
                        }
                    }
                    else if(i == 1){
                        String str = passwordEditText.getText().toString();
                        PreferenceManager.setString(getApplicationContext(),"candidate password",str);
                        textView.setText("한번 더 입력해주세요");
                        passwordEditText.setText(null);
                        i++;
                    }
                    else{
                        String str1 = passwordEditText.getText().toString();
                        if(str1.equals(PreferenceManager.getString(getApplicationContext(),"candidate password"))){
                            PreferenceManager.setString(getApplicationContext(),"password",str1);
                            Toast.makeText(getApplicationContext(),"비밀번호가 설정되었습니다.",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            textView.setText("다시 입력해주세요");
                            passwordEditText.setText(null);
                        }
                    }
                }
            });
        }


    }

}
