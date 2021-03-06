package com.roytrack.spring.core.transaction;


import com.roytrack.spring.core.transaction.model.AddressModel;
import com.roytrack.spring.core.transaction.model.UserModel;
import com.roytrack.spring.core.transaction.service.IAddressService;
import com.roytrack.spring.core.transaction.service.IUserService;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


public class TransactionTest {
   
    private static ApplicationContext ctx;
    private static PlatformTransactionManager txManager;
    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    
    
    //id自增主键从0开始
    private static final String CREATE_TABLE_SQL = "create table test" +
    "(id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
    "name varchar(100))";
    private static final String DROP_TABLE_SQL = "drop table test";

    
    private static final String CREATE_USER_TABLE_SQL = "create table user" +
    "(id int , " +
    "name varchar(100))";

    private static final String DROP_USER_TABLE_SQL = "drop table user";
    
    private static final String CREATE_ADDRESS_TABLE_SQL = "create table address" +
    "(id int, " +
    "province varchar(100), city varchar(100), street varchar(100), user_id int)";
    
    private static final String DROP_ADDRESS_TABLE_SQL = "drop table address";

    
    private static final String INSERT_SQL = "insert into test(name) values(?)";
    private static final String COUNT_SQL = "select count(*) from test";
    
    @BeforeClass
    public static void setUpClass() {
        String[] configLocations = new String[] {
                "classpath:transaction/applicationContext-resources.xml",
                "classpath:transaction/applicationContext-jdbc.xml"};
        ctx = new ClassPathXmlApplicationContext(configLocations);
        txManager = ctx.getBean(PlatformTransactionManager.class);
        dataSource = ctx.getBean(DataSource.class);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    
    @Test
    public void testServiceTransaction() {
        String[] configLocations = new String[] {
        "classpath:transaction/applicationContext-resources.xml",
        "classpath:transaction/applicationContext-jdbc.xml",
        "classpath:transaction/applicationContext-service.xml"};
        ApplicationContext ctx2 = new ClassPathXmlApplicationContext(configLocations);
        
        DataSource dataSource2 = ctx2.getBean(DataSource.class);
        JdbcTemplate jdbcTemplate2 = new JdbcTemplate(dataSource2);
        jdbcTemplate2.update(CREATE_USER_TABLE_SQL);
        jdbcTemplate2.update(CREATE_ADDRESS_TABLE_SQL);
        
        IUserService userService = ctx2.getBean("userService", IUserService.class);
        IAddressService addressService = ctx2.getBean("addressService", IAddressService.class);
        UserModel user = createDefaultUserModel();
        
        userService.save(user);
        Assert.assertEquals(1, userService.countAll());
        Assert.assertEquals(1, addressService.countAll());
        
        
        jdbcTemplate2.update(DROP_USER_TABLE_SQL);
        jdbcTemplate2.update(DROP_ADDRESS_TABLE_SQL);
    }
    

    
    
    private void prepareTable(JdbcTemplate jdbcTemplate2) {
        jdbcTemplate2.update(CREATE_USER_TABLE_SQL);
        jdbcTemplate2.update(CREATE_ADDRESS_TABLE_SQL);
    }
    
    private void cleanTable(JdbcTemplate jdbcTemplate2) {
        jdbcTemplate2.update(DROP_USER_TABLE_SQL);
        jdbcTemplate2.update(DROP_ADDRESS_TABLE_SQL);        
    }
    
    private UserModel createDefaultUserModel() {
        UserModel user = new UserModel();
        user.setName("test");
        AddressModel address = new AddressModel();
        address.setProvince("beijing");
        address.setCity("beijing");
        address.setStreet("haidian");
        user.setAddress(address);
        return user;
    }

    
}
