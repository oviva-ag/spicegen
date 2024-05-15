package com.oviva.spicegen.generator.internal;

import com.oviva.spicegen.generator.SpiceDbClientGenerator;
import com.oviva.spicegen.model.ObjectDefinition;
import com.oviva.spicegen.model.Permission;
import com.oviva.spicegen.model.Relationship;
import com.oviva.spicegen.model.Schema;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;


public class SpiceDbClientGeneratorImpl implements SpiceDbClientGenerator {
    @Override
    public void generate(Schema spec) {

        TypeSpec.Builder constants = TypeSpec
                .classBuilder("SchemaConstants")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(String.class, "PLATFORM_ID").initializer("1").build());

        for(ObjectDefinition definition :spec.definitions()){
            FieldSpec namespace = FieldSpec
                    .builder(String.class, "NAMESPACE_" + definition.name().toUpperCase())
                    .initializer("\""+definition.name()+"\"")
                    .build();
            constants.addField(namespace);

            for(Permission permission: definition.permissions()){
                FieldSpec permissionField = FieldSpec
                        .builder(String.class, "PERMISSION_" + definition.name().toUpperCase() +"_"+permission.name().toUpperCase())
                        .initializer("\""+permission.name()+"\"")
                        .build();
                constants.addField(permissionField);
            }

            for(Relationship relationship: definition.relationships()){
                FieldSpec relationshipField = FieldSpec
                        .builder(String.class, "RELATIONSHIP_" + definition.name().toUpperCase() +"_"+relationship.name().toUpperCase())
                        .initializer("\""+relationship.name()+"\"")
                        .build();
                constants.addField(relationshipField);
            }
        }
    }
}
