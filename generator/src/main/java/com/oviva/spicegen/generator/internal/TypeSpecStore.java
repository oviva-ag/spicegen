package com.oviva.spicegen.generator.internal;

import com.squareup.javapoet.TypeSpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TypeSpecStore implements Iterable<TypeSpec> {

  private Map<String, TypeSpec> types = new HashMap<>();

  public void put(TypeSpec spec) {
    types.put(spec.name, spec);
  }

  public boolean has(String name) {
    return types.containsKey(name);
  }

  public TypeSpec get(String name) {
    return types.get(name);
  }

  public Set<TypeSpec> entries() {
    return Set.copyOf(types.values());
  }

  @Override
  public Iterator<TypeSpec> iterator() {
    return types.values().iterator();
  }
}
