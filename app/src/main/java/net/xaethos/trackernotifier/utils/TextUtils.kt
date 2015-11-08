package net.xaethos.trackernotifier.utils

val CharSequence?.empty: Boolean
    get() = (this?.length ?: 0) == 0
