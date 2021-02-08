package smu.techtown.withyou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SirenOffActivity extends Activity {
    TextView textView;
    EditText passwordEditText;
    Button setBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_password);

        textView = (TextView)findViewById(R.id.textView);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        setBtn = (Button)findViewById(R.id.setBtn);

        textView.setText("사이렌을 끄려면 비밀번호를 입력해주세요");
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = PreferenceManager.getString(getApplicationContext(),"password");
                if(passwordEditText.getText().toString().equals(password)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result","success");
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                else{
                    textView.setText("틀렸습니다. 다시 입력해주세요");
                    passwordEditText.setText(null);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥 레이어 클릭해도 안 닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    @Override
    public void onBackPressed() {
        //백버튼 막기
        return;
    }
}
