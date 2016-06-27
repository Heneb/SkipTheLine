package com.bhoeft.skip_the_line.communication.requests;

import autodiscover.CustomObjectMapper;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.*;
import org.jetbrains.annotations.Nullable;
import registry.BoxRegistry;

/**
 * @author Benedikt Höft on 22.06.16.
 */
public abstract class AbstractWebserviceRequest
{
  private static final String SERVER_URL = "http://192.168.1.28:8080/";

  protected String url;
  protected HttpClient httpClient;
  protected ObjectMapper mapper;

  /**
   * Erzeugt eine neue Webservice-Anfrage
   *
   * @param pURLMethod Name des Webservice
   * @param pParams    beliebig viele Parameter
   */
  public AbstractWebserviceRequest(String pURLMethod, Object... pParams)
  {
    url = SERVER_URL + pURLMethod;
    for (Object param : pParams)
      if (param != null)
        url += "/" + param.toString();

    final HttpParams httpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
    httpClient = new DefaultHttpClient(httpParams);

    //Jackson Object-Mapper
    mapper = new CustomObjectMapper(BoxRegistry.getAllBoxTypes());
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
  }

  /**
   * Führt den Request aus
   *
   * @param pEntity optionale Entity als Parameter für den Request
   * @return <tt>true</tt> bei erfolgreichem Ausführen
   */
  public abstract boolean execute(@Nullable Object pEntity);

  /**
   * Liefert den Content-Type des Request abhängig von der Entity
   *
   * @param pEntity die Entity
   */
  protected String getContentType(@Nullable Object pEntity)
  {
    String contentType = pEntity == null || pEntity instanceof String ? "text/plain" : "application/json";
    contentType += ";charset=UTF-8";
    return contentType;
  }
}
