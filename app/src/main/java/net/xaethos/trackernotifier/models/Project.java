package net.xaethos.trackernotifier.models;

public class Project {

    /**
     * Database id of the project. This field is read only. This field is always returned.
     */
    public long id;

    /**
     * The name of the project. This field is required on create.
     * <p/>
     * <b>Required On Create</b>
     */
    public String name;

    /**
     * A counter that is incremented each time something is changed within a project. The project
     * version is used to track whether a client is 'up to date' with respect to the current
     * content of the project on the server, and to identify what updates have to be made to the
     * client's local copy of the project (if it stores one) to re-synchronize it with the server
     * . This field is read only.
     */
    public Long version;

    @Override
    public boolean equals(Object o) {
        return (o instanceof Project) && ((Project) o).id == id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }
}
