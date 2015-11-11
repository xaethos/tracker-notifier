package net.xaethos.quicker.entities

interface ResourceData {

    /**
     * Database id of the resource. This field is read only. This field is always returned.
     */
    val id: Long

    /**
     * The type of this object. This field is read only.
     */
    val kind: String

}
