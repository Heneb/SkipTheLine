package com.bhoeft.skip_the_line.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.*;
import android.os.*;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.*;
import com.bhoeft.skip_the_line.R;
import com.bhoeft.skip_the_line.communication.ServerInterface;
import com.bhoeft.skip_the_line.ui.util.*;
import com.bhoeft.skip_the_line.util.UniqueIdentifier;
import org.altbeacon.beacon.*;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import util.Config;

import java.util.*;

import static com.bhoeft.skip_the_line.R.drawable.*;

/**
 * Grundview der gesamten App. Hier werden die Actionbar und das Tablayout für die 3 Fragmente festgelegt.
 * Auch läuft in dieser Activity der Beacon-Scanner.
 *
 * @author Benedikt Höft, 15.05.16.
 */
public class MainActivity extends AppCompatActivity implements BeaconConsumer, ISettingsChangedCallback
{
  private static final int CURRENT_ITEM = 1;
  private static final int OFFSCREEN_PAGE_LIMIT = 2;

  private ViewPager viewpager;
  private ImageView imageView;
  private ViewPagerAdapter adapter;
  private String appID;

  //Variables for checking if app needs to sign off from server
  private int inBeaconRange = -1;
  private boolean checkedInServer = false;

  private BeaconManager beaconManager;
  private Identifier idToFind;

  @Override
  protected void onCreate(final Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    _requestPermissions();

    if (_checkRequiered())
    {
      idToFind = Identifier.fromUuid(UUID.fromString(Config.getPropertyString("hawla_uuid")));
      appID = UniqueIdentifier.id(this);
      _initUI();
      _setBeaconSettings();
    }
    else
    {
      Toast.makeText(this, getString(R.string.no_connection_msg), Toast.LENGTH_LONG).show();
    }
  }

  /**
   * Überprüft, ob eine Internetverbindung besteht
   *
   * @return true falls Verbindung besteht, andernfalls false
   */
  private boolean _checkRequiered()
  {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager.getActiveNetworkInfo() == null)
      return false;
    else
    {
      NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
      if (infos != null)
        for (int i = 0; i < infos.length; i++)
        {
          if (infos[i].isConnected())
            return true;
        }
      return false;
    }
  }

  /**
   * Initialisiert die UI-Komponenten
   */
  private void _initUI()
  {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    if (toolbar != null)
      setSupportActionBar(toolbar);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    tabLayout.addTab(tabLayout.newTab().setText(R.string.fragmentTabTitleMenu));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.fragmentTabTitleMain));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.fragmentTabTitleSettings));
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    viewpager = (ViewPager) findViewById(R.id.pager);
    adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), this);
    viewpager.setAdapter(adapter);
    viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    viewpager.setCurrentItem(CURRENT_ITEM);
    viewpager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);

    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
    {
      @Override
      public void onTabSelected(TabLayout.Tab tab)
      {
        viewpager.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab)
      {
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab)
      {
      }
    });
    imageView = (ImageView) findViewById(R.id.statusLed);
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    beaconManager.unbind(this);
  }

  /**
   * Initialisiert die Einstellungen für den Beacon-Scanner
   */
  private void _setBeaconSettings()
  {
    beaconManager = BeaconManager.getInstanceForApplication(this);

    beaconManager.setForegroundScanPeriod(Config.getPropertyLong("foreground_scan"));
    beaconManager.setForegroundBetweenScanPeriod(Config.getPropertyLong("foreground_between_scan"));
    beaconManager.setBackgroundScanPeriod(Config.getPropertyLong("background_scan"));
    beaconManager.setBackgroundBetweenScanPeriod(Config.getPropertyLong("background_between_scan"));

    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(Config.getPropertyString("estimote_parser")));
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(Config.getPropertyString("ibeacon_parser")));

    beaconManager.bind(this);

    try
    {
      beaconManager.updateScanPeriods();
    }
    catch (RemoteException pE)
    {
      throw new RuntimeException(pE);
    }

    new BackgroundPowerSaver(this);
  }

  /**
   * Einstellungen, was bei Finden einer bestimmten Beacon-UUID oder im anderen Fall passieren soll
   */
  @Override
  public void onBeaconServiceConnect()
  {
    beaconManager.setRangeNotifier(new RangeNotifier()
    {
      @Override
      public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region)
      {
        boolean beaconFound = false;
        for (Beacon currBeacon : beacons)
        {
          System.out.println(currBeacon.getId1());
          if (currBeacon.getId1().equals(idToFind))
          {
            //am server anmelden
            inBeaconRange = 20;
            checkedInServer = true;
            //send id to server
            runOnUiThread(new Runnable()
            {
              @Override
              public void run()
              {
                _changeStatusLight(true);
              }
            });
            beaconFound = true;
            ServerInterface.addQueueEntry(appID);
          }
        }
        if (!beaconFound && checkedInServer && inBeaconRange > 0)
        {
          //vom server austragen falls noch nicht geschehen
          inBeaconRange--;
          runOnUiThread(new Runnable()
          {
            @Override
            public void run()
            {
              _changeStatusLight(false);
            }
          });
          ServerInterface.removeQueueEntry(appID);
        }
      }
    });

    try
    {
      beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
    }
    catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Je nach Parameter wird die Farbe der LED-Anzeige gewechselt
   *
   * @param pInRange Ob sich ein Beacon in Reichweite befindet
   */
  private void _changeStatusLight(boolean pInRange)
  {
    if (imageView != null)
    {
      if (pInRange)
        imageView.setImageDrawable(getResources().getDrawable(status_led_green));
      else
        imageView.setImageDrawable(getResources().getDrawable(status_led_red));
    }
  }

  /**
   * Frägt die geforderten Berechtigungen für die App ab
   */
  private void _requestPermissions()
  {
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
      ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
  }

  /**
   * Bei Änderung der favorisierten Mensa ändert sich die Speiseplanansicht
   *
   * @param canteenPosition Positionswert der neu ausgewählten Mensa
   */
  @Override
  public void canteenChanged(int canteenPosition)
  {
    adapter.getFragmentMenu().updateWebViewURL(canteenPosition);
  }

  /**
   * Bei Änderung zeigt der Graph vergangene Durchschnittsdaten an oder nicht
   *
   * @param pShowHistory Ob Durchschnittsdaten angezeigt werden sollen
   */
  @Override
  public void historySettingsChanged(boolean pShowHistory)
  {
    adapter.getFragmentMain().setShowHistory(pShowHistory);
  }
}
