package com.sizzler.dao.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.UserDao;
import com.sizzler.domain.user.PtoneUser;

@Repository("userDao")
public class UserDaoImpl extends DaoBaseInterfaceImpl<PtoneUser, String> implements UserDao {

}
