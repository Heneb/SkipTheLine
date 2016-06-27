package util;

import objects.StatsEntry;

import java.util.*;

/**
 * @author Benedikt Höft on 21.06.16.
 */
public final class CommonUtil
{
  private CommonUtil()
  {
  }

  public static boolean isTheSameDay(long pTimeStamp1, long pTimeStamp2)
  {
    long start1 = getStartOfDay(pTimeStamp1);
    long start2 = getStartOfDay(pTimeStamp2);
    long end1 = getEndOfDay(pTimeStamp1);
    long end2 = getEndOfDay(pTimeStamp2);

    return start1 == start2 && end1 == end2;
  }

  /**
   * Liefert den Timestamp für den Beginn eines Tages ausgehend von einem beliebigen Timestamp
   */
  public static long getStartOfDay(long pTimeStamp)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(pTimeStamp));
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTimeInMillis();
  }

  /**
   * Liefert den Timestamp für das Ende eines Tages ausgehend von einem beliebigen Timestamp
   */
  public static long getEndOfDay(long pTimeStamp)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(pTimeStamp));
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTimeInMillis();
  }

  /**
   * Liefert eine sortierte Liste von Einträgen
   *
   * @param pEntries zu soriterende Liste
   * @return sortierte Liste
   */
  public static List<StatsEntry> sortByDayStartTime(List<StatsEntry> pEntries)
  {
    Comparator<StatsEntry> comparator = (pEntry1, pEntry2) ->
        getDayTimeInSeconds(pEntry1.getStartTime()) - getDayTimeInSeconds(pEntry2.getStartTime());

    Collections.sort(pEntries, comparator);
    return pEntries;
  }

  /**
   * Extrahiert die Tageszeit aus dem angegebenen Datum
   *
   * @param pDate zu extrahierendes Datum
   * @return Tageszeit in Sekunden
   */
  public static int getDayTimeInSeconds(long pDate)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(pDate);
    int hours = calendar.get(Calendar.HOUR_OF_DAY);
    int minutes = calendar.get(Calendar.MINUTE);
    int seconds = calendar.get(Calendar.SECOND);

    return hours * 60 * 60 + minutes * 60 + seconds;
  }
}
