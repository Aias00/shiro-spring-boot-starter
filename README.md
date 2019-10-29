# shiro-spring-boot-starter
开源实现shiro-spring-boot-starter

## 使用方式
    1.下载到本地进行编译
    2.将依赖添加到依赖工程
     `<dependency>
            <groupId>com.aias.springboot</groupId>
            <artifactId>shiro-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
     `
    3.在目标工程的配置文件种添加以下配置
   ` spring:
  redis:
    host: localhost
    port: 6379
    timeout: 5000
    database: 2
shiro:
  authc:
    credential:
      hash-algorithm: SHA-256
      hash-iterations: 1024
      stored-credentials-hex-encoded: false`
      
     4.自定义MyRealm  extends AuthorizingRealm