package com.hki.flowable;


import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;


/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-20 23:32
 * @Description:
 */
public class GroupTaskTest {

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

    //根据BusinessKey查询组待处理任务
    @Test
    public void findGroupbyId(){
        List<Task> list = taskService
                .createTaskQuery().processInstanceBusinessKey("44813770").list();
        list.stream().forEach(task -> {
            List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
            if(identityLinksForTask.size() == 1){
                System.out.println(""+ identityLinksForTask.get(0).getGroupId());
            }
            // System.out.println(""+ identityLinksForTask.get(0).getGroupId());
        });

    }

    /**
     * 批量签收组任务
     * @param taskId
     * @param assignee
     * @return
     */
    @Test
    public void claimCount(String [] taskId,String assignee) {

        List<String> taskList = Arrays.asList(taskId);

        taskList.stream().parallel().forEach(taskid -> {
            taskService.claim(taskid, assignee);

        });

    }


        /**
         * 带条件模糊查询获取多个用户的多个流程的组待审批任务
         */
    public void usersApprove() {

        String [] definitionKeys = {"work"};
        String [] assignee = {"zhangsan"};
        String [] key = {"name"};
        String [] value = {"lisi"};
        String [] keyLike = {"%name%"};
        String [] valueLike = {"%lisi%"};
        int firstResult= 1;
        int maxResults = 10;
        TaskQuery desc = taskService
                .createTaskQuery()
                .taskCandidateGroupIn(Arrays.asList(assignee))
                .processDefinitionKeyIn(Arrays.asList(definitionKeys));



        for (int i = 0; !ObjectUtils.isEmpty(key) && i < key.length; i++) {
            desc.processVariableValueEquals(key[i],value[i]);
        }



        if(!ObjectUtils.isEmpty(keyLike) ){
            desc.or();
        }
        for (int i = 0; !ObjectUtils.isEmpty(keyLike) && i < keyLike.length; i++) {
            desc.processVariableValueLike(keyLike[i],valueLike[i]);
        }
        if(!ObjectUtils.isEmpty(keyLike) ){
            desc.endOr();
        }

        desc.orderByTaskCreateTime().desc();
        List<Task> taskList = desc.listPage(firstResult == 1 ? 0 : (firstResult - 1) * maxResults, maxResults);


    }





}
