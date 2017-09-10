package com.ulfric.dragoon.curator.crud;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

public class PayloadHelper {

	private static final Gson GSON = new Gson();

	public static byte[] createPayload(Object payload) {
		return GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
	}

	public static <T> T readPayload(byte[] payload, Class<T> type) {
		return GSON.fromJson(new String(payload, StandardCharsets.UTF_8), type);
	}

	private PayloadHelper() {
	}

}
