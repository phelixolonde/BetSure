package com.automata.betsure;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import hotchemi.android.rate.AppRate;

public class Home extends AppCompatActivity {


    private static final String TAG ="FACEBOOK_ADS" ;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private InterstitialAd interstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        mViewPager.setCurrentItem(1);
        AdSettings.addTestDevice("e1112f97-7a12-4528-b088-917442576629");
        interstitialAd = new InterstitialAd(this, "946939095507299_946940662173809");
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.about) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("BET SURE");
            try {
                alert.setMessage("Version " + getApplication().getPackageManager().getPackageInfo(getPackageName(), 0).versionCode +
                        "\n"+"Automata Software. \n" + "\n" +
                        "All rights reserved \n"
                );
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            alert.show();
        } else if (id == R.id.feedback) {
            startActivity(new Intent(Home.this, Feedback.class));
        }else if (id == R.id.rate) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(Home.this, "Unable to find play store", Toast.LENGTH_SHORT).show();
            }
        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.menu_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "I've been winning big with this Bet Sure app. Download here https://play.google.com/store/apps/details?id=com.automata.betsure";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Bet Sure");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(sharingIntent);
        }

        return super.onOptionsItemSelected(item);
    }





    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new YesterdayFragment();
                case 1:
                    return new TodayFragment();
                case 2:
                    return new TomorrowFragment();
                    default:
                        return new TodayFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "YESTERDAY";
                case 1:
                    return "TODAY";
                case 2:
                    return "TOMORROW";
                default:
                    return "TODAY";
            }
        }
    }
    @Override
    public void onBackPressed() {


        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(2) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
        if (!AppRate.showRateDialogIfMeetsConditions(this)) {
            super.onBackPressed();
        }
    }
}
