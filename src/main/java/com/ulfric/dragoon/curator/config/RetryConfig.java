package com.ulfric.dragoon.curator.config;

import com.ulfric.conf4j.ConfigurationBean;

public interface RetryConfig extends ConfigurationBean {

	boolean forever();

	int intervalMs();

	int maxIntervalMs();

	int maxRetries();

}