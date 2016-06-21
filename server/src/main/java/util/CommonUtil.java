package util;

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
}
