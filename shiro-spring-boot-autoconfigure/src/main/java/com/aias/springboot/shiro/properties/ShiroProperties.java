package com.aias.springboot.shiro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * <b>
 *
 * <br>
 * <b>@ClassName:</b> ShiroProperties <br>
 * <b>@Date:</b> 2019/11/4  <br>
 *
 * @author <a> liuhy </a><br>
 */
@Data
@ConfigurationProperties(prefix = ShiroProperties.SHIRO_PREFIX)
public class ShiroProperties {
    static final String SHIRO_PREFIX = "shiro";


    private AuthcPropertires authc;

    private ShiroFilterProperties filter;

    @Data
    public static class AuthcPropertires {
        private String hashAlgorithm;
        private int hashIterations;
        private boolean storedCredentialsHexEncoded = true;
    }

    @Data
    public static class ShiroFilterProperties {
        private String loginUrl;
        private String successUrl;
        private String unauthorizedUrl;
        private Map<String,String> filterChainMap;
    }


}
