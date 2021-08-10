package com.sagar.snaphub;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sagar.snaphub.HomeFragment.HomeFragment;
import com.sagar.snaphub.LatestFragment.LatestFragment;
import com.sagar.snaphub.ProfileFragment.ProfileFragment;
import com.sagar.snaphub.RegisterFragment.RegisterFragment;
import com.sagar.snaphub.RegistrationSystem.RegisterActivity;
import com.sagar.snaphub.TrendingFragment.TrendingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import sagar.snaphub.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static Fragment profileFragment;
    public final FragmentManager fragmentManager = getSupportFragmentManager();
    public static BottomNavigationView navigation;

    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////// Initialization
        navigation = findViewById(R.id.nav_view);
        ////////// Initialization
        init();
    }


    private void init() {
        /////////////// Adding Fragments
        setFragment(new HomeFragment(), "HOME_FRAGMENT", fragmentManager);
        /////////////// Adding Fragments
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                setFragment(new HomeFragment(), "HOME_FRAGMENT", fragmentManager);
                return true;
            case R.id.navigation_notifications:
                setFragment(new LatestFragment(), "LATEST_FRAGMENT", fragmentManager);
                return true;
            case R.id.navigation_profile:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    profileFragment = new RegisterFragment();
                } else {
                    profileFragment = new ProfileFragment();
                }
                setFragment(profileFragment, "PROFILE_FRAGMENT", fragmentManager);
                return true;
            case R.id.navigation_trending:
                setFragment(new TrendingFragment(), "TRENDING_FRAGMENT", fragmentManager);
                return true;
            default:
                return false;

        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            switch (fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName()) {
                case "HOME_FRAGMENT":
                    navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
                    break;
                case "LATEST_FRAGMENT":
                    navigation.getMenu().findItem(R.id.navigation_notifications).setChecked(true);
                    break;
                case "PROFILE_FRAGMENT":
                    navigation.getMenu().findItem(R.id.navigation_profile).setChecked(true);
                    break;
                case "TRENDING_FRAGMENT":
                    navigation.getMenu().findItem(R.id.navigation_trending).setChecked(true);
                    break;
            }
            fragmentManager.popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public static void setFragment(Fragment fragment, String fragmentName, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(fragmentName).replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null && RegisterActivity.fromRegisterFragment) {
            profileFragment = new ProfileFragment();
//            fragmentManager.beginTransaction().add(R.id.fragment_container, profileFragment, "5").hide(profileFragment).commit();
//            fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
//            active = profileFragment;
            fragmentManager.popBackStack();
            navigation.setSelectedItemId(R.id.navigation_profile);
            RegisterActivity.fromRegisterFragment = false;
        }
    }
}