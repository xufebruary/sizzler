package com.sizzler.service;

/**
 * @ClassName: WebsocketSessionManagerService
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2016/4/18
 * @author: zhangli
 */
public interface WebSocketSessionManagerService {

  public abstract void tellMessage(String sessionKey, Object message);

}
