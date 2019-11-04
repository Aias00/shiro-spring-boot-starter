package com.aias.springboot.shiro.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aias.springboot.shiro.properties.ShiroProperties;
import com.aias.springboot.shiro.service.IResourceService;

/**
 * <b>
 *
 * <br>
 * <b>@ClassName:</b> ShiroAutoConfiguration <br>
 * <b>@Date:</b> 2019/10/25 <br>
 *
 * @author <a> liuhy </a><br>
 */
@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroAutoConfiguration {
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private ShiroProperties shiroProperties;
	@Autowired
	private RedisProperties redisProperties;
	@Autowired
	private AuthorizingRealm authorizingRealm;
	@Autowired
	private HashedCredentialsMatcher hashedCredentialsMatcher;

	@Bean
	public SecurityManager securityManager() {
		authorizingRealm.setCredentialsMatcher(hashedCredentialsMatcher);
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(authorizingRealm);
		// 自定义缓存实现 使用redis
		securityManager.setCacheManager(cacheManager());
		// 自定义session管理 使用redis
		securityManager.setSessionManager(sessionManager());
		return securityManager;
	}

	/**
	 * ShiroFilterFactoryBean 处理拦截资源文件问题。
	 * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，因为在
	 * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
	 *
	 * Filter Chain定义说明 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过
	 * 3、部分过滤器可指定参数，如perms，roles
	 *
	 */

	/**
	 * 凭证匹配器 （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
	 * 所以我们需要修改下doGetAuthenticationInfo中的代码; ）
	 * 
	 * @return
	 */
	@Bean(name = "hashedCredentialsMatcher")
	@ConditionalOnProperty(prefix = "shiro.authc", name = "hash-algorithm")
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		// 散列算法:这里使用MD5算法;
		hashedCredentialsMatcher.setHashAlgorithmName(
				shiroProperties.getAuthc().getHashAlgorithm());
		// 散列的次数，比如散列两次，相当于md5(md5(""));
		hashedCredentialsMatcher.setHashIterations(
				shiroProperties.getAuthc().getHashIterations());
		hashedCredentialsMatcher.setStoredCredentialsHexEncoded(
				shiroProperties.getAuthc().isStoredCredentialsHexEncoded());
		return hashedCredentialsMatcher;
	}

	/**
	 * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
	 * 
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	/**
	 * 配置shiro redisManager 使用的是shiro-redis开源插件
	 * 
	 * @return
	 */
	public RedisManager redisManager() {
		RedisManager redisManager = new RedisManager();
		redisManager.setHost(redisProperties.getHost());
		redisManager.setPort(redisProperties.getPort());
		redisManager.setDatabase(redisProperties.getDatabase());
		redisManager.setTimeout(60 * 1000);
		if (redisProperties.getPassword() != null) {
			redisManager.setPassword(redisProperties.getPassword());
		}
		return redisManager;
	}

	/**
	 * cacheManager 缓存 redis实现 使用的是shiro-redis开源插件
	 * 
	 * @return
	 */
	public RedisCacheManager cacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		redisCacheManager.setRedisManager(redisManager());
		return redisCacheManager;
	}

	/**
	 * RedisSessionDAO shiro sessionDao层的实现 通过redis 使用的是shiro-redis开源插件
	 */
	@Bean
	public RedisSessionDAO redisSessionDAO() {
		RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
		redisSessionDAO.setRedisManager(redisManager());
		return redisSessionDAO;
	}

	/**
	 * shiro session的管理
	 */
	@Bean
	public DefaultWebSessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionDAO(redisSessionDAO());
		return sessionManager;
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/login.html");
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/");
		// 未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		// 拦截器.
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
		// <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		// 自定义加载权限资源关系
		List<String> resourceList = resourceService.queryAll();
		if (resourceList != null && !resourceList.isEmpty()) {
			for (String resource : resourceList) {
				if (StringUtils.isNotEmpty(resource)) {
					String permission = "perms[" + resource + "]";
					filterChainDefinitionMap.put(resource, permission);
				}
			}
		}
		// 配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
//		filterChainDefinitionMap.put("/user/login", "anon");
//		filterChainDefinitionMap.put("/logout", "logout");
//		filterChainDefinitionMap.put("/resources/**", "anon");
//		filterChainDefinitionMap.put("/password/**", "anon");
//		filterChainDefinitionMap.put("/password.html", "anon");
//
//		filterChainDefinitionMap.put("/captcha.jpg", "anon");
//		filterChainDefinitionMap.put("/favicon.ico", "anon");
//		filterChainDefinitionMap.put("/favi.ico", "anon");
//
//		filterChainDefinitionMap.put("/login/sendCode", "anon");
//		filterChainDefinitionMap.put("/login/valid.json", "anon");
		filterChainDefinitionMap.putAll(shiroProperties.getFilter().getFilterChainMap());
		filterChainDefinitionMap.put("/**", "authc");
		shiroFilterFactoryBean
				.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}
}
