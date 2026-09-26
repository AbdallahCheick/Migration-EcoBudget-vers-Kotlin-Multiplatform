package com.example.utils

import java.util.UUID

actual fun generateUUID(): String = UUID.randomUUID().toString()
