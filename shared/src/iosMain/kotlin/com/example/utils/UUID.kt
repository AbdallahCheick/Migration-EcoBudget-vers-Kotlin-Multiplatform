package com.example.utils

import platform.Foundation.NSUUID

actual fun generateUUID(): String = NSUUID().UUIDString()
