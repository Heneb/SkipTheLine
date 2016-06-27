package com.bhoeft.skip_the_line.communication.requests;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.jetbrains.annotations.Nullable;

/**
 * PUTRequests für die Abfragen beim Server
 *
 * @author Benedikt Höft on 22.06.16.
 */
public class PUTRequest extends AbstractWebserviceRequest
{
  /**
   * Erzeugt den Request
   *
   * @param pURLMethod Webservice Methode
   */
  public PUTRequest(String pURLMethod)
  {
    super(pURLMethod);
  }

  /**
   * Führt die Abfrage aus
   *
   * @return Response-Code der Abfrage
   */
  public boolean execute(@Nullable Object pEntity)
  {
    try
    {
      String value = pEntity instanceof String ? (String) pEntity : mapper.writeValueAsString(pEntity);
      HttpPut request = new HttpPut(url);
      request.setHeader(HTTP.CONTENT_TYPE, getContentType(pEntity));
      request.setEntity(new StringEntity(value, HTTP.UTF_8));
      httpClient.execute(request);
      return true;
    }
    catch (Exception pE)
    {
      return false;
    }
  }
}
