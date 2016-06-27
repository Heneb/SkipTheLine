package objects;

import annotations.StorableObject;
import definition.AbstractStorableObject;
import definition.fields.*;

/**
 * Object für das Speichern einzelner Warteschlangeneinträge
 *
 * @author Benedikt Höft on 21.06.16.
 */
@StorableObject(fieldName = "queue", boxName = "queue")
public class QueueEntry extends AbstractStorableObject
{
  public static final Field<String> appID = FieldFactory.field(String.class);
  public static final Field<Long> entryTime = FieldFactory.field(Long.class);

  public QueueEntry()
  {
  }

  public QueueEntry(String pAppID)
  {
    setValue(appID, pAppID);
    setValue(entryTime, System.currentTimeMillis());
  }

  public String getAppID()
  {
    return getValue(appID);
  }

  public long getEntryTime()
  {
    return getValue(entryTime);
  }
}
