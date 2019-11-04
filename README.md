# shiro-spring-boot-starter
开源实现shiro-spring-boot-starter

快速启动:

1.将工程下载到本地进行编译(install),编译通过后在本地仓库会生成shiro-spring-boot-starter包
2.将依赖添加到依赖工程

```xml
 	<dependency>
        <groupId>com.aias.springboot</groupId>
        <artifactId>shiro-spring-boot-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
  	</dependency>
```

3.在需要调用shiro的工程application.yml中添加以下配置

```yml
shiro:
  authc:
    hash-algorithm: SHA-256
    hash-iterations: 1024
    stored-credentials-hex-encoded: false
  filter:
    login-url: /login
    success-url: /
    unauthorized-url: /403
    filter-chain-map:
      /user/login : anon
      /logout : anon
      /resources/** : anon
      /password/** : anon
      /password.html : anon
      /captcha.jpg : anon
      /favicon.ico : anon
      /favi.ico : anon
      /login/sendCode : anon
      /login/valid.json : anon

```

 4.需要调用shiro的工程中自定义MyRealm继承自AuthorizingRealm,自定义资源管理服务ResourceService实现com.aias.springboot.shiro.service.IResourceService

