package org.apache.metamodel.googlead.util;

/**
 * Created by ptmind on 2015/10/21.
 */
public enum AdwordsReportType {

  ACCOUNT_PERFORMANCE_REPORT("ACCOUNT_PERFORMANCE_REPORT"), CAMPAIGN_PERFORMANCE_REPORT(
      "CAMPAIGN_PERFORMANCE_REPORT"), ADGROUP_PERFORMANCE_REPORT("ADGROUP_PERFORMANCE_REPORT"), AD_PERFORMANCE_REPORT(
      "AD_PERFORMANCE_REPORT"), KEYWORDS_PERFORMANCE_REPORT("KEYWORDS_PERFORMANCE_REPORT"), // Search
                                                                                            // keyword,Search
                                                                                            // keyword
                                                                                            // state,Match
                                                                                            // type
  GEO_PERFORMANCE_REPORT("GEO_PERFORMANCE_REPORT"), // Country/Territory,Region,Metro area,City,Most
                                                    // specific location
  PARENTAL_STATUS_PERFORMANCE_REPORT("PARENTAL_STATUS_PERFORMANCE_REPORT"), // Parental status
  AGE_RANGE_PERFORMANCE_REPORT("AGE_RANGE_PERFORMANCE_REPORT"), // Age
  GENDER_PERFORMANCE_REPORT("GENDER_PERFORMANCE_REPORT"), // Gender
  AUDIENCE_PERFORMANCE_REPORT("AUDIENCE_PERFORMANCE_REPORT"), // Audience
  DISPLAY_TOPICS_PERFORMANCE_REPORT("DISPLAY_TOPICS_PERFORMANCE_REPORT"), // Topic
  PLACEMENT_PERFORMANCE_REPORT("PLACEMENT_PERFORMANCE_REPORT"), // Placement
  DISPLAY_KEYWORD_PERFORMANCE_REPORT("DISPLAY_KEYWORD_PERFORMANCE_REPORT"), // Display keyword
  SEARCH_QUERY_PERFORMANCE_REPORT("SEARCH_QUERY_PERFORMANCE_REPORT"), // Search term
  BID_GOAL_PERFORMANCE_REPORT("BID_GOAL_PERFORMANCE_REPORT")// Bid strategy type
  ;

  private String value;

  AdwordsReportType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
