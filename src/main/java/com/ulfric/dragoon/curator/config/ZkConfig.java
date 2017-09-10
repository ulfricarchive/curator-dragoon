package com.ulfric.dragoon.curator.config;

import java.util.List;

public interface ZkConfig {

	AuthenticationConfig authentication();

	ExhibitorConfig exhibitor();

	List<String> connections();

	RetryConfig retry();

}
