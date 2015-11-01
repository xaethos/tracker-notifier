package net.xaethos.trackernotifier.utils

val CharSequence?.empty: Boolean
    get() = if (this == null) false else this.length == 0
