package com.ali.minimalweather;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;

public class NetworkErrorDialog extends Dialog implements View.OnClickListener {

    private final Context context;

    public NetworkErrorDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_network_error);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());

        // setting width to 90% of display
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.9f);

        // setting height to 90% of display
        layoutParams.height = (int) (displayMetrics.heightPixels * 0.5f);

        getWindow().setAttributes(layoutParams);


        // for Lottie Animation View
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Calculate the desired width and height
        int desiredWidth = (int) (screenWidth * 0.9); // 90% of the screen width
        int desiredHeight = (int) (screenHeight * 0.5); // 50% of the screen height

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);


        LottieAnimationView networkErrorLottie = findViewById(R.id.network_error);
        ViewGroup.LayoutParams params = networkErrorLottie.getLayoutParams();
        params.width = desiredWidth;
        params.height = desiredHeight;
        networkErrorLottie.setLayoutParams(params);


        networkErrorLottie.setAnimation(R.raw.network_error);
        networkErrorLottie.setVisibility(View.VISIBLE);
        networkErrorLottie.playAnimation();

        Button btnNetworkError = findViewById(R.id.btn_network_error);
        btnNetworkError.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_network_error) {

            // restart app
            if (context instanceof Activity) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                dismiss();
                context.startActivity(intent);

                // Finish the current activity (optional)
                ((Activity) context).finish();
            } else {
                Toast.makeText(context, R.string.retry_failed, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
