package com.sizzler.service.impl;

import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.domain.user.PtoneUserBasicSetting;
import com.sizzler.service.UserSettingService;

@Service
public class UserSettingServiceImpl extends ServiceBaseInterfaceImpl<PtoneUserBasicSetting, String>
    implements UserSettingService {
}
