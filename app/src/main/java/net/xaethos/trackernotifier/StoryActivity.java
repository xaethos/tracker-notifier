package net.xaethos.trackernotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Story;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import rx.android.schedulers.AndroidSchedulers;

public class StoryActivity extends AppCompatActivity {

    private static final String EXTRA_STORY_ID = "net.xaethos.trackernotifier.storyId";

    public static Intent forStory(Context context, long storyId) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putExtra(EXTRA_STORY_ID, storyId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long storyId = getIntent().getLongExtra(EXTRA_STORY_ID, 0);

        setContentView(R.layout.activity_story);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        CollapsingToolbarLayout toolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView descriptionView = (TextView) findViewById(R.id.text_description);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view,
                "Replace with your own action",
                Snackbar.LENGTH_LONG).setAction("Action", null).show());

        TrackerClient.getInstance().stories.show(storyId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorToastSubscriber<Story>(this) {
                    @Override
                    public void onNext(Story story) {
                        toolbarLayout.setTitle(story.name);
                        descriptionView.setText(story.description);
                    }
                });
    }
}
