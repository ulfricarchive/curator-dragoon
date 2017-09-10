package com.ulfric.dragoon.curator.config;

public interface ExhibitorConfig {

	boolean enabled();

	int port();

	String backupConnection();

	boolean useSsl();

	String restUriPath();

	int pollingMs();

	ExhibitorRetryConfig retry();

}
