package com.oviva.spicegen.spicedbbinding.internal;

import com.oviva.spicegen.api.CheckBulkPermissionItem;
import com.oviva.spicegen.api.CheckBulkPermissionsResult;

public record CheckPermissionsResultImpl(boolean permissionGranted, CheckBulkPermissionItem request)
    implements CheckBulkPermissionsResult {}
