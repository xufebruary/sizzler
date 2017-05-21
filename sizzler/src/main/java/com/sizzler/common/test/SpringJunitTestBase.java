package com.sizzler.common.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 对基于spring所管理的项目进行单元测试所需要的父类
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 指定使用Junit4框架进行测试
// @ContextConfiguration(locations =
// "classpath:spring/applicationContext.xml")//指定所需要加载的配置文件，各个模块需要继承该类来分别指定配置文件的位置
// 这个非常关键，如果不加入这个注解配置，事务控制就会完全失效！
// @Transactional
// 这里的事务关联到配置文件中的事务控制器（transactionManager = "transactionManager"），同时指定自动回滚（defaultRollback =
// true）。这样做操作的数据才不会污染数据库！
// @TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class SpringJunitTestBase {

}
