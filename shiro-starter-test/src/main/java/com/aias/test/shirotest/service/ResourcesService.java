package com.aias.test.shirotest.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aias.springboot.shiro.service.IResourceService;
import org.springframework.stereotype.Service;

/**
 * <b>
 *
 * <br>
 * <b>@ClassName:</b> ResourcesService <br>
 * <b>@Date:</b> 2019/10/29  <br>
 *
 * @author <a> liuhy </a><br>
 */
@Service
public class ResourcesService implements IResourceService {



    @Override
    public List<String> queryAll() {
		return null;
    }

    public Set<String> getRolesByUserId(Long uid) {
        Set<String> roles = new HashSet<>();
        // 三种编程语言代表三种角色：js程序员、java程序员、c++程序员
        roles.add("js");
        roles.add("java");
        roles.add("cpp");
        return roles;
    }
}
