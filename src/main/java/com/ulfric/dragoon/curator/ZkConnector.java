package com.ulfric.dragoon.curator;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.curator.framework.CuratorFramework;

import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.asynchronous.Asynchronous;
import com.ulfric.dragoon.extension.intercept.asynchronous.AsynchronousResult;
import com.ulfric.dragoon.value.Result;

public class ZkConnector {

	@Inject
	private CuratorFramework curator;

	@Inject
	private Logger logger;

	private volatile boolean connected;

	@Asynchronous
	public Future<Boolean> connect(Consumer<Result> callback) {
		if (isConnected()) {
			callback(callback, Result.SUCCESS);
			return AsynchronousResult.TRUE;
		}

		try {
			curator.start();
			curator.blockUntilConnected();
			callback(callback, Result.SUCCESS);
			connected = true;
			return AsynchronousResult.TRUE;
		} catch (InterruptedException exception) {
		} catch (Exception exception) {
			logger.log(Level.SEVERE, "Failed connection", exception);
		}

		callback(callback, Result.FAILURE);
		disconnect();
		return AsynchronousResult.FALSE;
	}

	private void callback(Consumer<Result> callback, Result result) {
		if (callback != null) {
			callback.accept(result);
		}
	}

	public void disconnect() {
		curator.close();
		connected = false;
	}

	public boolean isConnected() {
		return curator.getZookeeperClient().isConnected() && connected;
	}

}
