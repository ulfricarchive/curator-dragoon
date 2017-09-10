package com.ulfric.dragoon.curator.authentication;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.curator.framework.AuthInfo;

public class AclHelper {

	public static AuthInfo parseAuthInfo(String authInfo) {
		Objects.requireNonNull(authInfo, "authInfo");

		String[] parts = authInfo.split(":", 2);

		if (parts.length != 2) {
			throw new IllegalArgumentException("Expected scheme:authInfo, but was " + authInfo);
		}

		return new AuthInfo(parts[0], parts[1].getBytes(StandardCharsets.UTF_8));
	}

	private AclHelper() {
	}

}
