package com.oviva.spicegen.model;

import java.util.List;

public record Relationship(String name, List<ObjectDefinition> allowedObjects) {}
