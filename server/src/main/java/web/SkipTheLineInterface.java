package web;

/**
 * @author Benedikt Höft on 21.06.16.
 */

import objects.*;
import registry.BoxRegistry;
import util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
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
    StatsService.getInstance().add(BoxRegistry.QUEUE.size(), Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
  }

  @PUT
  @Path("removeQueueEntry")
  @Consumes(MediaType.TEXT_PLAIN)
  public void removeQueueEntry(String pAppID)
  {
    BoxRegistry.QUEUE.remove(QueueEntry.appID.asSearch(pAppID));
    StatsService.getInstance().add(BoxRegistry.QUEUE.size(), Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
  }

  @GET
  @Path("getCurrentAmount")
  @Produces(MediaType.TEXT_PLAIN)
  public int getCurrentAmount()
  {
    return BoxRegistry.QUEUE.size();
  }

  @GET
  @Path("getCurrentGraphData")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Integer> getCurrentGraphData()
  {
    List<Integer> currentData = BoxRegistry.STATS.find().stream().
        filter(entry -> CommonUtil.isTheSameDay(System.currentTimeMillis(), entry.getStartTime())).
        map(StatsEntry::getAvgAmount).collect(Collectors.toList());

    currentData.add(getCurrentAmount());
    return currentData;
  }

  @GET
  @Path("getHistoryGraphData/{day}/{startTime}/{endTime}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public List<Integer> getHistoryGraphData(@PathParam("day") int pDay, @PathParam("startTime") double pStartTime,
                                           @PathParam("endTime") double pEndTime)
  {
    int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    double step = Config.getPropertyDouble("hawla_step");
    List<StatsEntry> entries = BoxRegistry.STATS.find(StatsEntry.weekDay.asSearch(weekday));
    entries = CommonUtil.sortByDayStartTime(entries);

    List<Integer> result = new ArrayList<>();
    for (double start = pStartTime; start <= pEndTime; start += step)
    {
      int sum = 0;
      int count = 0;

      while (entries.size() > 0)
      {
        StatsEntry current = entries.remove(0);
        if (CommonUtil.getDayTimeInSeconds(current.getStartTime()) >= (start + step) * 60 * 60)
          break;

        sum += current.getAvgAmount();
        count++;
      }

      result.add(count > 0 ? sum / count : 0);
    }

    return result;
  }
}
