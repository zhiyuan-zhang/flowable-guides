package com.hki.flowable;

import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-22 22:22
 * @Description: 监听器 和 条件判断 走流程 都在当前测试类中
 */
public class HandlerTest {

    private ProcessEngine processEngine = null;
    // 初始化流程引擎
    @Before
    public void createDeployment() {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration();
        cfg.setJdbcDriver("com.mysql.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306/flowable?useUnicode=true&characterEncoding=utf8&useSSL=false");
        cfg.setJdbcUsername("root");
        cfg.setJdbcPassword("root");
        cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngine = cfg.buildProcessEngine();
    }

    // 部署流程
    @Test
    public void deployProcess() throws Exception {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String filePath = "bpmn/handlertest.bpmn";
        // 获取输入流
        FileInputStream in = new FileInputStream(filePath);
        // 部署
        repositoryService
                .createDeployment()
                .name("监听器流程-0332")
                .addInputStream("handlertest.bpmn", in)
                .deploy();
        // 验证部署是否部署成功
        ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> pdList = pdq.processDefinitionKey("handlerProcess").list();
        assertNotNull(pdList);
        for (ProcessDefinition pd: pdList) {
            System.out.println("id: " + pd.getId());
            System.out.println("name: " + pd.getName());
            System.out.println("version: " + pd.getVersion());
            System.out.println("deploymentId: " + pd.getDeploymentId());
        }
    }
    // 启动流程实例
    @Test
    public void startProcess() {
        String sheetId = "12345";
        String sheetName = "listener-1";
        String startUser = "lijianxun";
        // 指定AuthenticatedUserId后，启动流程 startUser 字段会有值
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(startUser);
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("sheetId", sheetId);
        variableMap.put("sheetName", sheetName);
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("handlerProcess", sheetId, variableMap);
        assertNotNull(pi);
        System.out.println("pid: " + pi.getId()
                + " ,activitiId: " + pi.getActivityId()
                + " ,pdId: " + pi.getProcessDefinitionId()
                + " ,startUser: " + pi.getStartUserId()
                + " ,businessKey: " + pi.getBusinessKey()
        );
    }
    //查找单据提交后的第一个用户 来获取taskid
    @Test
    public void queryPersonalTask() {
        String assignee = "admin2";
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService
                .createTaskQuery()
                .taskAssignee(assignee)
                .orderByTaskCreateTime().desc()
                .list();
        for(Task task: taskList) {
            System.out.println("pId:" + task.getProcessInstanceId());
            System.out.println("task _ id: " + task.getId());
//            processEngine.getRuntimeService().deleteProcessInstance(task.getProcessInstanceId(),"流程驳回");

            System.out.println("name: " + task.getName());
            System.out.println("crateTime: " + task.getCreateTime());
            System.out.println("assignee: " + task.getAssignee());
        }
    }
    // 当用户处理任务的时候传参数 isHelp == 0 则不走监听器  isHelp == 1 则触发监听器 执行
    // 注意 流程图的判断条件在网关之后的分叉线上设置

    @Test
    public void completeTask() {
        String taskId = "01db207c-4c85-11e9-8e90-1a286c31b420";
        TaskService taskService = processEngine.getTaskService();
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("isHelp", "0");
        variableMap.put("user", "admin5");
        taskService.complete(taskId,variableMap);
    }
}
