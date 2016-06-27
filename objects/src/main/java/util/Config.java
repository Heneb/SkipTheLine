package util;


import java.io.IOException;
import java.util.Properties;

/**
 * Läd Daten aus der config.properties-Datei
 *
 * @author Benedikt Höft on 26.06.16.
 */
public class Config
{
  private final static Properties properties = new Properties();

  static
  {
    try
    {
      properties.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
    }
    catch (IOException pE)
    {
      throw new RuntimeException(pE);
    }
  }

  /**
   * Holt einen String aus der Config.properties-Datei
   *
   * @param key Schlüsselwort
   * @return gefundener String
   */
  public static String getPropertyString(String key)
  {
    return properties.getProperty(key);
  }

  /**
   * Holt einen Long-Wert aus der Config.properties-Datei
   *
   * @param key Schlüsselwort
   * @return gefundener Long-Wert
   */
  public static long getPropertyLong(String key)
  {
    return Long.parseLong(properties.getProperty(key));
  }

  /**
   * Holt einen Integer-Wert aus der Config.properties-Datei
   *
   * @param key Schlüsselwort
   * @return gefundener Integer-Wert
   */
  public static double getPropertyDouble(String key)
  {
    return Double.parseDouble(properties.getProperty(key));
  }

  /**
   * Holt ein String-Array aus der Config.properties-Datei
   *
   * @param key Schlüsselwort
   * @return gefundenes String-Array
   */
  public static String[] getPropertyStringArray(String key)
  {
    return properties.getProperty(key).split("#");
  }
}
