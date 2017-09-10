package com.ulfric.dragoon.curator.exception;

import org.apache.zookeeper.KeeperException;

public class UncheckedZkException extends RuntimeException {

	public UncheckedZkException(Throwable cause) {
		super(cause);
	}

	public boolean is(Class<? extends KeeperException> exception) {
		return exception.isInstance(getCause());
	}

}
