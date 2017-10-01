package com.ulfric.dragoon.curator.config;

import com.ulfric.conf4j.ConfigurationBean;

public interface AuthenticationConfig extends ConfigurationBean {

	boolean enabled();

	boolean useVault();

	String acl();

}
