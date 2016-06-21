package web;

/**
 * @author Benedikt Höft on 21.06.16.
 */

import objects.*;
import registry.BoxRegistry;
import util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("")
public class SkipTheLineInterface
{
  @PUT
  @Path("addQueueEntry")
  @Consumes(MediaType.TEXT_PLAIN)
  public void addQueueEntry(String pAppID)
  {
    BoxRegistry.QUEUE.insert(new QueueEntry(pAppID));
    StatsService.getInstance().add(BoxRegistry.QUEUE.size());
  }

  @PUT
  @Path("removeQueueEntry")
  @Consumes(MediaType.TEXT_PLAIN)
  public void removeQueueEntry(String pAppID)
  {
    BoxRegistry.QUEUE.remove(QueueEntry.appID.asSearch(pAppID));
    StatsService.getInstance().add(BoxRegistry.QUEUE.size());
  }

  @GET
  @Path("getCurrentAmount")
  @Produces(MediaType.TEXT_PLAIN)
  public int getCurrentAmount()
  {
    return BoxRegistry.QUEUE.size();
  }

  @GET
  @Path("getStatsEntries/{timeStamp}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public List<StatsEntry> getStatsEntries(@PathParam("timeStamp") long pTimeStamp)
  {
    return BoxRegistry.STATS.find().stream().filter(entry -> CommonUtil.isTheSameDay(pTimeStamp, entry.getStartTime())).
        collect(Collectors.toList());
  }
}
