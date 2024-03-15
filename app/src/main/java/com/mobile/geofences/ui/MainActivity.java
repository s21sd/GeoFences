package com.mobile.geofences.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.mobile.geofences.R;
import com.mobile.geofences.service.ImplicitService;
import com.mobile.geofences.storage.RoamPreferences;
import com.roam.sdk.Roam;
import com.roam.sdk.builder.RoamPublish;
import com.roam.sdk.callback.PublishCallback;
import com.roam.sdk.callback.SubscribeCallback;
import com.roam.sdk.models.RoamError;
import com.roam.sdk.trips_v2.RoamTrip;
import com.roam.sdk.trips_v2.callback.RoamTripCallback;
import com.roam.sdk.trips_v2.models.Error;
import com.roam.sdk.trips_v2.models.RoamTripResponse;
import com.roam.sdk.trips_v2.request.RoamTripStops;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener  {
    private ProgressBar progressBar;
    private RadioGroup mRadioGroup;
    private EditText edtAccuracyFilter, edtTime, edtDist;
    private TextView snackBar;
    private CheckBox ckOffline, ckFilter, ckMock, ckToggleEvents, ckToggleLocation, ckNetwork, ckRooted, ckSource, ckMotion;
    private Button btnStartTracking, btnStopTracking, btnCustomAccuracyEnable, btnToggleSecurity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Roam.disableBatteryOptimization();

        progressBar = findViewById(R.id.progressbar);
        mRadioGroup = findViewById(R.id.radioGroup);
        btnStartTracking = findViewById(R.id.btnStartTracking);
        btnStopTracking = findViewById(R.id.btnStopTracking);
        btnCustomAccuracyEnable = findViewById(R.id.btnCustomAccuracyEnable);
        edtAccuracyFilter = findViewById(R.id.edtAccuracyFilter);
        edtTime = findViewById(R.id.edtTime);
        edtDist = findViewById(R.id.edtDist);
        snackBar = findViewById(R.id.snackBar);
        ckOffline = findViewById(R.id.ckOffline);
        Button btnCreateTrip = findViewById(R.id.btnCreateTrip);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnTrip = findViewById(R.id.btnTrip);

        ckMock = findViewById(R.id.ckMock);
        ckFilter = findViewById(R.id.ckFilter);
        ckToggleEvents = findViewById(R.id.ckToggleEvents);
        ckToggleLocation = findViewById(R.id.ckToggleLocation);
        btnStartTracking.setOnClickListener(this);
        btnStopTracking.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnTrip.setOnClickListener(this);
        btnCreateTrip.setOnClickListener(this);
        btnCustomAccuracyEnable.setOnClickListener(this);
        ckMock.setOnCheckedChangeListener(this);
        ckFilter.setOnCheckedChangeListener(this);
        ckToggleEvents.setOnCheckedChangeListener(this);
        ckToggleLocation.setOnCheckedChangeListener(this);

        btnToggleSecurity = findViewById(R.id.btnToggleSecurity);
        ckMotion = findViewById(R.id.ckVerifyMotion);
        ckNetwork = findViewById(R.id.ckNetwork);
        ckRooted = findViewById(R.id.ckRooted);
        ckSource = findViewById(R.id.ckVerifySource);

        btnToggleSecurity.setOnClickListener(this);

        checkPermissions();
    }

    private void checkPermissions() {
        if (!Roam.checkLocationServices()) {
            Roam.requestLocationServices(this);
        } else if (!Roam.checkLocationPermission()) {
            Roam.requestLocationPermission(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Roam.checkBackgroundLocationPermission()) {
            Roam.requestBackgroundLocationPermission(this);
        } else if(!Roam.checkPhoneStatePermission()){
            Roam.requestPhoneStatePermission(this);
        } else if(!Roam.checkActivityPermission()){
            Roam.requestActivityPermission(this);
        }else {
//            callBottomDialoge();
        }
        
        //for firebase campaign notification
//        String deviceToken = getSharedPreferences("_", MODE_PRIVATE).getString("fcm_token", "empty");
//        Log.d("DeviceToken", deviceToken);
//        Roam.setDeviceToken(deviceToken);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartTracking:
                checkPermissions();
                break;
            case R.id.btnStopTracking:
                stopTracking();
                break;
            case R.id.btnCreateTrip:
                createTrip();
                break;
//            case R.id.btnTrip:
//                startActivity(new Intent(this, TripActivity.class).putExtra("OFFLINE", ckOffline.isChecked()));
//                break;
//            case R.id.btnLogout:
//                logout();
//                break;
            case R.id.btnCustomAccuracyEnable:
                if (!TextUtils.isEmpty(edtAccuracyFilter.getText().toString())) {
                    Roam.enableAccuracyEngine(Integer.parseInt(edtAccuracyFilter.getText().toString()));
                } else {
                    showMsg("Enter accuracy");
                }
                break;
            case R.id.btnToggleSecurity:
//                toggleSecurity();
                break;
        }
    }

    private void showMsg(String msg) {
        Snackbar.make(snackBar, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void trackingStatus() {
        if (Roam.isLocationTracking()) {
            startService(new Intent(this, ImplicitService.class));
            btnStartTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_disable));
            btnStopTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_enable));
            btnStartTracking.setEnabled(false);
            btnStopTracking.setEnabled(true);
        } else {
            stopService(new Intent(this, ImplicitService.class));
            btnStartTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_enable));
            btnStopTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_disable));
            btnStartTracking.setEnabled(true);
            btnStopTracking.setEnabled(false);
        }
    }


    private void stopTracking() {
//        Roam.stopTracking();
        trackingStatus();
    }

    private void createTrip() {
        show();

        //metadata
        JSONObject metadata = new JSONObject();
        try {
            metadata.put("KEY", "VALUE");
        } catch (Exception e) {
        }

        List<Double> geometry = new ArrayList<>();
        geometry.add(85.30614739); //lon
        geometry.add(23.5155215); //lat

        //stop point
        RoamTripStops stop = new RoamTripStops();
        stop.setMetadata(metadata);
        stop.setStopDescription("STOP-DESCRIPTION");
        stop.setStopName("STOP-NAME");
        stop.setAddress("STOP-ADDRESS");
        stop.setGeometryRadius(600.0);
        stop.setGeometry(geometry);
        List<RoamTripStops> stops = new ArrayList<>();
        stops.add(stop);

        //builder
        RoamTrip trip = new RoamTrip.Builder()
                .setUserId(RoamPreferences.getUserId(this, "userId"))
                .setTripDescription("TRIP-DESCRIPTION")
                .setTripName("TRIP-NAME")
                .setIsLocal(ckOffline.isChecked())
                .setStop(stops)
                .setMetadata(metadata)
                .build();

        Roam.createTrip(trip, new RoamTripCallback() {
            @Override
            public void onSuccess(RoamTripResponse roamTripResponse) {
                hide();
            }

            @Override
            public void onError(Error error) {
                hide();
            }
        });
    }

    private void show() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hide() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {

            case R.id.ckMock:
                if (isChecked) {
                    Roam.allowMockLocation(true);
                } else {
                    Roam.allowMockLocation(false);
                }
                break;

            case R.id.ckFilter:
                if (isChecked) {
                    Roam.enableAccuracyEngine();
                } else {
                    Roam.disableAccuracyEngine();
                }
                break;

            case R.id.ckToggleLocation:
                if (isChecked) {
                    // TODO: Step 6 : Subscribe to your userId to listen location updated from LocationReceiver.java

                    Roam.subscribe(Roam.Subscribe.LOCATION,  RoamPreferences.getUserId(MainActivity.this, "userId"), new SubscribeCallback() {
                        @Override
                        public void onSuccess(String s, String s1) {
                            Log.d("subsMessage","subs"+s+"S1"+s1);
                            //do something
                        }

                        @Override
                        public void onError(RoamError roamError) {

                        }

                    });
                } else {
                    Roam.unSubscribe(Roam.Subscribe.LOCATION,  RoamPreferences.getUserId(MainActivity.this, "userId"), new SubscribeCallback() {
                        @Override
                        public void onSuccess(String s, String s1) {
                            Log.d("subsMessage","subs"+s+"S1"+s1);

                        }
                        @Override
                        public void onError(RoamError roamError) {

                        }

                    });

                }
                break;

            case R.id.ckToggleEvents:
                if (isChecked) {
                    // TODO: Step 7 : Publish and save location in Roam Backend.
                    RoamPublish locationData = new RoamPublish.Builder().build();
                    Roam.publishAndSave(locationData, new PublishCallback() {
                        @Override
                        public void onSuccess(String s) {
                            //do something
                        }
                        @Override
                        public void onError(RoamError roamError) {
                            //do something
                        }
                    });
                } else {

                    Roam.unSubscribe(Roam.Subscribe.LOCATION,  RoamPreferences.getUserId(MainActivity.this, "userId"), new SubscribeCallback() {
                        @Override
                        public void onSuccess(String s, String s1) {
                            Log.d("subsMessage","subs"+s+"S1"+s1);

                        }
                        @Override
                        public void onError(RoamError roamError) {

                        }

                    });


                }
                break;
        }
    }
}