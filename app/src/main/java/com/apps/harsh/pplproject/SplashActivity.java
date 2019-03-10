package com.apps.harsh.pplproject;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    Thread splashTread;
    TextView textView;
    ImageView imageView;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.image);
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Exo2-SemiBoldItalic.ttf");
        textView.setTypeface(myCustomFont);
        startAnimation();

        /*int SPLASH_TIME_OUT = 2500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, SPLASH_TIME_OUT);*/
    }

    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        animation.reset();

        /*Animation anim = AnimationUtils.loadAnimation(this, R.anim.blink);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);*/

        /*RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);*/

        /*FlipAnimation flipAnimation = new FlipAnimation(imageView,imageView);
        if (imageView.getVisibility() == View.GONE) {
            flipAnimation.reverse();
        }else{
            imageView.startAnimation(flipAnimation);
        }
        flipAnimation.setRepeatCount(Animation.INFINITE);
        flipAnimation.setDuration(700);*/

        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip);
        set.setTarget(imageView);
        set.start();

        textView.clearAnimation();
        textView.startAnimation(animation);
        //imageView.clearAnimation();
        //imageView.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 2000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashActivity.this,
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashActivity.this.finish();
                }

            }
        };
        splashTread.start();
    }
}
