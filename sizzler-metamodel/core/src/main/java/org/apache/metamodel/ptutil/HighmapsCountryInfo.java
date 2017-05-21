package org.apache.metamodel.ptutil;

import java.io.Serializable;

/**
 * Highmaps 世界地图国家信息
 *
 * @author peng.xu
 */
public class HighmapsCountryInfo implements Serializable {

  private static final long serialVersionUID = -1628145853887982053L;

  private String id;
  private String isoA2;
  private String isoA3;
  private String name;
  private String countryAbbrev;
  private String woeId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIsoA2() {
    return isoA2;
  }

  public void setIsoA2(String isoA2) {
    this.isoA2 = isoA2;
  }

  public String getIsoA3() {
    return isoA3;
  }

  public void setIsoA3(String isoA3) {
    this.isoA3 = isoA3;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCountryAbbrev() {
    return countryAbbrev;
  }

  public void setCountryAbbrev(String countryAbbrev) {
    this.countryAbbrev = countryAbbrev;
  }

  public String getWoeId() {
    return woeId;
  }

  public void setWoeId(String woeId) {
    this.woeId = woeId;
  }

}
