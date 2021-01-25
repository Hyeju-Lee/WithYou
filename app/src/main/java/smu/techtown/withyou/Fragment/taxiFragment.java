package smu.techtown.withyou.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import smu.techtown.withyou.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class taxiFragment extends Fragment {
    Spinner taxiSpinner;
    Spinner hourSpinner;
    Spinner minSpinner;
    Button TaxiBtn;
    EditText firstTaxiNum;
    EditText lastTaxiNum;

    int onOff= 1;
    public taxiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        firstTaxiNum = (EditText)view.findViewById(R.id.firstTaxiNum);
        lastTaxiNum = (EditText)view.findViewById(R.id.lastTaxiNum);

        //승차 버튼
        TaxiBtn = (Button)view.findViewById(R.id.TaxiBtn);
        TaxiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (onOff){
                    case 0:
                        TaxiBtn.setText("승   차");
                        firstTaxiNum.setText(null);
                        lastTaxiNum.setText(null);
                        taxiSpinner.setSelection(0);
                        hourSpinner.setSelection(0);
                        minSpinner.setSelection(0);
                        onOff = 1;
                        break;
                    case 1:
                        TaxiBtn.setText("하   차");
                        onOff = 0;
                        break;
                }
            }
        });



        return view;
    }

}
