package com.ulfric.dragoon.curator.config;

public interface AuthenticationConfig {

	boolean enabled();

	boolean useVault();

	String acl();

}
