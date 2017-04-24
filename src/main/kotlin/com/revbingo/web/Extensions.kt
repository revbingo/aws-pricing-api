package com.revbingo.web

import com.revbingo.price.AvailabilityZone
import com.revbingo.price.OperatingSystem

fun AvailabilityZone.toLongRegionName(): String = when(this) {
    "eu-west-1" -> "EU (Ireland)"
    "us-west-1" -> "US West (N. California)"
    "us-west-2" -> "US West (Oregon)"
    "ap-southeast-1" -> "Asia Pacific (Singapore)"
    "ap-southeast-2" -> "Asia Pacific (Sydney)"
    "us-east-1" -> "US East (N. Virginia)"
    else -> this
}

fun OperatingSystem.adaptOS(): String = when(this.toLowerCase()) {
    "windows" -> "Windows"
    "linux/unix", "linux" -> "Linux"
    else -> "Other"
}