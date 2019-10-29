package com.aias.test.shirotest.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aias.springboot.shiro.service.IResourceService;

/**
 * <b>
 *
 * <br>
 * <b>@ClassName:</b> ShiroCOnfig <br>
 * <b>@Date:</b> 2019/10/29 <br>
 *
 * @author <a> liuhy </a><br>
 */
@Configuration
public class ShiroConfig {
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private HashedCredentialsMatcher hashedCredentialsMatcher;

	@Bean
	public MyRealm myShiroRealm() {
		MyRealm myShiroRealm = new MyRealm();
		myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher);
		return myShiroRealm;
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
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

		// 配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
		filterChainDefinitionMap.put("/user/login", "anon");
		filterChainDefinitionMap.put("/logout", "logout");
		filterChainDefinitionMap.put("/resources/**", "anon");
		filterChainDefinitionMap.put("/password/**", "anon");
		filterChainDefinitionMap.put("/password.html", "anon");

		filterChainDefinitionMap.put("/captcha.jpg", "anon");
		filterChainDefinitionMap.put("/favicon.ico", "anon");
		filterChainDefinitionMap.put("/favi.ico", "anon");

		filterChainDefinitionMap.put("/login/sendCode", "anon");
		filterChainDefinitionMap.put("/login/valid.json", "anon");

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
		filterChainDefinitionMap.put("/**", "authc");
		shiroFilterFactoryBean
				.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}
}
