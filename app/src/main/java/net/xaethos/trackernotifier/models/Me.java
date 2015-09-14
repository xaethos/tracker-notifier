package net.xaethos.trackernotifier.models;

import java.util.List;

public class Me extends Person {

    /**
     * A string that can be used as the API authentication token (X-TrackerToken) to authenticate
     * future API requests as being on behalf of the current user.
     */
    public String api_token;

    /**
     * IDs of the project(s) that the authenticated user is a member of. By default this will be
     * included in responses as an array of nested structures, using the key `projects`. In API
     * responses, this attribute may be project_ids or projects.
     *
     * @see #projects
     */
    public List<Long> project_ids;

    /**
     * The project(s) that the authenticated user is a member of. By default this will be
     * included in responses as an array of nested structures, using the key `projects`. In API
     * responses, this attribute may be project_ids or projects.
     *
     * @see #project_ids
     */
    public List<Project> projects;

}
