package com.ulfric.dragoon.curator.config;

import com.ulfric.conf4j.ConfigurationBean;

public interface ExhibitorConfig extends ConfigurationBean {

	boolean enabled();

	int port();

	String backupConnection();

	boolean useSsl();

	String restUriPath();

	int pollingMs();

	ExhibitorRetryConfig retry();

}
