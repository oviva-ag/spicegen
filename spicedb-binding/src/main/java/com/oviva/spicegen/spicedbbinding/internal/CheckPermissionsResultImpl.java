package com.oviva.spicegen.spicedbbinding.internal;

import com.oviva.spicegen.api.CheckPermission;
import com.oviva.spicegen.api.CheckPermissionsResult;

public record CheckPermissionsResultImpl(boolean permissionGranted,
                                         CheckPermission request) implements CheckPermissionsResult {

}
