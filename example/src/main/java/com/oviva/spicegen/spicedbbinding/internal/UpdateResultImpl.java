package com.oviva.spicegen.spicedbbinding.internal;

import com.oviva.spicegen.api.UpdateResult;

public record UpdateResultImpl(String consistencyToken) implements UpdateResult {}
