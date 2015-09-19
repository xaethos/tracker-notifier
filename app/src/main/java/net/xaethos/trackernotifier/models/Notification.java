package net.xaethos.trackernotifier.models;

public class Notification extends Resource {

    /**
     * Message of the notification. This field is read only.
     */
    public String message;

    /**
     * Context of the notification. For example, if a comment was added, this will contain the
     * comment text. This field is read only.
     */
    public String context;

    /**
     * Id of the project. This field is read only. By default this will be included in responses
     * as a nested structure, using {@link #project}.
     */
    public long project_id;

    /**
     * The notification project. This field is read only. In API responses, {@link #project_id}
     * may be present instead.
     */
    public Project project;

    /**
     * Id of the person who triggered the notification. This field is read only. By default this
     * will be included in responses as a nested structure, using {@link #performer}. In API
     * responses, this attribute may be performer_id or performer.
     */
    public long performer_id;

    /**
     * The {@link Person} who triggered the notification. This field is read only. In API
     * responses, {@link #performer_id} may be present instead.
     */
    public Person performer;

    /**
     * ID of the story this notification is about. This field is read only. By default this will
     * be included in responses as a nested structure, using {@link #story}.
     */
    public long story_id;

    /**
     * The story this notification is about. This field is read only. In API responses, {@link
     * #story_id} may be present instead.
     */
    public Story story;

    /**
     * Time notification was read.
     * type: datetime
     */
    public Object read_at;

}
