package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Story;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

public interface StoriesApi {

    @GET("projects/{project_id}/stories")
    Observable<List<Story>> get(@Path("project_id") long projectId);

    @POST("projects/{project_id}/stories")
    Observable<Story> create(@Path("project_id") long projectId, @Body Story story);

    @GET("projects/{project_id}/stories/{story_id}")
    Observable<Story> show(@Path("project_id") long projectId, @Path("story_id") long storyId);

    @PUT("projects/{project_id}/stories/{story_id}")
    Observable<Story> update(
            @Path("project_id") long projectId, @Path("story_id") long storyId, @Body Story story);

    @DELETE("projects/{project_id}/stories/{story_id}")
    Observable<Void> delete(@Path("project_id") long projectId, @Path("story_id") long storyId);

    /**
     * Returns the specified story.
     *
     * @param storyId The ID of the story.
     * @return Successful responses to this request return the story resource.
     * @see <a href="https://www.pivotaltracker.com/help/api/rest/v5#stories_story_id_get">
     * Pivotal Tracker documentation</a>
     */
    @GET("stories/{story_id}")
    Observable<Story> show(@Path("story_id") long storyId);

    /**
     * Updates the specified story.
     *
     * @see <a href="https://www.pivotaltracker.com/help/api/rest/v5#stories_story_id_put">
     * Pivotal Tracker documentation</a>
     */
    @PUT("stories/{story_id}")
    Observable<Story> update(@Path("story_id") long storyId, @Body Story story);

    /**
     * Deletes the specified story.
     *
     * @see <a href="https://www.pivotaltracker.com/help/api/rest/v5#stories_story_id_delete">
     * Pivotal Tracker documentation</a>
     */
    @DELETE("stories/{story_id}")
    Observable<Void> delete(@Path("story_id") long storyId);

}
