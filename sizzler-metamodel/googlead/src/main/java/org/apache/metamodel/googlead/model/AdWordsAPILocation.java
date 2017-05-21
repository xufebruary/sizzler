package org.apache.metamodel.googlead.model;

/**
 * 地理信息
 * @author you.zou by 2016.2.26
 *
 */
public class AdWordsAPILocation {
  private String criteriaID;
  private String name;
  private String canonicalName;
  private String parentID;
  private String countryCode;
  private String targetType;
  private String status;

  public String getCriteriaID() {
    return criteriaID;
  }

  public void setCriteriaID(String criteriaID) {
    this.criteriaID = criteriaID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  public void setCanonicalName(String canonicalName) {
    this.canonicalName = canonicalName;
  }

  public String getParentID() {
    return parentID;
  }

  public void setParentID(String parentID) {
    this.parentID = parentID;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getTargetType() {
    return targetType;
  }

  public void setTargetType(String targetType) {
    this.targetType = targetType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
