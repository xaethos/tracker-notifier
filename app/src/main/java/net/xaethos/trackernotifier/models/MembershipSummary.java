package net.xaethos.trackernotifier.models;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MembershipSummary extends Resource {

    @StringDef({ROLE_OWNER, ROLE_MEMBER, ROLE_VIEWER, ROLE_INACTIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Role {
    }

    public static final String ROLE_OWNER = "owner";
    public static final String ROLE_MEMBER = "member";
    public static final String ROLE_VIEWER = "viewer";
    public static final String ROLE_INACTIVE = "inactive";

    /**
     * The id of the project.
     */
    public long project_id;

    /**
     * The name of the project.
     */
    public String project_name;

    /**
     * The color of the project on the member's views.
     */
    public String project_color;

    /**
     * The relationship between the authenticated user making the request and the project.
     */
    @Role public String role;

    /**
     * The last (approximate) time at which the authenticated user accessed the project.
     */
    public Object last_viewed_at;

}
