package web;

import application.CMSBaseServerApplication;
import definition.ObjectBox;
import registry.BoxRegistry;
import util.EDatabaseType;

import javax.ws.rs.ApplicationPath;
import java.util.*;

/**
 * @author Benedikt Höft on 21.06.16.
 */
@ApplicationPath("")
public class ServerApplication extends CMSBaseServerApplication
{
  @Override
  public Set<Class<?>> getCustomWebserviceResources()
  {
    return Collections.singleton(SkipTheLineInterface.class);
  }

  @Override
  public EDatabaseType getDatabaseType()
  {
    return EDatabaseType.MONGO_DB;
  }

  @Override
  public Set<ObjectBox> getObjectBoxes()
  {
    return BoxRegistry.getAllBoxes();
  }
}
