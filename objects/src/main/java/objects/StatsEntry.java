package objects;

import annotations.StorableObject;
import definition.AbstractStorableObject;
import definition.fields.*;

/**
 * Object für das Speichern der Graphdaten
 *
 * @author Benedikt Höft on 21.06.16.
 */
@StorableObject(fieldName = "stats", boxName = "stats")
public class StatsEntry extends AbstractStorableObject
{
  public static final Field<Integer> avgAmount = FieldFactory.field(Integer.class);
  public static final Field<Long> startTime = FieldFactory.field(Long.class);
  public static final Field<Integer> weekDay = FieldFactory.field(Integer.class);

  public StatsEntry()
  {
  }

  public StatsEntry(int pAvgAmount, long pStartTime, int pWeekDay)
  {
    setValue(avgAmount, pAvgAmount);
    setValue(startTime, pStartTime);
    setValue(weekDay, pWeekDay);
  }

  public int getAvgAmount()
  {
    return getValue(avgAmount);
  }

  public long getStartTime()
  {
    return getValue(startTime);
  }

  public int getWeekDay()
  {
    return getValue(weekDay);
  }
}
