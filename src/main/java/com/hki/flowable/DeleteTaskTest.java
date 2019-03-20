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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-20 23:23
 * @Description:
 */
public class DeleteTaskTest {

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
    public void testdeletebyTaskId(){
        // 模拟会员申请 表单 定义一个流程单据id，流程单据name
        String sheetId = "54321";
        String sheetName = "lisi";
        // 如果多个key相同的流程定义，会启动version最高的流程
        // 操作数据库的act_ru_execution表,如果是用户任务节点，同时也会在act_ru_task添加一条记录
        // 设置流程变量的时候，会向act_ru_variable表添加数据
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("userid", sheetId);
        variableMap.put("username", sheetName);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("listenerProcess", variableMap);
        //查询这个流程任务
        Task task= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();


        Task updatedTask = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        runtimeService.deleteProcessInstance(updatedTask.getProcessInstanceId(),"流程驳回");

        //查询这个流程任务
        Task task1= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        assertEquals(null,task1);

    }

    @Test
    public void testdeletebyProcessInstanceId(){
        // 模拟会员申请 表单 定义一个流程单据id，流程单据name
        String sheetId = "54321";
        String sheetName = "lisi";
        // 如果多个key相同的流程定义，会启动version最高的流程
        // 操作数据库的act_ru_execution表,如果是用户任务节点，同时也会在act_ru_task添加一条记录
        // 设置流程变量的时候，会向act_ru_variable表添加数据
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("userid", sheetId);
        variableMap.put("username", sheetName);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("listenerProcess", variableMap);

        runtimeService.deleteProcessInstance(processInstance.getId(),"流程驳回");

        //查询这个流程任务
        Task task= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        assertEquals(null,task);

    }

}
