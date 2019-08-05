package com.hki.flowable;


import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;

/**
 * @Auther: ZHANG.HAO
 * @Date: 2019-03-20 23:15
 * @Description:  部署项目有多种方式  1 流的形式 2 文件的形式
 */
@RunWith(SpringRunner.class)
@SpringBootTest

public class DeployProcess {

    TaskService taskService;

    RuntimeService runtimeService;

    RepositoryService repositoryService;

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
        repositoryService = processEngine.getRepositoryService();

    }
    //BPMN 文件 部署流程

    @Test
    public void deployProcess() throws Exception {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String filePath = "bpmn/myflow.bpmn";
        // 获取输入流
        FileInputStream in = new FileInputStream(filePath);
        // 部署
        repositoryService
                .createDeployment()
                .name("myflowtest")
                .addInputStream("myflow.bpmn", in)
                .deploy();
        // 验证部署是否部署成功
        ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
//        List<ProcessDefinition> pdList = pdq.processDefinitionKey("listenerProcess").list();
        //查询所有流程
        List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionName().asc().orderByProcessDefinitionVersion().asc()
                .orderByProcessDefinitionCategory().asc().list();
        assertNotNull(pdList);
        for (ProcessDefinition pd: pdList) {
            System.out.println("id: " + pd.getId());
            System.out.println("name: " + pd.getName());
            System.out.println("version: " + pd.getVersion());
        }
    }


    @Autowired
    ModelService modelService;

    /**
     * 部署模型
     * @throws Exception
     */
    @Test
    public void deployModel() throws Exception {
        String modelId = "";
        Model model = modelService.getModel(modelId);
        byte[] bpmnBytes = modelService.getBpmnXML(model);

        String processName = model.getName();
        if (!org.apache.commons.lang3.StringUtils.endsWith(processName, ".bpmn20.xml")){
            processName += ".bpmn20.xml";
        }

        Deployment deployment = repositoryService.createDeployment()
                .addBytes(processName, bpmnBytes)
                .name(model.getName())
                .key(model.getKey())
                .deploy();

        System.out.println("流程部署--------deploy：" + deployment);

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).list();


        StringBuilder message = new StringBuilder();
        final String category = "zhxt";
        // 设置流程分类
        for (ProcessDefinition processDefinition : list) {
            if(org.apache.commons.lang3.StringUtils.isNotBlank(category)) {
                repositoryService.setProcessDefinitionCategory(processDefinition.getId(), category);
            }
            message.append("部署成功，流程ID=").append(processDefinition.getId()).append("<br/>");
        }

        if (list.size() == 0){
            message.append("部署失败，没有流程。");
            System.out.println("部署 " + modelId + "，详情：" + message.toString());
        }

        System.out.println("部署 " + modelId + "，详情：" + message.toString());
    }




}
