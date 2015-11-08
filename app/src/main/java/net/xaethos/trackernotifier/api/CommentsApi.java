package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Comment;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface CommentsApi {

    @GET("projects/{project_id}/stories/{story_id}/comments")
    Observable<List<Comment>> get(
            @Path("project_id") long projectId,
            @Path("story_id") long storyId,
            @Query("fields") String fields);

    @POST("projects/{project_id}/stories/{story_id}/comments")
    Observable<Comment> create(
            @Path("project_id") long projectId,
            @Path("story_id") long storyId,
            @Body Comment comment);

}
