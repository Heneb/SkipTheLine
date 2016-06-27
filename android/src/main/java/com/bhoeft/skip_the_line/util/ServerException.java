package com.bhoeft.skip_the_line.util;

import android.content.Context;
import com.bhoeft.skip_the_line.R;

/**
 * Erzeugt eine Benachrichtigung bei Auftreten eines Server-Fehlers
 *
 * @author Benedikt Höft on 22.06.16.
 */
public class ServerException extends Exception
{
  private EServerOperation operation;

  public ServerException(EServerOperation pOperation)
  {
    operation = pOperation;
  }

  /**
   * Liefert eine Fehlermeldung abhängig vom Typ der Anfrage
   *
   * @param pContext Kontext
   * @return Fehler-Nachricht
   */
  public String getErrorMessage(Context pContext)
  {
    int errorMessageID;

    switch (operation)
    {
      case GET_CURRENTAVG:
        errorMessageID = R.string.error_get_currentAvg;
        break;
      case GET_CURRENTGRAPH:
        errorMessageID = R.string.error_get_currentGraph;
        break;
      case GET_HISTORYGRAPH:
        errorMessageID = R.string.error_get_historyGraph;
        break;
      default:
        errorMessageID = R.string.error_unknown;
    }

    return pContext.getString(errorMessageID);
  }

  /**
   * Definiert die möglichen Server-Operationen
   */
  public enum EServerOperation
  {
    GET_CURRENTAVG, GET_CURRENTGRAPH, GET_HISTORYGRAPH
  }
}
