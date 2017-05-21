package com.sizzler.service;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.common.PtoneShortUrl;

public interface PtoneShortUrlService extends ServiceBaseInterface<PtoneShortUrl, String> {

  /**
   * 根据url获取对应的短链映射记录
   */
  public PtoneShortUrl getByUrl(String url);

}
