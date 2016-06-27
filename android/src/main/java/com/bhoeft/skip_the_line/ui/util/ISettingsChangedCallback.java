package com.bhoeft.skip_the_line.ui.util;

/**
 * Interface für Änderungen im Einstellungs-Tab
 *
 * @author Benedikt Höft on 25.06.16.
 */
public interface ISettingsChangedCallback
{
  void canteenChanged(int canteenPosition);

  void historySettingsChanged(boolean showHistory);
}
