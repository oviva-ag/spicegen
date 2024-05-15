package com.oviva.spicegen.generator;

import com.oviva.spicegen.model.Schema;

public interface SpiceDbClientGenerator {

  void generate(Schema spec);
}
