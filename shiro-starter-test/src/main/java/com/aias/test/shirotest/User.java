package com.aias.test.shirotest;


import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;


/**
 * <b>
 *
 * <br>
 * <b>@ClassName:</b> User <br>
 * <b>@Date:</b> 2019/10/25 <br>
 *
 * @author <a> liuhy </a><br>
 */
@Data
public class User implements Serializable {

	private Long uid; // 用户id
	private String uname; // 登录名，不可改
	private String nick; // 用户昵称，可改
	private String pwd; // 已加密的登录密码
	private String salt; // 加密盐值
	private Date created; // 创建时间
	private Date updated; // 修改时间
	private Set<String> roles = new HashSet<>(); // 用户所有角色值，用于shiro做角色权限的判断
	private Set<String> perms = new HashSet<>(); // 用户所有权限值，用于shiro做资源权限的判断
	// getters and setters...
}