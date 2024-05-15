package com.oviva.spicegen.generator.internal;

import com.oviva.spicegen.model.ObjectDefinition;
import com.oviva.spicegen.model.ObjectTypeRef;
import com.oviva.spicegen.model.Relation;
import com.oviva.spicegen.model.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpiceDbClientGeneratorImplTest {

    @Test
    void test(){
        var generator = new SpiceDbClientGeneratorImpl();
        var objectDefinition2 = new ObjectDefinition("foo", List.of(), List.of());
    var objectDefinition1 =
        new ObjectDefinition(
            "hi", List.of(new Relation("son_of", List.of(new ObjectTypeRef("foo", null)))), List.of());
        var definitions = List.of(objectDefinition1, objectDefinition2);
        var schema = new Schema(definitions);
        generator.generate(schema);
    }
}

