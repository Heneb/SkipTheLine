package com.bhoeft.skip_the_line.ui.util;

import android.support.v4.app.*;
import com.bhoeft.skip_the_line.ui.*;

/**
 * Adapter für das Erzeugen der 3 Fragmente
 *
 * @author Benedikt Höft on 19.05.16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
  private int mNumOfTabs;
  private FragmentMenu fragmentMenu;
  private FragmentMain fragmentMain;
  private FragmentSettings fragmentSettings;
  private ISettingsChangedCallback callback;

  public ViewPagerAdapter(FragmentManager fm, int NumOfTabs, ISettingsChangedCallback pCallback)
  {
    super(fm);
    this.mNumOfTabs = NumOfTabs;
    callback = pCallback;
  }

  @Override
  public Fragment getItem(int position)
  {

    switch (position)
    {
      case 0:
        return (fragmentMenu = new FragmentMenu());
      case 1:
        return (fragmentMain = new FragmentMain());
      case 2:
        return (fragmentSettings = new FragmentSettings(callback));
      default:
        return null;
    }
  }

  @Override
  public int getCount()
  {
    return mNumOfTabs;
  }

  public FragmentMenu getFragmentMenu()
  {
    return fragmentMenu;
  }

  public FragmentMain getFragmentMain()
  {
    return fragmentMain;
  }

  public FragmentSettings getFragmentSettings()
  {
    return fragmentSettings;
  }
}
