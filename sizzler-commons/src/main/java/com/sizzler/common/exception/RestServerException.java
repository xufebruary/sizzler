package com.sizzler.common.exception;

public class RestServerException extends Exception {

  private static final long serialVersionUID = 4788973024691735750L;

  public RestServerException() {
    super();
  }

  public RestServerException(String msg) {
    super(msg);
  }

  public RestServerException(Throwable throwable) {
    super(throwable);
  }

  public RestServerException(String msg, Throwable throwable) {
    super(msg, throwable);
  }

  public void printStackTrace() {
    super.printStackTrace();
  }

}
