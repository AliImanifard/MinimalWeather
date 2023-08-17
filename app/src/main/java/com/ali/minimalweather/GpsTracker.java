package com.ali.minimalweather;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class GpsTracker extends Service implements LocationListener {

    private static final int TIME_BETWEEN_LOCATION_UPDATES = 1000 * 60 * 10; // 10 minutes
    private static final int MIN_DISTANCE_FOR_LOCATION_UPDATE = 200;    //200 meters
    private final Context context;
    private Location location;
    private double latitude;
    private double longitude;
    private boolean canGetLocation = false;


    public GpsTracker(Context context) {
        this.context = context;
        getLocation();
    }


    public Location getLocation() throws SecurityException {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            // no provider enabled
            canGetLocation = false;
        }
        else {
            canGetLocation = true;
            // first , get location using network provider
            if (!hasPermissions()) {
                return null;
            }
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        TIME_BETWEEN_LOCATION_UPDATES,
                        MIN_DISTANCE_FOR_LOCATION_UPDATE,
                        this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }

            if (isGpsEnabled && location == null) {
                assert locationManager != null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        TIME_BETWEEN_LOCATION_UPDATES, MIN_DISTANCE_FOR_LOCATION_UPDATE, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        }


        return location;

    }


    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    public boolean hasPermissions() {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }


    public void showGpsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("GPS")
                .setMessage("GPS is not enabled. Do you want to go to Settings menu?")
                .setPositiveButton("Settings", (dialog, i) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, i) -> dialog.cancel());
        builder.show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}
