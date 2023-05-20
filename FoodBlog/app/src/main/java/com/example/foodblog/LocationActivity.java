package com.example.foodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.foodblog.adapter_recycler_view.UserLocationAdapter;
import com.example.foodblog.api_instance.UserApiInstance;
import com.example.foodblog.model.ResponseAllUserLocation;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseUserLocation;
import com.example.foodblog.model.User;
import com.example.foodblog.model.UserLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationActivity extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
    private Location myLocation;

    private ArrayList<UserLocation> userLocations;
    private ImageButton btnBack;
    private SwitchCompat switchShareMyLocation;
    private SwipeRefreshLayout swfUserLocation;
    private RecyclerView rvOtherUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        setControl();
        setEvent();
        setData();
    }

    private void setControl() {
        btnBack = findViewById(R.id.btnBack);
        switchShareMyLocation = findViewById(R.id.switchShareMyLocation);
        swfUserLocation = findViewById(R.id.swfUserLocation);

        rvOtherUserLocation = findViewById(R.id.rvOtherUserLocation);
        rvOtherUserLocation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void setEvent() {
        btnBack.setOnClickListener(view -> finish());
        switchShareMyLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    shareMyLocation();
                }
                else {
                    stopShareMyLocation();
                }
            }
        });
        swfUserLocation.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setAllSharedUserLocation();
            }
        });
    }

    private void setData(){
        // set my share location status
        setMyShareLocationStatus();
        // set rv all user shared location
        setAllSharedUserLocation();
    }

    private void setMyShareLocationStatus() {
        Call<ResponseUserLocation> call = UserApiInstance.getInstance().getMyLocation(MyAuthorization.getInstance().getToken());
        call.enqueue(new Callback<ResponseUserLocation>() {
            @Override
            public void onResponse(Call<ResponseUserLocation> call, Response<ResponseUserLocation> response) {
                if (response.isSuccessful()){
                    UserLocation myLocation = response.body().getData();
                    if (myLocation.getCurrentLocation() != null && !myLocation.getCurrentLocation().equals("")){
                        switchShareMyLocation.setChecked(true);
                    }
                }
                else {
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        Toast.makeText(LocationActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseUserLocation> call, Throwable t) {
                Toast.makeText(LocationActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void setAllSharedUserLocation() {
        Call<ResponseAllUserLocation> call = UserApiInstance.getInstance().getAllUserLocations(MyAuthorization.getInstance().getToken());
        call.enqueue(new Callback<ResponseAllUserLocation>() {
            @Override
            public void onResponse(Call<ResponseAllUserLocation> call, Response<ResponseAllUserLocation> response) {
                if (response.isSuccessful()){
                    userLocations = new ArrayList<>();
                    UserLocationAdapter userLocationAdapter = new UserLocationAdapter(LocationActivity.this, userLocations);
                    rvOtherUserLocation.setAdapter(userLocationAdapter);
                    for (UserLocation userLocation: response.body().getData()){
                        if (userLocation.getCurrentLocation() != null && !userLocation.getCurrentLocation().equals("")){
                            userLocations.add(userLocation);
                            userLocationAdapter.notifyItemInserted(userLocations.size()-1);
                        }
                    }
                }
                else {
                    Toast.makeText(LocationActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                swfUserLocation.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseAllUserLocation> call, Throwable t) {
                Toast.makeText(LocationActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                swfUserLocation.setRefreshing(false);
            }
        });
    }

    private void shareMyLocation() {
        try {
            if (MyPermission.checkFineLocationPermission(this)){
                Toast.makeText(this, R.string.noti_sharing_location, Toast.LENGTH_SHORT).show();
                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this);
            }
            else {
                switchShareMyLocation.setChecked(false);
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopShareMyLocation() {
        // stop tracking location
        locationManager.removeUpdates(this);
        // delete current location in db
        Call<ResponseObject> call = UserApiInstance.getInstance().deleteMyLocation(MyAuthorization.getInstance().getToken());
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful()){
                    Toast.makeText(LocationActivity.this, R.string.success_stop_share_my_location, Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        Toast.makeText(LocationActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(LocationActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        myLocation = location;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            // share location here
            Log.e("coordinate", ""+location.getLatitude()+","+location.getLongitude());
            Log.e("my location", address);
            UserLocation myCurrentLocation = new UserLocation(address, location.getLatitude(), location.getLongitude());
            updateMyLocation(myCurrentLocation);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateMyLocation(UserLocation myCurrentLocation) {
        Call<ResponseObject> call = UserApiInstance.getInstance().updateMyCurrentLocation(MyAuthorization.getInstance().getToken(), myCurrentLocation);
        call.enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful()){
                    Toast.makeText(LocationActivity.this, R.string.success_share_my_location, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LocationActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(LocationActivity.this, R.string.error_500, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void openGoogleMap(UserLocation userLocation){
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + userLocation.getLatitude() + "," + userLocation.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}