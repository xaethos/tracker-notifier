package net.xaethos.trackernotifier.models;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class ResourceTest {

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void testEquals() throws Exception {
        Resource resource = res(1, "foo");

        assertThat(resource.equals(null), is(false));
        assertThat(resource.equals(res(1, "foo")), is(true));
        assertThat(resource.equals(res(2, "foo")), is(false));
        assertThat(resource.equals(res(1, "bar")), is(false));
        assertThat(resource.equals(res(1, null)), is(false));
    }

    @Test
    public void testHashCode() throws Exception {
        Resource resource = res(1, "foo");

        assertThat(resource.hashCode(), is(res(1, "foo").hashCode()));
        assertThat(resource.hashCode(), is(not(res(2, "foo").hashCode())));
        assertThat(resource.hashCode(), is(not(res(1, "bar").hashCode())));
        assertThat(resource.hashCode(), is(not(res(1, null).hashCode())));
    }

    private Resource res(long id, String kind) {
        Resource res = new Resource();
        res.id = id;
        res.kind = kind;
        return res;
    }
}