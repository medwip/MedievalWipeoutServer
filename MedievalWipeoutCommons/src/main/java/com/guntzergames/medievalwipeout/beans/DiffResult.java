package com.guntzergames.medievalwipeout.beans;

import org.codehaus.jackson.map.ObjectMapper.DefaultTyping;

import com.guntzergames.medievalwipeout.exceptions.JsonException;
import com.guntzergames.medievalwipeout.utils.JsonUtils;

import difflib.Patch;

public class DiffResult {
	
	private Patch patch;
	
	public DiffResult() {
	}
	
	public DiffResult(Patch patch) {
		this.patch = patch;
	}
	
	public Patch getPatch() {
		return patch;
	}

	public void setPatch(Patch patch) {
		this.patch = patch;
	}
	
	public static DiffResult fromJson(String json) throws JsonException {
		return JsonUtils.fromJson(DiffResult.class, json, DefaultTyping.NON_FINAL);
	}
	
	public String toJson() throws JsonException {
		return JsonUtils.toJson(this, DefaultTyping.NON_FINAL);
	}
	
}
