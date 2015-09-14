package net.xaethos.trackernotifier.models;

public class Person {
    /**
     * Database id of the person. This field is read only. This field is always returned.
     */
    public long id;

    /**
     * The full name of the person. This field is required on create.
     * <p/>
     * <b>Required On Create</b>
     */
    public String name;

    /**
     * The email address of the person. This field may be omitted for security reasons depending
     * on the request from which it is being returned. For example, the content of a public
     * project can be retrieved through the API without user authentication, but in this case the
     * email is not included in person resources contained in the project's 'members' list. This
     * field is required on create.
     * <p/>
     * <b>Required On Create</b>
     */
    public String email;

    /**
     * The initials of the person.
     */
    public String initials;

    /**
     * The username of the person.
     */
    public String username;

    /**
     * The type of this object. i.e. person. This field is read only.
     */
    public String kind;
}
