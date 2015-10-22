package net.xaethos.trackernotifier.models;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class Story extends Resource {

    @StringDef({TYPE_FEATURE, TYPE_BUG, TYPE_CHORE, TYPE_RELEASE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final String TYPE_FEATURE = "feature";
    public static final String TYPE_BUG = "bug";
    public static final String TYPE_CHORE = "chore";
    public static final String TYPE_RELEASE = "release";

    @StringDef({STATE_ACCEPTED,
            STATE_DELIVERED,
            STATE_FINISHED,
            STATE_STARTED,
            STATE_REJECTED,
            STATE_PLANNED,
            STATE_UNSTARTED,
            STATE_UNSCHEDULED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public static final String STATE_ACCEPTED = "accepted";
    public static final String STATE_DELIVERED = "delivered";
    public static final String STATE_FINISHED = "finished";
    public static final String STATE_STARTED = "started";
    public static final String STATE_REJECTED = "rejected";
    public static final String STATE_PLANNED = "planned";
    public static final String STATE_UNSTARTED = "unstarted";
    public static final String STATE_UNSCHEDULED = "unscheduled";

    /**
     * Name of the story. This field is required on create.
     * <p>
     * <b>Required On Create</b>
     */
    public String name;

    /**
     * In-depth explanation of the story requirements.
     */
    public String description;

    /**
     * Type of story.
     */
    @Type public String story_type;

    /**
     * Story's state of completion.
     */
    @State public String current_state;

    /**
     * Point value of the story.
     */
    public float estimate;

    /**
     * The id of the person who requested the story. In API responses, this attribute may be
     * requested_by_id or requested_by.
     */
    public long requested_by_id;

    /**
     * The {@link Person} who requested the story. In API responses, {@link #requested_by_id} may
     * be present instead.
     */
    public Person requested_by;

    /**
     * IDs of the current story owners. By default this will be included in responses as an array
     * of nested structures, using the {@link #owners}.
     */
    public List<Long> owner_ids;

    /**
     * The current story {@link Person owners}. In API responses, {@link #owner_ids} may be
     * present instead.
     */
    public List<Person> owners;

    /**
     * The url for this story in Tracker. This field is read only.
     */
    public String url;

}
