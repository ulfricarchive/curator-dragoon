package com.ulfric.dragoon.curator.authentication;

import com.ulfric.dragoon.vault.Secret;

public class VaultAuthentication {

	@Secret("zookeeper")
	private String acl;

	public String getAcl() {
		return acl;
	}

}
