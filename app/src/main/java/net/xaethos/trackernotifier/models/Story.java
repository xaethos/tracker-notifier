package net.xaethos.trackernotifier.models;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Story extends Resource {

    @StringDef({TYPE_FEATURE, TYPE_BUG, TYPE_CHORE, TYPE_RELEASE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final String TYPE_FEATURE = "feature";
    public static final String TYPE_BUG = "bug";
    public static final String TYPE_CHORE = "chore";
    public static final String TYPE_RELEASE = "release";


    /**
     * Name of the story. This field is required on create.
     * <p>
     * <b>Required On Create</b>
     */
    public String name;

    /**
     * Type of story.
     */
    @Type
    public String story_type;

}
