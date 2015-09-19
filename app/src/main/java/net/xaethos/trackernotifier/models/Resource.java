package net.xaethos.trackernotifier.models;

public class Resource {

    /**
     * Database id of the resource. This field is read only. This field is always returned.
     */
    public long id;

    /**
     * The type of this object. This field is read only.
     */
    public String kind;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Resource)) return false;
        Resource a = this;
        Resource b = (Resource) o;
        return a.id == b.id && ((a.kind == null) ? (b.kind == null) : a.kind.equals(b.kind));
    }

    @Override
    public int hashCode() {
        int hashCode = (int) (id ^ (id >>> 32));
        hashCode = 31 * hashCode + (kind == null ? 0 : kind.hashCode());
        return hashCode;
    }

}
