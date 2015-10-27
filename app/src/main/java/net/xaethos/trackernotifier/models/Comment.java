package net.xaethos.trackernotifier.models;

public class Comment extends Resource {

    /**
     * The id of the story to which the comment is attached (will be absent if comment attached
     * to an epic.
     * <p>
     * This field is read only.
     */
    public long story_id;

    /**
     * The id of the epic to which the comment is attached (will be absent if comment attached to
     * a story.
     * <p>
     * This field is read only.
     */
    public long epic_id;

    /**
     * Content of the comment.
     * <p>
     * This field is writable only on create.
     * <p>
     * Max length 20000
     */
    public String text;

    /**
     * The id of the comment creator. This field is writable only on create. In API responses,
     * this attribute may be person_id or person.
     */
    public long person_id;

    /**
     * The id of the comment creator. This field is writable only on create. In API responses,
     * this attribute may be person_id or person.
     */
    public Person person;

    /**
     * Creation time.
     * <p>
     * This field is read only.
     * <p>
     * datetime
     */
    Object created_at;

    /**
     * Updated time. (Comments are updated by removing one of their attachments.)
     * <p>
     * This field is read only.
     * <p>
     * datetime
     */
    Object updated_at;

    /**
     * IDs of any file attachments associated with the comment. This field is writable only on
     * create. This field is excluded by default. In API responses, this attribute may be
     * file_attachment_ids or file_attachments.
     */
    long[] file_attachment_ids;

    /**
     * IDs of any google attachments associated with the comment. This field is writable only on
     * create. This field is excluded by default. In API responses, this attribute may be
     * google_attachment_ids or google_attachments.
     */
    long[] google_attachment_ids;

    /**
     * Commit Id on the remote source control system for the comment. Present only on comments
     * that were created by a POST to the source commits API endpoint. This field is writable
     * only on create. (Note that this attribute does not indicate an association to another
     * resource.)
     * <p>
     * Max length 255
     */
    String commit_identifier;

    /**
     * String identifying the type of remote source control system if Pivotal Tracker can
     * determine it. Present only on comments that were created by a POST to the source commits
     * API endpoint. This field is writable only on create.
     * <p>
     * Max length 255
     */
    String commit_type;
}
