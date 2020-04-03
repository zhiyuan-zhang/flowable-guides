# flowable-guides

本工程为 flowable 的一些简单示例，

目前项目进度

BPMN文件部署流程 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/DeployProcess.java))

流方式部署流程  ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/DeployProcess.java))

用户相关操作 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/UserTest.java))

完成任务([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/TaskTest.java))

删除任务 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/DeleteTaskTest.java))

查询待处理任务 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/TaskTest.java))

任意节点跳转 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/NodeJumpTest.java))

开始节点跳转 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/NodeJumpTest.java))

驳回任务 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/DeleteTaskTest.java))

互斥网关 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/HandlerTest.java))

事件监听 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/HandlerTest.java))

审批历史  ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/HistoryTest.java))

根据BusinessKey查询组待处理任务 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/GroupTaskTest.java))

带条件模糊查询获取多个用户的多个流程的组待审批任务 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/GroupTaskTest.java))

审批意见支持  ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/TaskTest.java))

获取fromkey参数  ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/ArgTest.java))

获取流程附带参数  ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/ArgTest.java))

会签 

撤回

委派和转交 ([ 完成 ](https://github.com/zhiyuan-zhang/flowable-guides/blob/master/src/main/java/com/hki/flowable/TaskTest.java))


***

flowable 分为两部分 
一部分是 画流程(主要是BPMN2.0之类的流程图) 文件格式多是xml结尾或者bpmn2.0 结尾的
剩下的是 调用接口来完成相对应的工作

所以在项目开始之前我们先要想清楚 

流程图是否需要集成到我们项目中 ?

如果不需要  

那么直接部署war包来在tomcat中启动 然后在下载下来你画好的流程图 然后在部署到自己的项目中

war包地址下载[https://www.flowable.org/]

下载后做以下几步

1.找到wars文件夹将里面的所有war包都放入tomcat webapps目录

2.解压后修改每个包里面的配置文件

3.启动tomcat 查看日志 是否有报错然后在看看哪里配置错了 


如果需要那么需要考虑以下几个东西

因为flowable自己有一套自己的idm权限那么绕过去 要么集成到自己项目中

如果是绕过去到话那么参考这个项目
[git地址](https://gitee.com/flowable-project/springboot-flowable-ui/blob/master/src/main/java/com/fxtcn/platform/filter/CustomHandlerInterceptor.java)

其中主要代码是在这里 绕过了idm权限

```
if (servletPath.startsWith("/app")) {
			User user = new UserEntityImpl();
			user.setId("admin");
			SecurityUtils.assumeUser(user);
}
```
如果是集成到自己项目中 
参考
[git地址](https://www.cnblogs.com/liuwenjun/p/10291210.html)

流程图还有其他打开方式

1.问 eclipse怎么画流程图

答 下载bpmn插件 

2.问 idea怎么画流程图

答 下载bpmn插件 

3.问 都不想用怎么画流程图 我们是前后端分离的怎么办

答 去了解 bpmn.js 

4.问 驳回到审批人怎么处理

答 一般我是邮件或者消息通知 然后把当前任务关闭或者删除

5.问 有没有视频或者教程

答 目前还没计划要出

6. 我画好图后 不想用文件部署还有其他办法么

答 很多种方式 1 后台找到bpmn.xml文件传入参数部署,2 用文件流的形式model部署












