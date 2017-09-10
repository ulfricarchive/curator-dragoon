package com.ulfric.dragoon.curator.crud;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import com.ulfric.dragoon.curator.exception.UncheckedZkException;
import com.ulfric.dragoon.extension.inject.Inject;

public class ZkCrud {

	@Inject
	private CuratorFramework curator;

	public void createEphemeral(String path) {
		try {
			curator.create()
				.creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL)
				.forPath(path);
		} catch (Exception exception) {
			throw new UncheckedZkException(exception);
		}
	}

	public void createEphemeral(String path, Object payload) {
		if (payload == null) {
			createEphemeral(path);
		}

		try {
			curator.create()
				.creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL)
				.forPath(path, PayloadHelper.createPayload(payload));
		} catch (Exception exception) {
			throw new UncheckedZkException(exception);
		}
	}

	public void delete(String path) {
		try {
			curator.delete()
				.guaranteed()
				.deletingChildrenIfNeeded()
				.forPath(path);
		} catch (Exception exception) {
			throw new UncheckedZkException(exception);
		}
	}

}
