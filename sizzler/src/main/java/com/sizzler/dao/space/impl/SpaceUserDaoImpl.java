package com.sizzler.dao.space.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.space.SpaceUserDao;
import com.sizzler.domain.space.PtoneSpaceUser;

@Repository("spaceUserDao")
public class SpaceUserDaoImpl extends DaoBaseInterfaceImpl<PtoneSpaceUser, Long> implements
    SpaceUserDao {

}
