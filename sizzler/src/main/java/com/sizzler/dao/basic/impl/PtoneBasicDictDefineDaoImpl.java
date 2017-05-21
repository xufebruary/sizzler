package com.sizzler.dao.basic.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.basic.PtoneBasicDictDefineDao;
import com.sizzler.domain.basic.PtoneBasicDictDefine;

@Repository("ptoneBasicDictDefineDao")
public class PtoneBasicDictDefineDaoImpl extends DaoBaseInterfaceImpl<PtoneBasicDictDefine, Long>
    implements PtoneBasicDictDefineDao {

}
