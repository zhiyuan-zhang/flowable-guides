package com.hki.flowable;


import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;

import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-20 23:32
 * @Description:
 */
public class TaskTest {

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

    // 获取第一个用户的待审批任务
    @Test
    public void queryPersonalTask1() {
        String assignee = "user1";
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService
                .createTaskQuery()
                .taskAssignee(assignee)
                .orderByTaskCreateTime().desc()
                .list();
        for(Task task: taskList) {
            System.out.println("id: " + task.getId());
            System.out.println("name: " + task.getName());
            System.out.println("assignee: " + task.getAssignee());
            // 获取跟随流程的表单
            HistoryService historyService = processEngine.getHistoryService();
            HistoricVariableInstance sheetId = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).variableName("sheetId").singleResult();

        }
    }
    /**
     *
     * 功能描述: 办理任务
     *
     * @param:
     * @return:
     * @auther: ZHANG.HAO
     * @date: 2019-03-29 11:01
     */
    @Test
    public void completeTask() {
        String taskId = "32502";
        TaskService taskService = processEngine.getTaskService();
//      对于执行完的任务，activiti将从act_ru_task表中删除该任务，下一个任务会被插入进来

//      带参数 签收
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("isHelp", "0");
        variableMap.put("user", "admin4");
        taskService.complete(taskId,variableMap);
//      不带参数
        taskService.complete(taskId);
        // 执行完后任务将被派发给下一审批人员或组
    }

    /**
     *
     * 功能描述: 完成任务并附带审批意见 以及查询审批意见
     *
     * @param:
     * @return:
     * @auther: ZHANG.HAO
     * @date: 2019-03-29 10:58
     */
    @Test
    public void completeTaskWithComment() {
        String taskId = "32502";
        TaskService taskService = processEngine.getTaskService();

        Task task = taskService.createTaskQuery()
                .orderByTaskId()
                .taskId(taskId)
                .singleResult();

        taskService.addComment(taskId, task.getProcessInstanceId(), "同意，审批通过");
        taskService.complete(taskId);

        // 查找流程实例的审批意见 #247
        List<Comment> taskComments = taskService.getProcessInstanceComments(task.getProcessInstanceId());
        for(Comment comment: taskComments) {
            System.out.printf("任务: %s, 审批人：%s， 审批意见: %s", comment.getTaskId(), comment.getUserId(), comment.getFullMessage());
        }
        // 执行完后任务将被派发给下一审批人员或组
    }



}
