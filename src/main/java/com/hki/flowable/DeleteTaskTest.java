package com.hki.flowable;

import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-20 23:23
 * @Description:  驳回任务分为驳回到 申请人 和驳回到上一级 当前测试类是驳回至申请人
 */

public class DeleteTaskTest {

    TaskService taskService;

    RuntimeService runtimeService;

    HistoryService historyService;

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
        historyService = processEngine.getHistoryService();
    }


    @Test
    public void testdeletebyTaskId(){
        // 模拟会员申请 表单 定义一个流程单据id，流程单据name
        String sheetId = "54321";
        String sheetName = "lisi";
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("userid", sheetId);
        variableMap.put("username", sheetName);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("listenerProcess", variableMap);
        //查询这个流程任务
        Task task= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        Task updatedTask = taskService.createTaskQuery().taskId(task.getId()).singleResult();

        if(ObjectUtils.isEmpty(updatedTask)){
            System.out.println("任务没找到  ");
        }else{
            runtimeService.suspendProcessInstanceById(updatedTask.getProcessInstanceId());
            runtimeService.deleteProcessInstance(updatedTask.getProcessInstanceId(),"流程驳回");
            historyService.deleteHistoricProcessInstance(updatedTask.getProcessInstanceId());
            System.out.println("任务被驳回  "+updatedTask.getProcessInstanceId());
            assertEquals(true,task);
        }

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

        // 驳回其实也就是删除当前任务  然后消息通知对方 如果觉得做的简单也可以加个邮件通知节点,
        runtimeService.deleteProcessInstance(processInstance.getId(),"流程驳回");

        //查询这个流程任务
        Task task= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        assertEquals(null,task);

    }


}
