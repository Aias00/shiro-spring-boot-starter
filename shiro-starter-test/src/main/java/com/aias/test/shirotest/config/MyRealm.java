package com.aias.test.shirotest.config;

import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aias.test.shirotest.User;
import com.aias.test.shirotest.service.PermService;
import com.aias.test.shirotest.service.ResourcesService;
import com.aias.test.shirotest.service.UserService;

/**
 * <b>
 *
 * <br>
 * <b>@ClassName:</b> MyRealm <br>
 * <b>@Date:</b> 2019/10/29 <br>
 *
 * @author <a> liuhy </a><br>
 */
//@Component
public class MyRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;
	@Autowired
	private ResourcesService roleService;
	@Autowired
	private PermService permService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String username = upToken.getUsername();

		// Null username is invalid
		if (username == null) {
			throw new AccountException(
					"Null usernames are not allowed by this realm.");
		}

		User userDB = userService.findUserByName(username);

		if (userDB == null) {
			throw new UnknownAccountException(
					"No account found for admin [" + username + "]");
		}

		// 查询用户的角色和权限存到SimpleAuthenticationInfo中，这样在其它地方
		// SecurityUtils.getSubject().getPrincipal()就能拿出用户的所有信息，包括角色和权限
		Set<String> roles = roleService.getRolesByUserId(userDB.getUid());
		Set<String> perms = permService.getPermsByUserId(userDB.getUid());
		userDB.getRoles().addAll(roles);
		userDB.getPerms().addAll(perms);

		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userDB,
				userDB.getPwd(), getName());
		if (userDB.getSalt() != null) {
			info.setCredentialsSalt(ByteSource.Util.bytes(userDB.getSalt()));
		}
//		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
//				userDB, // 用户
//				new SimpleHash("md5", userDB.getPwd(), ByteSource.Util.bytes(userDB.getPwd()),
//						2).toHex(), // 密码
//				ByteSource.Util.bytes(userDB.getPwd()), getName() // realm name
//		);
		Session session = SecurityUtils.getSubject().getSession();
		session.setAttribute("userSession", userDB);
		session.setAttribute("userSessionId", userDB.getUname());

		session.setTimeout(1000 * 60 * 60 * 24);
		return info;
	}
}
