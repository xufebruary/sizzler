package com.sizzler.dao.basic.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.basic.PtoneBasicDictItemDao;
import com.sizzler.domain.basic.PtoneBasicDictItem;

@Repository("ptoneBasicDictItemDao")
public class PtoneBasicDictItemDaoImpl extends DaoBaseInterfaceImpl<PtoneBasicDictItem, Long>
    implements PtoneBasicDictItemDao {

}
