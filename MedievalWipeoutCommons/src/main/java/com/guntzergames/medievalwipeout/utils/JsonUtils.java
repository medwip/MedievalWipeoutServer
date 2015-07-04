package com.guntzergames.medievalwipeout.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ObjectMapper.DefaultTyping;

import com.guntzergames.medievalwipeout.exceptions.JsonException;

public class JsonUtils {

	public static String toJson(Object o, DefaultTyping defaultTyping) throws JsonException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		mapper.enable(SerializationConfig.Feature.USE_ANNOTATIONS);
		mapper.enable(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.enableDefaultTyping(defaultTyping);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, o);
		} catch (JsonGenerationException e) {
			throw new JsonException(e) ;
		} catch (JsonMappingException e) {
			throw new JsonException(e) ;
		} catch (IOException e) {
			throw new JsonException(e) ;
		}
		String json = new String(out.toByteArray());
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;

	}

	public static <T> T fromJson(Class<T> clazz, String json, DefaultTyping defaultTyping) throws JsonException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(defaultTyping);
		T obj = null;
		try {
			obj = (T) mapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			throw new JsonException(e);
		} catch (JsonMappingException e) {
			throw new JsonException(e);
		} catch (IOException e) {
			throw new JsonException(e);
		}
		return obj;

	}

}
