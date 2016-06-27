package util;

import objects.StatsEntry;
import registry.BoxRegistry;

import java.util.*;

/**
 * @author Benedikt Höft on 21.06.16.
 */
public class StatsService
{
  private static final int HALF_HOUR = 30 * 60 * 1000;
  private static StatsService instance;

  private long lastUpdate;
  private List<Integer> amounts;

  public static StatsService getInstance()
  {
    if (instance == null)
      instance = new StatsService();
    return instance;
  }

  private StatsService()
  {
    lastUpdate = -1;
    amounts = new ArrayList<>();
  }

  /**
   * Trägt die übergebene Anzahl der Wartenden in die Datenbank ein
   *
   * @param pAmount  Anzahl der Wartenden
   * @param pWeekDay Zeitpunkt der Messung
   */
  public void add(int pAmount, int pWeekDay)
  {
    long current = System.currentTimeMillis();

    if (lastUpdate == -1)
      lastUpdate = current;

    if (current - lastUpdate > HALF_HOUR)
    {
      int sum = 0;
      for (Integer amount : amounts)
        sum += amount;
      BoxRegistry.STATS.insert(new StatsEntry(sum / amounts.size(), lastUpdate, pWeekDay));
      lastUpdate = current;
      amounts.clear();
    }

    amounts.add(pAmount);
  }
}
