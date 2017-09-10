package com.ulfric.dragoon.curator;

import java.util.Objects;

public class Namespace {

	private final String value;

	public Namespace(String name) {
		Objects.requireNonNull(name, "name"); // TODO more validation

		this.value = '/' + name;
	}

	public String value() {
		return value;
	}

}
