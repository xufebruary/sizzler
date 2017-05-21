package com.sizzler.dao.basic.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.basic.PtoneBasicDictCategoryDao;
import com.sizzler.domain.basic.PtoneBasicDictCategory;

@Repository("ptoneBasicDictCategoryDao")
public class PtoneBasicDictCategoryDaoImpl extends
    DaoBaseInterfaceImpl<PtoneBasicDictCategory, Long> implements PtoneBasicDictCategoryDao {

}
