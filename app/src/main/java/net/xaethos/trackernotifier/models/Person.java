package net.xaethos.trackernotifier.models;

public class Person extends Resource {

    /**
     * The full name of the person.
     * <p>
     * <b>Required On Create</b>
     */
    public String name;

    /**
     * The email address of the person. This field may be omitted for security reasons depending
     * on the request from which it is being returned. For example, the content of a public
     * project can be retrieved through the API without user authentication, but in this case the
     * email is not included in person resources contained in the project's 'members' list.
     * <p>
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

}
