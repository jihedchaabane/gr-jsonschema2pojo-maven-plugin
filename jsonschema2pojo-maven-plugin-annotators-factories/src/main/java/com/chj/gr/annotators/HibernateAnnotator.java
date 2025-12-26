//package com.chj.gr.annotators;
//
//import org.jsonschema2pojo.AbstractAnnotator;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.sun.codemodel.JDefinedClass;
//import com.sun.codemodel.JFieldVar;
//
//public class HibernateAnnotator extends AbstractAnnotator implements org.jsonschema2pojo.Annotator {
//
//	@Override
//	public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
//		super.propertyField(field, clazz, propertyName, propertyNode);
//		
//		if (propertyName.equals("entity")) {
//			clazz.annotate(javax.persistence.Entity.class).param("name", propertyName);
//			clazz.annotate(javax.persistence.Table.class);
//			clazz.annotate(lombok.Getter.class);
//		}
//	}
//}
