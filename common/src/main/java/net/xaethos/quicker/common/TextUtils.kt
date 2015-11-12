package net.xaethos.quicker.common

val CharSequence?.empty: Boolean
    get() = (this != null) && (this.length > 0)
