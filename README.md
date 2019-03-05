# flowable-guides
本工程为 flowable 的一些简单示例，

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
如果是绕过去到话那么参考这个文档
[https://gitee.com/flowable-project/springboot-flowable-ui/blob/master/src/main/java/com/fxtcn/platform/filter/CustomHandlerInterceptor.java]

其中主要代码是在这里 

if (servletPath.startsWith("/app")) {
			User user = new UserEntityImpl();
			user.setId("admin");
			SecurityUtils.assumeUser(user);
}




