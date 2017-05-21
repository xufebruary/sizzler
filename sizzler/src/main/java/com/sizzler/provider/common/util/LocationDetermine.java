package com.sizzler.provider.common.util;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ptmind on 2015/11/10.
 */
public class LocationDetermine {

  // iso 3166-1标准
  private static Set<String> CountryName_3166 = new HashSet<String>();
  private static Set<String> CountryCode2_3166 = new HashSet<String>();
  private static Set<String> CountryCode3_3166 = new HashSet<String>();

  // iso 3166-2
  private static Set<String> RegionName_3166 = new HashSet<String>();
  private static Set<String> RegionCode_3166 = new HashSet<String>();

  private static Set<String> CityName_3166 = new HashSet<String>();
  private static Set<String> CityCode_3166 = new HashSet<String>();

  static {
    try {
      // File countryFile=new File("d:/country.properties");
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream("locations/country.properties");

      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line = "";

      while ((line = reader.readLine()) != null) {
        String[] countryNameCode2Code3 = line.split(",");
        CountryName_3166.add(countryNameCode2Code3[0].toUpperCase());
        CountryCode2_3166.add(countryNameCode2Code3[1].toUpperCase());
        CountryCode3_3166.add(countryNameCode2Code3[2].toUpperCase());
      }

      // File regionFile=new File("d:/region.csv");
      inputStream = classLoader.getResourceAsStream("locations/region.properties");
      reader = new BufferedReader(new InputStreamReader(inputStream));
      line = "";
      while ((line = reader.readLine()) != null) {
        String[] regionNameCode = line.split(",");
        RegionName_3166.add(regionNameCode[0].toUpperCase());
        RegionCode_3166.add(regionNameCode[1].toUpperCase());
      }

      // File cityFile=new File("d:/city.csv");
      inputStream = classLoader.getResourceAsStream("locations/city.properties");
      reader = new BufferedReader(new InputStreamReader(inputStream));
      line = "";
      while ((line = reader.readLine()) != null) {
        String[] cityNameCode = line.split(",");
        CityName_3166.add(cityNameCode[0].toUpperCase());
        CityCode_3166.add(cityNameCode[1].toUpperCase());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static boolean isCountry(String inputStr) {
    String upperCaseInput = inputStr.trim().toUpperCase();

    return CountryName_3166.contains(upperCaseInput) || CountryCode2_3166.contains(upperCaseInput)
        || CountryCode3_3166.contains(upperCaseInput);
  }

  public static boolean isRegion(String inputStr) {
    String upperCaseInput = inputStr.trim().toUpperCase();
    return RegionName_3166.contains(upperCaseInput) || RegionCode_3166.contains(upperCaseInput);

  }

  public static boolean isCity(String inputStr) {
    String upperCaseInput = inputStr.trim().toUpperCase();

    return CityName_3166.contains(upperCaseInput) || CityCode_3166.contains(upperCaseInput);
  }

  public static void main(String[] args) {
    String input = "Morristown";
    System.out.println(isCity(input));
  }



}
