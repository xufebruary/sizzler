package com.sizzler.system.api.config;

public enum CustomHttpStatus {

  INTERNAL_INTERFACE_ERROR(450, "Internal interface error");

  private final int value;

  private final String reasonPhrase;

  private CustomHttpStatus(int value, String reasonPhrase) {
    this.value = value;
    this.reasonPhrase = reasonPhrase;
  }

  public int getValue() {
    return value;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }

}
