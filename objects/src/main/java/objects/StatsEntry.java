package objects;

import annotations.StorableObject;
import definition.AbstractStorableObject;
import definition.fields.*;

/**
 * @author Benedikt Höft on 21.06.16.
 */
@StorableObject(fieldName = "stats", boxName = "stats")
public class StatsEntry extends AbstractStorableObject
{
  public static final Field<Integer> avgAmount = FieldFactory.field(Integer.class);
  public static final Field<Long> startTime = FieldFactory.field(Long.class);

  public StatsEntry()
  {
  }

  public StatsEntry(int pAvgAmount, long pStartTime)
  {
    setValue(avgAmount, pAvgAmount);
    setValue(startTime, pStartTime);
  }

  public int getAvgAmount()
  {
    return getValue(avgAmount);
  }

  public long getStartTime()
  {
    return getValue(startTime);
  }
}
