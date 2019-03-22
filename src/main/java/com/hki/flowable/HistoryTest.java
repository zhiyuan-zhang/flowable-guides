package com.hki.flowable;

import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-22 22:31
 * @Description:
 */
public class HistoryTest {
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

    // 历史流程实例查看（查找按照某个规则或流程定义一共执行了多少次流程）
    @Test
    public void queryHistoricProcessInstance() {
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey("listenerProcess")
                .orderByProcessInstanceStartTime()
                .desc()
                .list();
        for (HistoricProcessInstance hpi: hpiList) {
            System.out.println("pId: " + hpi.getId());
            System.out.println("pdId: " + hpi.getProcessDefinitionId());
            System.out.println("startTime: " + hpi.getStartTime());
            System.out.println("endTime: " + hpi.getEndTime());
            System.out.println("duration: " + hpi.getDurationInMillis());
            System.out.println("----------------------------------------");
        }
    }
    // 某一次流程一共经历了多少个活动
    @Test
    public void queryHistoricActivityInstance() {
        String processInstanceId = "30001";
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list();
        for (HistoricActivityInstance hai: haiList) {
            System.out.println("id: " + hai.getId());
            System.out.println("activityId: " + hai.getActivityId());
            System.out.println("taskId: " + hai.getTaskId());
            System.out.println("pId: " + hai.getProcessInstanceId());
            System.out.println("name: " + hai.getActivityName());
            System.out.println("type: " + hai.getActivityType());
            System.out.println("assignee: " + hai.getAssignee());
            System.out.println("startTime: " + hai.getStartTime());
            System.out.println("endTime: " + hai.getEndTime());
            System.out.println("duration: " + hai.getDurationInMillis());
            System.out.println("--------------------------------------");
        }
    }
    // 历史任务查询（某一次流程的执行经历了多少任务节点）// 查询某个流程的历史记录
    @Test
    public void queryHistoricTask() {
        String processInstanceId = "30001";
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list();
        for (HistoricTaskInstance hti: htiList) {
            System.out.println("--------------------------------------");
            System.out.println("taskId: " + hti.getId());
            System.out.println("name: " + hti.getName());
            System.out.println("pId: " + hti.getProcessInstanceId());
            System.out.println("pdId: " + hti.getProcessDefinitionId());
            System.out.println("workTime: " + hti.getWorkTimeInMillis());
            System.out.println("assignee: " + hti.getAssignee());
            System.out.println("startTime: " + hti.getStartTime());
            System.out.println("endTime: " + hti.getEndTime());
            System.out.println("duration: " + hti.getDurationInMillis());
        }
    }
    // 某一次流程执行时设置的流程变量
    @Test
    public void queryHistoricVariables() {
        String processInstanceId = "2501";
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricVariableInstance> hviList = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByVariableName()
                .asc()
                .list();
        for (HistoricVariableInstance hvi: hviList) {
            System.out.println("--------------------------------------");
            System.out.println("id: " + hvi.getId());
            System.out.println("pId: " + hvi.getProcessInstanceId());
            System.out.println("variableName: " + hvi.getVariableName());
            System.out.println("variableType: " + hvi.getVariableTypeName());
            System.out.println("variableValue: " + hvi.getValue());
        }
    }
    // 通过流程变量查询历史流程实例
    @Test
    public void queryProcessInstanceByVariable() {
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery()
                .variableValueEquals("sheetId", "12345")
                .orderByProcessInstanceStartTime()
                .desc()
                .list();
        for (HistoricProcessInstance hpi: hpiList) {
            System.out.println("pId: " + hpi.getId());
            System.out.println("pdId: " + hpi.getProcessDefinitionId());
            System.out.println("businessKey: " + hpi.getBusinessKey());
            System.out.println("startTime: " + hpi.getStartTime());
            System.out.println("endTime: " + hpi.getEndTime());
            System.out.println("duration: " + hpi.getDurationInMillis());
            System.out.println("----------------------------------------");
        }
    }
}
