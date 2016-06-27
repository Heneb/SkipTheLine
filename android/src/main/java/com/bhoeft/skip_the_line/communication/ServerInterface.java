package com.bhoeft.skip_the_line.communication;

import com.bhoeft.skip_the_line.communication.requests.*;
import com.bhoeft.skip_the_line.util.ServerException;
import com.fasterxml.jackson.core.type.TypeReference;
import util.Config;

import java.util.List;


/**
 * Interface für die Funktionen der Serveranfragen
 *
 * @author Benedikt Höft on 22.06.16.
 */
public final class ServerInterface
{
  private ServerInterface()
  {
  }

  /**
   * Trägt einen neuen Wartenden in die Schlange ein
   *
   * @param pAppID eindeutige ID des Wartenden
   */
  public static void addQueueEntry(String pAppID)
  {
    AbstractWebserviceRequest request = new PUTRequest("addQueueEntry");
    request.execute(pAppID);
  }

  /**
   * Trägt einen Wartenden aus der Schlange aus
   *
   * @param pAppID eindeutige ID des Wartenden
   */
  public static void removeQueueEntry(String pAppID)
  {
    AbstractWebserviceRequest request = new PUTRequest("removeQueueEntry");
    request.execute(pAppID);
  }

  /**
   * Holt die momentane Anzahl der Wartenden in der Schlange
   *
   * @return Anzahl der Wartenden
   * @throws ServerException falls der Server nicht erreichbar ist
   */
  public static int getCurrentAmount() throws ServerException
  {
    GETRequest<Integer> request = new GETRequest<>("getCurrentAmount", Integer.class);

    if (request.execute(null))
      return request.getObject();

    throw new ServerException(ServerException.EServerOperation.GET_CURRENTAVG);
  }

  /**
   * Holt die Daten des heutigen Tages der Warteschlange für den Graph
   *
   * @return Liste von Warteschlangendaten
   * @throws ServerException falls der Server nicht erreichbar ist
   */
  public static List<Integer> getCurrentGraphData() throws ServerException
  {
    TypeReference<List<Integer>> type = new TypeReference<List<Integer>>()
    {
    };

    GETRequest<List<Integer>> request = new GETRequest<>("getCurrentGraphData", type);

    if (request.execute(null))
      return request.getObject();

    throw new ServerException(ServerException.EServerOperation.GET_CURRENTAVG);
  }

  /**
   * Holt die Daten eines früheren Tages der Warteschlange für den Graph
   *
   * @param pWeekDay Tag der dargestellt werden soll
   * @return Liste von Warteschlangendaten
   * @throws ServerException falls der Server nicht erreichbar ist
   */
  public static List<Integer> getHistoryGraphData(int pWeekDay) throws ServerException
  {
    TypeReference<List<Integer>> type = new TypeReference<List<Integer>>()
    {
    };

    double start = Config.getPropertyDouble("hawla_start_time");
    double end = Config.getPropertyDouble("hawla_end_time");

    GETRequest<List<Integer>> request = new GETRequest<>("getHistoryGraphData", type, pWeekDay, start, end);

    if (request.execute(null))
      return request.getObject();

    throw new ServerException(ServerException.EServerOperation.GET_CURRENTAVG);
  }
}
