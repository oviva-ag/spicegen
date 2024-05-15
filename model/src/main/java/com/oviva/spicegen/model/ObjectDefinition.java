package com.oviva.spicegen.model;

import java.util.List;

public record ObjectDefinition(String name, List<Relationship> relationships, List<Permission> permissions) {}
