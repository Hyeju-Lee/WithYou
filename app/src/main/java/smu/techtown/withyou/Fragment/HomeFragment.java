package smu.techtown.withyou.Fragment;


import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import smu.techtown.withyou.JSONData;
import smu.techtown.withyou.MainActivity;
import smu.techtown.withyou.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View view;
    MapView mapView;
    ViewGroup mapViewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mapView = new MapView(getActivity());
        mapViewContainer = (ViewGroup) view.findViewById(R.id.map);
        mapViewContainer.addView(mapView);

        Marker marker = new Marker();
        marker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;

    }

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
                    Double latitude = Double.parseDouble(safeAreaLocation.get(i).getLatitude());
                    Double longitude = Double.parseDouble(safeAreaLocation.get(i).getLongitude());
                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude,longitude);
                    MapPOIItem marker = new MapPOIItem();
                    marker.setItemName(safeAreaLocation.get(i).getStorNm());
                    marker.setMapPoint(mapPoint);
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                    mapView.addPOIItem(marker);
                }

            }
            return null;
        }

        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

        }

        @Override
        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

        }

        @Override
        public void onCurrentLocationUpdateFailed(MapView mapView) {

        }

        @Override
        public void onCurrentLocationUpdateCancelled(MapView mapView) {

        }

        @Override
        public void onMapViewInitialized(MapView mapView) {

        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        }
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

}
