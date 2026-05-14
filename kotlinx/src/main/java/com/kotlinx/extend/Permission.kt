package com.kotlinx.extend

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * 在 [ComponentActivity.onCreate]（或更早）注册单次权限回调。
 *
 * 示例：
 * ```
 * private val permission = registerRequestPermissionLauncher { granted -> ... }
 * permission.launch(Manifest.permission.XXX)
 * ```
 */
fun ComponentActivity.registerRequestPermissionLauncher(
    onResult: (Boolean) -> Unit,
): ActivityResultLauncher<String> =
    registerForActivityResult(ActivityResultContracts.RequestPermission(), onResult)
