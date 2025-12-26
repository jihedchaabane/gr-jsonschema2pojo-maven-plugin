package com.chj.gr.annotators;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jsonschema2pojo.AbstractAnnotator;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class LombokAnnotator extends AbstractAnnotator {

	@Override
    public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        super.propertyField(field, clazz, propertyName, propertyNode);

        String typeName = field.type().name();
        boolean isRequired = isRequired(propertyNode);

        // === JPA : champ id ===
        if ("id".equals(propertyName)) {
            field.annotate(Id.class);
            field.annotate(GeneratedValue.class)
                    .param("strategy", GenerationType.SEQUENCE)
                    .param("generator", "seq_gen");

            field.annotate(Column.class)
                    .param("name", "id_" + clazz.name().toLowerCase())
                    .param("nullable", false)
                    .param("updatable", false);
            return;
        }

        // === Bean Validation : contraintes de base ===
        if (propertyNode.has("minLength") || propertyNode.has("maxLength") || "string".equals(propertyNode.path("type").asText())) {
            if (propertyNode.has("minLength")) {
                field.annotate(Size.class).param("min", propertyNode.get("minLength").asInt());
            }
            if (propertyNode.has("maxLength")) {
                field.annotate(Size.class).param("max", propertyNode.get("maxLength").asInt());
            }
            if (isRequired) {
                field.annotate(NotBlank.class);
            } else if ("string".equals(propertyNode.path("type").asText())) {
                field.annotate(NotNull.class);
            }
        }
       
        // Pattern → @Pattern
        if (propertyNode.has("pattern")) {
            field.annotate(javax.validation.constraints.Pattern.class)
                    .param("regexp", propertyNode.get("pattern").asText());
        }

        // Format email
        if ("email".equals(propertyNode.path("format").asText())) {
            field.annotate(Email.class);
        }
       
        // Nombres
        if ("integer".equals(propertyNode.path("type").asText()) || "number".equals(propertyNode.path("type").asText())) {
            if (propertyNode.has("minimum")) {
                if (field.type().isPrimitive() || "Long".equals(typeName) || "Integer".equals(typeName)) {
                    field.annotate(Min.class).param("value", propertyNode.get("minimum").asLong());
                } else {
                    field.annotate(PositiveOrZero.class);
                }
            }
            if (propertyNode.has("maximum")) {
                field.annotate(Max.class).param("value", propertyNode.get("maximum").asLong());
            }
            if (isRequired) field.annotate(NotNull.class);
        }

        // Boolean → rien de spécial
        if ("boolean".equals(propertyNode.path("type").asText()) && isRequired) {
            field.annotate(NotNull.class);
        }

        // === Dates & Temporal ===
        String format = propertyNode.path("format").asText();
        if ("date".equals(format) || "date-time".equals(format)) {
            field.annotate(Temporal.class)
                    .param("value", "date-time".equals(format) ? TemporalType.TIMESTAMP : TemporalType.DATE);

            if (propertyName.contains("birth") || propertyName.contains("dateOfBirth")) {
                field.annotate(Past.class);
            }
            if (propertyName.contains("expiry") || propertyName.contains("expiration")) {
                field.annotate(Future.class);
            }
            if (isRequired) field.annotate(NotNull.class);
        }

        // === Enum ===
        if (propertyNode.has("enum")) {
            field.annotate(Enumerated.class)
                    .param("value", EnumType.STRING);
        }
        
        // === JPA : colonne standard ===
        field.annotate(Column.class)
                .param("name", "col_" + propertyName.toLowerCase())
                .param("nullable", !isRequired);
    }

	@Override
    public void propertyInclusion(JDefinedClass clazz, JsonNode schema) {
        JsonNode addProps = schema.get("additionalProperties");
        if (addProps == null || !addProps.isObject()) return;

        addProps.fields().forEachRemaining(entry -> {
            if (entry.getValue().asBoolean() == true) {
                switch (entry.getKey()) {
                    case "entity" -> clazz.annotate(Entity.class);
                    case "table" -> clazz.annotate(Table.class).param("name", clazz.name().toLowerCase() + "s");
                    case "lombok-data" -> clazz.annotate(lombok.Data.class);
                    case "lombok-builder" -> clazz.annotate(lombok.Builder.class);
                    case "lombok-getter" -> clazz.annotate(lombok.Getter.class);
            		case "lombok-setter" -> clazz.annotate(lombok.Setter.class);
                    case "lombok-to-string" -> clazz.annotate(lombok.ToString.class);
                    case "lombok-equals-and-hash-code" -> clazz.annotate(lombok.EqualsAndHashCode.class);
                    case "lombok-no-args-constructor" -> clazz.annotate(lombok.NoArgsConstructor.class);
                    case "lombok-all-args-constructor" -> clazz.annotate(lombok.AllArgsConstructor.class);
                    case "bean-validation" -> clazz.annotate(lombok.val.class);
                }
            }
        });

        // SequenceGenerator si entité
        if (addProps.has("entity") && addProps.get("entity").asBoolean() == true) {
            clazz.annotate(SequenceGenerator.class)
                    .param("name", "seq_gen")
                    .param("sequenceName", "seq_" + clazz.name().toLowerCase())
                    .param("allocationSize", 1);
        }
    }
	
	private boolean isRequired(JsonNode propertyNode) {
	    // Si la propriété a "required": true → obligatoire
	    // Sinon → facultatif (même si absent → false)
	    return propertyNode.has("required") && propertyNode.get("required").asBoolean() == true;
	}

	@Override
	public boolean isAdditionalPropertiesSupported() {
		return false;
	}
}
