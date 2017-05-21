package com.sizzler.common.exception;

public enum ExceptionLevelEnum {
  ERROR_NOTIFY, // 打印error，同时发送报警邮件
  ERROR, // 打印error
  WARN_NOTIFY, // 打印warn，同时发送报警邮件
  WARN; // 打印warn
}
