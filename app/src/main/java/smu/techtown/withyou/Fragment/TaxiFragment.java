package smu.techtown.withyou.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import smu.techtown.withyou.PreferenceManager;
import smu.techtown.withyou.R;

import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaxiFragment extends Fragment {
    Spinner taxiSpinner;
    Spinner hourSpinner;
    Spinner minSpinner;
    Button TaxiBtn;
    EditText firstTaxiEditText;
    EditText lastTaxiEditText;
    Button cameraBtn;
    ImageView imageView;

    SmsManager smsManager;
    String taxiMessage;
    String phoneNumber;
    File file;

    int onOff= 1;

    public TaxiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_taxi, container, false);
        //spinner 구성
        taxiSpinner = (Spinner)view.findViewById(R.id.taxiSpinner);
        ArrayAdapter taxiAdapter = ArrayAdapter.createFromResource(
                getActivity(),R.array.taxi_num,R.layout.support_simple_spinner_dropdown_item);
        taxiAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        taxiSpinner.setAdapter(taxiAdapter);

        hourSpinner = (Spinner)view.findViewById(R.id.hourSpinner);
        ArrayAdapter hourAdapter = ArrayAdapter.createFromResource(
                getActivity(),R.array.hour,R.layout.support_simple_spinner_dropdown_item
        );
        hourAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);

        minSpinner = (Spinner)view.findViewById(R.id.minSpinner);
        ArrayAdapter minAdapter = ArrayAdapter.createFromResource(
                getActivity(),R.array.minute,R.layout.support_simple_spinner_dropdown_item
        );
        minAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        minSpinner.setAdapter(minAdapter);

        firstTaxiEditText = (EditText)view.findViewById(R.id.firstTaxiNum);
        lastTaxiEditText = (EditText)view.findViewById(R.id.lastTaxiNum);

        imageView = view.findViewById(R.id.imageView);
        File sdcard = Environment.getExternalStorageDirectory();
        file = new File(sdcard, "taxi.jpg");
        cameraBtn = view.findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = FileProvider.getUriForFile(getContext(),
                        "smu.techtown.withyou.fileprovider",file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 101);
            }
        });

        TaxiBtn = (Button)view.findViewById(R.id.TaxiBtn);
        TaxiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (onOff){
                    case 0: //하차버튼 클릭 시
                        makeTaxiMessage(onOff);
                        sendTaxiMessage();
                        TaxiBtn.setText("승   차");
                        firstTaxiEditText.setText(null);
                        lastTaxiEditText.setText(null);
                        taxiSpinner.setSelection(0);
                        hourSpinner.setSelection(0);
                        minSpinner.setSelection(0);
                        onOff = 1;
                        break;
                    case 1: //승차버튼 클릭시
                        makeTaxiMessage(onOff);
                        if(sendTaxiMessage()){
                            TaxiBtn.setText("하   차");
                            onOff = 0;
                        }
                        else
                            onOff = 1;
                        break;
                }
            }
        });



        return view;
    }

    public void makeTaxiMessage(int onoff){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 HH시 mm분");
        String getTime = dateFormat.format(date);

        String takeOnOff;
        String firstTaxiNum = firstTaxiEditText.getText().toString();
        String lastTaxiNum = lastTaxiEditText.getText().toString();
        String taxiSpinnerString = taxiSpinner.getSelectedItem().toString();

        String estimatedHour = hourSpinner.getSelectedItem().toString();
        String estimatedMin = minSpinner.getSelectedItem().toString();

        if(onoff == 1){
            takeOnOff = "예상 시간 : " + estimatedHour + "시간 " + estimatedMin + "분" + "\n" + "승차하였습니다.";
        }
        else{
            takeOnOff = "하차하였습니다.";
        }

        taxiMessage = getTime + "\n"
                + "택시 번호 : " + firstTaxiNum + taxiSpinnerString + " " + lastTaxiNum + "\n"
                + takeOnOff;

    }

    public boolean sendTaxiMessage(){
        try{
            phoneNumber = PreferenceManager.getString(getActivity(),"phone number");
            smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, taxiMessage,
                    null,null);
            Toast.makeText(getActivity(),"메시지 전송 성공",Toast.LENGTH_SHORT).show();
            return true;
        }catch (IllegalArgumentException e){
            Toast.makeText(getActivity(),"긴급 번호를 설정해주세요",Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            imageView.setImageBitmap(bitmap);
        }
    }
}
