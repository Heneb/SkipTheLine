package com.bhoeft.skip_the_line;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.altbeacon.beacon.*;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static com.bhoeft.skip_the_line.R.drawable.status_led_green;
import static com.bhoeft.skip_the_line.R.drawable.status_led_red;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, FragmentSettings.OnFavoriteSelectedListener {
    public static boolean showHistory = false;
    ViewPager viewpager;
    private boolean status = false;

    //Beacon stuff
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconManager beaconManager;
    private Identifier idToFind = Identifier.fromUuid(UUID.fromString("3c52bad2-4b1d-4ba4-9535-0ae9771dfc3e"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //getSupportActionBar().setTitle("Skip the Line");
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //getSupportActionBar().setTitle("Skip the Line");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Menu"));
        tabLayout.addTab(tabLayout.newTab().setText("Main"));
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewpager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewpager.setCurrentItem(1);
        viewpager.setOffscreenPageLimit(2);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm");

                TextView lastRefresh = (TextView) findViewById(R.id.statusRefreshTimeTextView);
                lastRefresh.setText(date.format(currentLocalTime));

                ImageView imageView = (ImageView) findViewById(R.id.statusLed);
                if (imageView != null) {
                    if (!status) {
                        imageView.setImageDrawable(getResources().getDrawable(status_led_green));
                        status = true;
                    } else {
                        imageView.setImageDrawable(getResources().getDrawable(status_led_red));
                        status = false;
                    }
                }
                Snackbar.make(view, "Scanning for devices...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            setBeaconSettings();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        beaconManager.unbind(this);
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
    */
    //The user selected a favorite canteen in FragmentSettings
    public void onCanteenSelected(int pos) {
        FragmentMenu fragmentMenu = (FragmentMenu) getSupportFragmentManager().findFragmentById(R.id.settingsFavoriteSpinner);

        if (fragmentMenu != null) {
            // If article frag is available, we're in two-pane layout...
            // Call a method in the ArticleFragment to update its content
            fragmentMenu.updateWebViewURL(pos);
            System.out.println("Update URL");
        } else {
            System.out.println("Fail " + pos);
        }
    }

    private void setBeaconSettings() throws RemoteException {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.setForegroundScanPeriod(2000L);
        beaconManager.setForegroundBetweenScanPeriod(5000L);
        beaconManager.setBackgroundScanPeriod(2000L);
        beaconManager.setBackgroundBetweenScanPeriod(30000L);


        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        beaconManager.updateScanPeriods();
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if(beacons.size() > 0){
                    for(Beacon currBeacon : beacons){
                        if(currBeacon.getId1().equals(idToFind)){
                            //send id to server
                            System.out.println("Juhu gefunden!");
                        }
                    }
                }
            }
        });

        try{
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch(RemoteException e){

        }
    }
}
