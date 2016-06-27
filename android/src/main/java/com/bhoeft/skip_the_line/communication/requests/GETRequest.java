package com.bhoeft.skip_the_line.communication.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.methods.HttpGet;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * GETRequest für die Abfragen beim Server
 *
 * @author Benedikt Höft on 22.06.16.
 */
public class GETRequest<T> extends AbstractResponseWebservice<T>
{
  private InputStream response;

  /**
   * Erzeugt den GET-Request
   *
   * @param pURLMethod      benötigte Webservice Methode
   * @param pRequestedClass Typ des abzufragenden Objektes
   * @param pParams         zusätzliche Parameter für die Abfrage (werden an die URL gehängt)
   */
  public GETRequest(String pURLMethod, Class<T> pRequestedClass, Object... pParams)
  {
    super(pURLMethod, pRequestedClass, pParams);
  }

  /**
   * Erzeugt den GET-Request
   *
   * @param pURLMethod     benötigte Webservice Methode
   * @param pTypeReference Typ des abzufragenden Objektes
   * @param pParams        zusätzliche Parameter für die Abfrage (werden an die URL gehängt)
   */
  public GETRequest(String pURLMethod, TypeReference<T> pTypeReference, Object... pParams)
  {
    super(pURLMethod, pTypeReference, pParams);
  }


  /**
   * Liefert das Ergebnis der Abfrage als JSON-String
   */
  protected InputStream getResponse() throws IOException
  {
    return response;
  }

  /**
   * Führt den Request aus
   *
   * @param pEntity optionale Entity als Parameter für den Request
   * @return <tt>true</tt> bei erfolgreichem Ausführen
   */
  @Override
  public boolean execute(@Nullable Object pEntity)
  {
    try
    {
      HttpGet request = new HttpGet(url);
      response = httpClient.execute(request).getEntity().getContent();
      return true;
    }
    catch (Exception pE)
    {
      return false;
    }
  }
}
