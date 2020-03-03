package com.navigatpeer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class SplashActivity extends Activity implements AnimationListener {
    private static boolean splashLoaded = false;
    Animation animBounce;
    Button btnStart;
    ImageView imgPoster;

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            public void run() {

                SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                SplashActivity.this.finish();

            }
        }, (long) 1500);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.imgPoster.setVisibility(0);
                SplashActivity.this.imgPoster.startAnimation(SplashActivity.this.animBounce);
            }
        }, 0);
        splashLoaded = true;
        this.imgPoster = (ImageView) findViewById(R.id.imgLogo);
        this.animBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        this.animBounce.setAnimationListener(this);
    }

    public void onAnimationEnd(Animation animation) {
        Animation animation2 = this.animBounce;
    }
}
