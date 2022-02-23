package com.example.einsatzapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Objects;


public class MapFragment extends Fragment {
    private GoogleMap mMap;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView xCoord, yCoord, missionName;
    private Button btnOpenNavigation, btnClosePopUp;
    public ArrayList<Mission> missionList;

    private ChipGroup chipGroup;
    FusedLocationProviderClient locationClient;
    SupportMapFragment mapFragment;
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(48.345903, 14.1613589);
    private static final float DEFAULT_ZOOM = 15.0f;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;


    private Location lastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Retrive location and camer position from saved instance state
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        //Initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize the Location Provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //Initalize map fragment
        mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        //region mission declaration
        Mission mission1 = new Mission("Mission1", 48.34536, 14.16017);
        Mission mission2 = new Mission("Mission2", 48.35485, 14.16251);
        missionList = new ArrayList<Mission>();
        missionList.add(mission1);
        missionList.add(mission2);
        //endregion



        //Async map
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15.0f));
                mMap.setTrafficEnabled(true);

                for (Mission mission : missionList) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mission.X, mission.Y))
                            .title(mission.Name)
                            .snippet(String.valueOf(mission.X))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(@NonNull Marker marker) {
                            createNewDialog(marker);
                        }
                    });
                }
                getLocationPermission();
                getDeviceLocation();
                updateLocationUI();



            }


        });
        //ChipGroup to switch hybrid and normal map type
        //use view.findViewById to find views in this view, otherwise it wont work
        chipGroup = view.findViewById(R.id.chip_group);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if(group==null){return;}
                if(checkedId==R.id.chip_normal){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });





        return view;
    }


    public void createNewDialog(Marker marker) {
        dialogBuilder = new AlertDialog.Builder(requireActivity());
        final View missionPopUpView = getLayoutInflater().inflate(R.layout.popup, null);
        xCoord = (TextView) missionPopUpView.findViewById(R.id.popup_X);
        yCoord = (TextView) missionPopUpView.findViewById(R.id.popup_Y);
        missionName = (TextView) missionPopUpView.findViewById(R.id.popup_name);

        btnOpenNavigation = (Button) missionPopUpView.findViewById(R.id.popup_NavButton);
        btnClosePopUp = (Button) missionPopUpView.findViewById(R.id.popup_close);
        missionName.setText(marker.getTitle());
        LatLng markerPosition = marker.getPosition();
        xCoord.setText(String.valueOf(markerPosition.latitude));
        yCoord.setText(String.valueOf(markerPosition.longitude));
        dialogBuilder.setView(missionPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnOpenNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigation(markerPosition);
            }
        });
        btnClosePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void startNavigation(LatLng markerPosition) {
        String navigationString = markerPosition.latitude + "," + markerPosition.longitude;
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + navigationString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void getLocationPermission(){
        if(ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted=true;
        }else{
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted=false;
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION){
            if(grantResults.length>0
                && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                locationPermissionGranted=true;
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();


    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation(){
        try{
            if(locationPermissionGranted){
                Task<Location> locationResult= fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            lastKnownLocation = task.getResult();
                            if(lastKnownLocation!=null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                            else{

                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



}