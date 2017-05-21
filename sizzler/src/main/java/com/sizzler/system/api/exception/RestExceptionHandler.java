package com.sizzler.system.api.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sizzler.common.exception.BusinessException;
import com.sizzler.system.Constants;
import com.sizzler.system.api.common.ResponseResult;
import com.sizzler.system.api.common.RestResultGenerator;

@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

  /**
   * 统一的rest接口异常处理器
   * 
   * @param e
   *          捕获的异常
   * @return 异常信息
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
  private <T> ResponseResult<T> globalExceptionHandler(HttpServletRequest request, Exception e) {

    logCommonException(request);
    logger.error("--------->接口异常详细信息:", e);
    return RestResultGenerator.genErrorResult(ResponseErrorEnum.INTERNAL_INTERFACE_ERROR);
  }

  /**
   * 统一的BusinessException异常处理器
   * 
   * @param e
   *          捕获的异常
   * @return 异常信息
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(BusinessException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
  private <T> ResponseResult<T> businessExceptionHandler(HttpServletRequest request,
      BusinessException e) {

    logCommonException(request);
    logger.error("--------->Business接口调用异常!", e);
    return RestResultGenerator.genErrorResult(e);
  }

  /**
   * bean校验未通过异常
   * 
   * @see javax.validation.Valid
   * @see org.springframework.validation.Validator
   * @see org.springframework.validation.DataBinder
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  private <T> ResponseResult<T> illegalParamsExceptionHandler(HttpServletRequest request,
      MethodArgumentNotValidException e) {

    logCommonException(request);
    logger.error("--------->请求参数不合法!", e);
    return RestResultGenerator.genErrorResult(ResponseErrorEnum.ILLEGAL_PARAMS);
  }

  /**
   * 打印错误前后文信息
   * 
   * @param request
   */
  private void logCommonException(HttpServletRequest request) {
    Object body = request.getAttribute(Constants.REQUEST_BODY);
    logger.error("--------->异常请求的URI:" + request.getRequestURI());
    logger.error("--------->请求header token:" + request.getHeader("token"));
    logger.error("--------->异常请求的body:" + body);
    logger.error("--------->异常请求的参数:" + request.getQueryString());
  }

}
