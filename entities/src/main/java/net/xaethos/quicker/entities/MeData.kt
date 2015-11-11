package net.xaethos.quicker.entities

data class MeData(
        override val id: Long,

        /**
         * Name of the authenticated user.
         */
        var name: String,

        /**
         * A string that can be used as the API authentication token (X-TrackerToken) to
         * authenticate future API requests as being on behalf of the current user.
         */
        val api_token: String

) : ResourceData {
    override val kind = "me"
}
