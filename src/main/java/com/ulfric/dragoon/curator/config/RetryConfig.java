package com.ulfric.dragoon.curator.config;

public interface RetryConfig {

	boolean forever();

	int intervalMs();

	int maxIntervalMs();

	int maxRetries();

}