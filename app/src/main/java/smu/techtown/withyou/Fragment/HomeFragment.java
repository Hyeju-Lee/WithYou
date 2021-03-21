package smu.techtown.withyou.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import smu.techtown.withyou.JSONData;
import smu.techtown.withyou.MessageActivity;
import smu.techtown.withyou.PreferenceManager;
import smu.techtown.withyou.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.opencsv.CSVReader;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View view;
    MapView mapView;
    ViewGroup mapViewContainer;
    Button messageButton;
    Double safeAreaLatitude;
    Double safeAreaLongitude;

    LocationManager locationManager;
    Location location;
    Double currentLatitude;
    Double currentLongitude;
    float distance;
    float minDistance = 10000;
    Double destinationLatitude;
    Double destinationLongitude;
    Double policeLatitude;
    Double policeLongitude;
    String policeName;
    List <Address> addresses;
    Geocoder geocoder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mapView = new MapView(getActivity());
        mapViewContainer = (ViewGroup) view.findViewById(R.id.map);
        mapViewContainer.addView(mapView);

        getCurrentLocation();
        geocoder = new Geocoder(getActivity());
        try {
            addresses = geocoder.getFromLocation(
                    currentLatitude,currentLongitude,1);
            PreferenceManager.setString(getActivity(),"address",addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("here",addresses.get(0).getAddressLine(0));
        Marker marker = new Marker();
        marker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        final String phoneNumber = PreferenceManager.getString(getActivity(),"phone number");

        messageButton = (Button)view.findViewById(R.id.messageButton);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("긴급 번호 설정 필요").setMessage("setting 메뉴에서 번호를 입력해주세요");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    Intent intent = new Intent(getActivity(), MessageActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    public void getCurrentLocation() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
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

    public class Marker extends AsyncTask<Void,Void,Void> implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {

        @Override
        protected Void doInBackground(Void... voids) {
            mapView.setMapViewEventListener(this);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            ArrayList<JSONData> safeAreaLocation = getDataList();

            for(int i = 0; i < safeAreaLocation.size(); i++){
                if(safeAreaLocation.get(i).getLatitude().equals("") || safeAreaLocation.get(i).getLongitude().equals("")) {
                    continue;
                }
                else {
                    safeAreaLatitude = Double.parseDouble(safeAreaLocation.get(i).getLatitude());
                    safeAreaLongitude = Double.parseDouble(safeAreaLocation.get(i).getLongitude());
                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(safeAreaLatitude,safeAreaLongitude);
                    MapPOIItem marker = new MapPOIItem();
                    marker.setItemName(safeAreaLocation.get(i).getStorNm());
                    marker.setMapPoint(mapPoint);
                    marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                    mapView.addPOIItem(marker);

                    Location startPoint = new Location("start");
                    startPoint.setLatitude(currentLatitude);
                    startPoint.setLongitude(currentLongitude);

                    Location destination = new Location("destination");
                    destination.setLatitude(safeAreaLatitude);
                    destination.setLongitude(safeAreaLongitude);

                    distance = startPoint.distanceTo(destination);
                    if(distance < minDistance) {
                        minDistance = distance;
                        destinationLatitude = safeAreaLatitude;
                        destinationLongitude = safeAreaLongitude;
                    }
                }
            }

            try{
                InputStreamReader inputStreamReader = new InputStreamReader(getResources().openRawResource(R.raw.policelocation), "euc-kr");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                CSVReader csvReader = new CSVReader(bufferedReader);
                String[] nextLine;
                csvReader.readNext();
                while ((nextLine = csvReader.readNext()) != null) {
                    for(int i = 0; i < nextLine.length;i++) {
                        policeLongitude = Double.parseDouble(nextLine[3]);
                        policeLatitude = Double.parseDouble(nextLine[4]);
                        policeName = nextLine[2];
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(policeLatitude,policeLongitude);
                        MapPOIItem policeMarker = new MapPOIItem();
                        policeMarker.setItemName(policeName);
                        policeMarker.setMapPoint(mapPoint);
                        policeMarker.setMarkerType(MapPOIItem.MarkerType.RedPin);
                        policeMarker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin);
                        mapView.addPOIItem(policeMarker);
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            PreferenceManager.setString(getActivity(), "destination latitude", Double.toString(destinationLatitude));
            PreferenceManager.setString(getActivity(), "destination longitude", Double.toString(destinationLongitude));

            return null;
        }

        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float v) {}

        @Override
        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {}

        @Override
        public void onCurrentLocationUpdateFailed(MapView mapView) {}

        @Override
        public void onCurrentLocationUpdateCancelled(MapView mapView) {}

        @Override
        public void onMapViewInitialized(MapView mapView) {}

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {}

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {}

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {}

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {}

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {}

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {}

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {}

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {}
    }

    private ArrayList<JSONData> getDataList(){
        ArrayList<JSONData> dataList = new ArrayList<>();
        Gson gson = new Gson();
        try {
            String data = new Task().execute().get();
            JSONObject jsonObject = new JSONObject(data);
            JSONObject response = (JSONObject)jsonObject.get("response");
            JSONObject body = (JSONObject)response.get("body");
            JSONArray jsonArray = body.getJSONArray("items");

            for(int i=0; i<jsonArray.length();i++){
                JSONData jsonData = gson.fromJson(jsonArray.get(i).toString(),JSONData.class);
                dataList.add(jsonData);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public class Task extends AsyncTask<String, Void, String> {
        String receiveMsg,str;

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                String ctpNm = URLEncoder.encode("ctprvnNm","UTF-8") + "=" + URLEncoder.encode("서울특별시", "UTF-8") + "&";
                String guNm = "&" + URLEncoder.encode("signguNm","UTF-8") + "=" + URLEncoder.encode("구로구", "UTF-8");
                url = new URL("http://api.data.go.kr/openapi/tn_pubr_public_female_safety_prtchouse_api?serviceKey=lyu9hd5DtmdtX5XlO5D065odvYhzyhWEf6WfjHeyhvRdY5qDpZtkeUWgaWA3iqTEcAS8drjpovBIEeiu83D%2FeA%3D%3D&"+
                        ctpNm + guNm + "&pageNo=1&numOfRows=36&type=json");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                if(httpURLConnection.getResponseCode() == httpURLConnection.HTTP_OK) {
                    InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null){
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    receiveMsg = receiveMsg.replace(",}","}");
                    reader.close();
                }
                else{
                    Log.i("통신",httpURLConnection.getResponseCode()+"에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
}
