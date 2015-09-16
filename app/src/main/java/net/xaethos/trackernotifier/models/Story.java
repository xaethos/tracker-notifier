package net.xaethos.trackernotifier.models;

public class Story {

    /**
     * Database id of the story. This field is read only. This field is always returned.
     */
    public long id;

    /**
     * Name of the story. This field is required on create.
     * <p/>
     * <b>Required On Create</b>
     */
    public String name;

    @Override
    public boolean equals(Object o) {
        return (o instanceof Story) && ((Story) o).id == id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

}
