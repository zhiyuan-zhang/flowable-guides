package com.hki.flowable;


import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;



/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-20 23:06
 * @Description:
 */
public class UserTest {

        private ProcessEngine processEngine = null;
        // 初始化流程引擎
        @Before
        public void createDeployment() {
            ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration();
            cfg.setJdbcDriver("com.mysql.jdbc.Driver");
            cfg.setJdbcUrl("jdbc:mysql://localhost:3306/flowable?nullCatalogMeansCurrent=true&useUnicode=true&characterEncoding=utf8&useSSL=false");
            cfg.setJdbcUsername("root");
            cfg.setJdbcPassword("root");
            cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
            processEngine = cfg.buildProcessEngine();
        }
        // 添加用户和组
        @Test
        public void addGroupAndUser() {
            IdentityService identityService = processEngine.getIdentityService();
            addSysUser(identityService);
        }

    public void addSysUser(IdentityService identityService) {
            //创建组
        Group developGroup = processEngine.getIdentityService().newGroup("manager");
        developGroup.setName("系统管理组");
        developGroup.setType("assignee");
        identityService.saveGroup(developGroup);
        for (int i = 0 ;i < 10; i++){
            // 创建用户
            User user3 = identityService.newUser("admin"+ i);
            user3.setFirstName("admin"+ i);
            user3.setLastName("");
            user3.setEmail("admin@abcd.com");
            identityService.saveUser(user3);
            // 用户加入组

            identityService.createMembership("user"+ i, "manager");
            User user = identityService.createUserQuery().userId("admin" + i).singleResult();

            System.out.println("id: " + user.getId());
        }
    }

}
