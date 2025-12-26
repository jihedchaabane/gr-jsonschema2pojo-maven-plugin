package com.chj.gr.factories;

import java.util.Iterator;

import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.rules.SchemaRule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JType;
/*
 * https://stackoverflow.com/questions/60478946/maven-plugin-jsonschema2pojo-maven-plugin-not-generating-pojos-for-all-the-defin
 */

public class JsonSchemaRuleFactory extends RuleFactory {

    @Override
    public Rule<JClassContainer, JType> getSchemaRule() {
        return new MySchemaRule(this);
    }

    private class MySchemaRule extends SchemaRule {

        public MySchemaRule(JsonSchemaRuleFactory jsonSchemaRuleFactory) {
            super(jsonSchemaRuleFactory);
        }

        @Override
        public JType apply(String nodeName, JsonNode schemaNode, JsonNode parent,
                           JClassContainer generatableType,
                           org.jsonschema2pojo.Schema schema) {

            final JType apply = super.apply(nodeName, schemaNode, parent, generatableType, schema);

            final JsonNode definitions = schemaNode.get("definitions");
            if (definitions != null && definitions.isObject()) {
                final ObjectNode objectNode = (ObjectNode) definitions;
                final Iterator<String> nodetIterator = objectNode.fieldNames();
                while (nodetIterator.hasNext()) {
                    final String name = nodetIterator.next();
                    try {
                        final ObjectNode node = (ObjectNode) objectNode.get(name);
                        final Schema currentSchema = getSchemaStore().create(
                            schema, "#/definitions/" + name, getGenerationConfig().getRefFragmentPathDelimiters());
                        getSchemaRule().apply(name, node, schemaNode, generatableType.getPackage(), currentSchema);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return apply;
        }
    }
}

