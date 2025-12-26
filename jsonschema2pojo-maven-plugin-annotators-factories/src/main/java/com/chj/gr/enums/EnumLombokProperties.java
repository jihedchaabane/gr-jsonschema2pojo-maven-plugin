package com.chj.gr.enums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

public enum EnumLombokProperties {
	LOMBOK_DATA("lombok-data", 											Data.class),
	LOMBOK_BUILDER("lombok-builder", 									Builder.class),
	LOMBOK_GETTER("lombok-getter", 										Getter.class),
	LOMBOK_SETTER("lombok-setter", 										Setter.class),
	LOMBOK_EQUALS_AND_HASH_CODE("lombok-equals-and-hash-code", 			EqualsAndHashCode.class),
	LOMBOK_TO_STRING("lombok-to-string", 								ToString.class),
	LOMBOK_NO_ARGS_CONSTRUCTOR("lombok-no-args-constructor", 			NoArgsConstructor.class),
	LOMBOK_ALL_ARGS_CONSTRUCTOR("lombok-all-args-constructor", 			AllArgsConstructor.class),
	LOMBOK_BEAN_VALIDATION("bean-validation", val.class);

	private final String value;
	private final Class annotationClass;

	EnumLombokProperties(String value, Class annotationClass) {
		this.value = value;
		this.annotationClass = annotationClass;
	}

	public String value() {
		return value;
	}

	public Class clazz() {
		return annotationClass;
	}

	public static EnumLombokProperties fromValue(String value) {
		for (EnumLombokProperties prop : values()) {
			if (prop.value.equals(value)) {
				return prop;
			}
		}
		return null;
	}
}