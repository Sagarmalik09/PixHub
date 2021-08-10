package com.example.snaphub.RegistrationSystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.example.snaphub.MainActivity;
import com.example.snaphub.R;


public class RegisterActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    public static boolean onResetPasswordFragment;

    public static boolean fromLogin = true;
    public static boolean fromRegisterFragment = false;

    public static boolean IS_AUTHENTICATION_SKIPPED;
    public static Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        frameLayout = findViewById(R.id.register_framelayout);

        resources = getResources();


        SharedPreferences sharedPreferences = getSharedPreferences("PixHub", MODE_PRIVATE);
        IS_AUTHENTICATION_SKIPPED = sharedPreferences.getBoolean("IS_AUTHENTICATION_SKIPPED", false);

        if (IS_AUTHENTICATION_SKIPPED && !fromRegisterFragment) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }

        if (fromLogin) {
            setDefaultFragment(new LoginFragment());
        } else {
            setDefaultFragment(new signupFragment());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onResetPasswordFragment) {
                onResetPasswordFragment = false;
                setFragment(new LoginFragment());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

}