package com.sizzler.dexcoder.exceptions;

import com.sizzler.dexcoder.enums.IEnum;

/**
 * 自定义异常类
 * 
 */
public class DexcoderException extends RuntimeException {

  private static final long serialVersionUID = -6618870405063770962L;

  /** Exception code */
  protected String resultCode = "UN_KNOWN_EXCEPTION";

  /** Exception message */
  protected String resultMsg = "未知异常";

  /**
   * Constructor
   */
  public DexcoderException() {
    super();
  }

  /**
   * Instantiates a new DexcoderException.
   * 
   * @param e
   *          the e
   */
  public DexcoderException(IEnum e) {
    super(e.getDesc());
    this.resultCode = e.getCode();
    this.resultMsg = e.getDesc();
  }

  /**
   * Instantiates a new DexcoderException.
   * 
   * @param e
   *          the e
   */
  public DexcoderException(Throwable e) {
    super(e);
    this.resultMsg = e.getMessage();
  }

  /**
   * Constructor
   * 
   * @param message
   *          the message
   */
  public DexcoderException(String message) {
    super(message);
    this.resultMsg = message;
  }

  /**
   * Constructor
   * 
   * @param code
   *          the code
   * @param message
   *          the message
   */
  public DexcoderException(String code, String message) {
    super(message);
    this.resultCode = code;
    this.resultMsg = message;
  }

  public String getResultCode() {
    return resultCode;
  }

  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public String getResultMsg() {
    return resultMsg;
  }

  public void setResultMsg(String resultMsg) {
    this.resultMsg = resultMsg;
  }
}
