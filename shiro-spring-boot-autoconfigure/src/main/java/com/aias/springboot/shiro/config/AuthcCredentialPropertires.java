package com.aias.springboot.shiro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <b>
 *
 * <br>
 * <b>@ClassName:</b> AuthcCredentialPropertires <br>
 * <b>@Date:</b> 2019/10/29 <br>
 *
 * @author <a> liuhy </a><br>
 */

@Data
@ConfigurationProperties(prefix = AuthcCredentialPropertires.AUTHC_CREDENTIAL_PREFIX)
public class AuthcCredentialPropertires {
	static final String AUTHC_CREDENTIAL_PREFIX = "shiro.authc.credential";
	private String hashAlgorithm;
	private int hashIterations;
	private boolean storedCredentialsHexEncoded = true;
}
