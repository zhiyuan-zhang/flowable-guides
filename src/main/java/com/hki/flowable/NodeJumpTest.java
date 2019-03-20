package com.hki.flowable;

import lombok.extern.log4j.Log4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-18 17:59
 * @Description:
 */


@Log4j
public class NodeJumpTest {

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

    // 跳转到开始节点
    @Test
    public void test() {
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



        System.out.println("id: " + task.getId());
        System.out.println("name: " + task.getName());
        System.out.println("crateTime: " + task.getCreateTime());
        System.out.println("assignee: " + task.getAssignee());
        System.out.println("getProcessInstanceId "+task.getProcessInstanceId());
        System.out.println("getProcessDefinitionId "+task.getProcessDefinitionId());
        //[ 完成 ]流程
        taskService.complete(task.getId());
        //查询这个流程任务
        Task task2= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();


        System.out.println("id: " + task2.getId());
        System.out.println("name: " + task2.getName());
        System.out.println("crateTime: " + task2.getCreateTime());
        System.out.println("assignee: " + task2.getAssignee());
        System.out.println("getProcessInstanceId "+task2.getProcessInstanceId());
        System.out.println("getProcessDefinitionId "+task2.getProcessDefinitionId());
        //再次[ 完成 ]流程
        taskService.complete(task2.getId());

        //流程查询
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(1, tasks.size());

        //流程任务
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().list();
        // assertEquals(1, executions.size());



        List<Task> tasks1 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        List<String> currentActivityIds = new ArrayList<>();
        tasks1.forEach(t -> currentActivityIds.add(t.getTaskDefinitionKey()));

        for(Task task1: tasks1) {
            System.out.println("id: " + task1.getId());
            System.out.println("name: " + task1.getName());
            System.out.println("crateTime: " + task1.getCreateTime());
            System.out.println("assignee: " + task1.getAssignee());
            System.out.println("getProcessInstanceId "+task1.getProcessInstanceId());
            System.out.println("getProcessDefinitionId "+task1.getProcessDefinitionId());
        }

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstance.getId())
                .moveActivityIdsToSingleActivityId(currentActivityIds, "startevent1")
                .changeState();

        //查询这个流程任务
        Task startTask= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        System.out.println("id: " + startTask.getId());
        System.out.println("name: " + startTask.getName());
        System.out.println("crateTime: " + startTask.getCreateTime());
        System.out.println("assignee: " + startTask.getAssignee());
        System.out.println("getProcessInstanceId "+startTask.getProcessInstanceId());
        System.out.println("getProcessDefinitionId "+startTask.getProcessDefinitionId());

        System.out.println("ProcessDefinition END");
    }


    // 跳转到任意节点
    @Test
    public void test1() {
        //启动一个流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("listenerProcess");
        //查询这个流程任务
        Task task= taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();


        System.out.println("id: " + task.getId());
        System.out.println("name: " + task.getName());
        System.out.println("crateTime: " + task.getCreateTime());
        System.out.println("assignee: " + task.getAssignee());
        System.out.println("getProcessInstanceId "+task.getProcessInstanceId());
        System.out.println("getProcessDefinitionId "+task.getProcessDefinitionId());
        //[ 完成 ]流程
        taskService.complete(task.getId());

        //流程查询
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(1, tasks.size());

        //流程任务
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().list();
        assertEquals(1, executions.size());

//
//        String assignee = "admin";
//        List<Task> taskList = taskService
//                .createTaskQuery()
//                .taskAssignee(assignee)
//                .orderByTaskCreateTime().desc()
//                .list();
        List<Task> tasks1 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        List<String> currentActivityIds = new ArrayList<>();
        tasks1.forEach(t -> currentActivityIds.add(t.getTaskDefinitionKey()));

        for(Task task1: tasks1) {
            System.out.println("id: " + task1.getId());
            System.out.println("name: " + task1.getName());
            System.out.println("crateTime: " + task1.getCreateTime());
            System.out.println("assignee: " + task1.getAssignee());
            System.out.println("getProcessInstanceId "+task1.getProcessInstanceId());
            System.out.println("getProcessDefinitionId "+task1.getProcessDefinitionId());
        }
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstance.getId())
                .moveActivityIdsToSingleActivityId(currentActivityIds, "sid-E4D84D41-6C1C-4EA1-B35A-896C6E5655C2")
                .changeState();

        System.out.println("ProcessDefinition END");


    }



}
