package net.xaethos.quicker.common

import javax.inject.Inject

class Config @Inject constructor(
        val baseUrl: String,
        val isDebug: Boolean
)
