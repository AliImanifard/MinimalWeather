package com.ali.minimalweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ali.minimalweather.databinding.ActivityFirstBinding;

public class FirstActivity extends AppCompatActivity {

    private static final int PERMISSIONS_RQUEST_CODE = 123;
    private static final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.SCHEDULE_EXACT_ALARM
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFirstBinding firstBinding = ActivityFirstBinding.inflate(getLayoutInflater());
        setContentView(firstBinding.getRoot());


        //Animations
        firstBinding.saWeatherCloud.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.top_animation));
        firstBinding.saTv.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.bottom_animation));
        firstBinding.saLeaf.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));


        // Check if the required permissions are granted
        if (checkPermissions()) {

            // Permissions are already granted, proceed with your app logic
            launchMainActivity();

        } else {

            // Request the required permissions
            requestMyPermissions();
        }
    }

    private boolean checkPermissions() {
        // Check if the permission is already granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                            &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                            &&
                            ContextCompat.checkSelfPermission(this,Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED
                    ;
        }

        return true; // Permissions are granted by default on older versions
    }

    private void requestMyPermissions() {
        // Request the necessary permissions at runtime
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_RQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_RQUEST_CODE) {
            // Check if all permissions are granted
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                // All permissions are granted, proceed with your app logic
                launchMainActivity();
            } else {
                // Some permissions are denied, show an error message or handle it accordingly
                Toast.makeText(this, R.string.permissions_are_not_granted, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void launchMainActivity() {
        // Delay the splash screen for a few seconds using a Handler
        new Handler().postDelayed(() -> {
            startActivity(new Intent(FirstActivity.this, MainActivity.class));
            finish();
        }, 3000);  // Splash screen duration in milliseconds
    }
}