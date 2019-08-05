package com.hki.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-29 11:04
 * @Description:
 */
public class ArgTest {

    TaskService taskService;

    RuntimeService runtimeService;

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
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
    }


    @Test
    public void deploy(){
        // 模拟会员申请 表单 定义一个流程单据id，流程单据name
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("userid", "123");
        variableMap.put("username", "zhangsan");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("listenerProcess", variableMap);
        //查询这个流程任务
        Task task= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        Map<String, Object> processVariables = processInstance.getProcessVariables();


    }

    @Test
    public void deployWithBusinessKey(){
        // 模拟会员申请 表单 定义一个流程单据id，流程单据name
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("listenerProcess", "businessKey");
        //查询这个流程任务
        Task task= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        String businessKey = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult().getBusinessKey();
        assertEquals("businessKey",businessKey);
    }

}
