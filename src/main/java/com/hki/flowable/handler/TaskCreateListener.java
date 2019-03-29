package com.hki.flowable.handler;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.io.Serializable;

// TaskCreate task: Task[id=35008, name=李建勋]TaskCreate notifyType: emailTaskCreate task id: 35008TaskCreate execution id: 35005TaskCreate event name: create, 审批人：lijianxun
@Component
public class TaskCreateListener implements TaskListener, Serializable {
    private Expression notifyType;
    private Expression task;
    public void notify(DelegateTask delegateTask) {
        try {

            System.out.println("---------------TaskCreateListener----------------");
//            System.out.printf("TaskCreate task: %s \n", task.getValue(delegateTask));
//            System.out.printf("TaskCreate notifyType: %s \n", notifyType.getValue(delegateTask));
//            System.out.printf("TaskCreate task id: %s \n", delegateTask.getId());
//            System.out.printf("TaskCreate execution id: %s \n", delegateTask.getExecutionId());
//            System.out.printf("TaskCreate event name: %s, 审批人：%s \n", delegateTask.getEventName(), delegateTask.getAssignee());
//            System.out.println("---------------TaskCreateListener----------------");
            Object key = delegateTask.getVariable("key");
            if (null != key)
                delegateTask.setAssignee(key.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Expression getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(Expression notifyType) {
        this.notifyType = notifyType;
    }

    public Expression getTask() {
        return task;
    }

    public void setTask(Expression task) {
        this.task = task;
    }
}
