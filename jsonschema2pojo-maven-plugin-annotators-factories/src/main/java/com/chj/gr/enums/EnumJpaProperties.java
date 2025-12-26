package com.chj.gr.enums;

import java.util.HashMap;
import java.util.Map;

public enum EnumJpaProperties {

	ENTITY("entity", javax.persistence.Entity.class),
	TABLE("table", javax.persistence.Table.class);
	
	private final String value;
	private final Class<?> clazz;
	
	private final static Map<String, EnumJpaProperties> CONSTANTS = new HashMap<String, EnumJpaProperties>();
	
	static {
		for (EnumJpaProperties c : values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	private EnumJpaProperties(String value, Class<?> clazz) {
		this.value = value;
		this.clazz = clazz;
	}
	
	@Override
	public String toString() {
		return this.value + ":" + this.clazz.getCanonicalName();
	}
	
	public String value() {
		return this.value;
	}
	
	public Class<?> clazz() {
		return this.clazz;
	}
	
	public static EnumJpaProperties fromValue(String value) {
		return CONSTANTS.get(value);
	}
}
