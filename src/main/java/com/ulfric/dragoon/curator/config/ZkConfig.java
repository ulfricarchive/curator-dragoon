package com.ulfric.dragoon.curator.config;

import com.ulfric.conf4j.ConfigurationBean;

import java.util.List;

public interface ZkConfig extends ConfigurationBean {

	AuthenticationConfig authentication();

	ExhibitorConfig exhibitor();

	List<String> connections();

	RetryConfig retry();

}
