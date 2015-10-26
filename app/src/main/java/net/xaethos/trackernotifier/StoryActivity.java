package net.xaethos.trackernotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Story;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import java.text.DecimalFormat;

import rx.android.schedulers.AndroidSchedulers;

public class StoryActivity extends AppCompatActivity {

    private static final String EXTRA_STORY_ID = "net.xaethos.trackernotifier.storyId";
    private static final String EXTRA_STORY_HINT = "net.xaethos.trackernotifier.storyHint";

    public static Intent forStory(Context context, Story story) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putExtra(EXTRA_STORY_ID, story.id);
        intent.putExtra(EXTRA_STORY_HINT, story);
        return intent;
    }

    public static Intent forStory(Context context, long storyId) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putExtra(EXTRA_STORY_ID, storyId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long storyId = getIntent().getLongExtra(EXTRA_STORY_ID, 0);
        Story storyHint = (Story) getIntent().getSerializableExtra(EXTRA_STORY_HINT);

        setContentView(R.layout.activity_story);

        CollapsingToolbarLayout toolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        setSupportActionBar((Toolbar) toolbarLayout.findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View contentView = findViewById(R.id.content_story);

        setupAppBar(toolbarLayout, storyHint);
        //setupFab((FloatingActionButton) findViewById(R.id.fab));

        TrackerClient.getInstance().stories.show(storyId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorToastSubscriber<Story>(this) {
                    @Override
                    public void onNext(Story story) {
                        setupAppBar(toolbarLayout, story);
                        setupContent(contentView, storyHint);
                    }
                });
    }

    private void setupAppBar(CollapsingToolbarLayout appBar, @Nullable Story story) {
        appBar.setTitle(story == null ? null : story.name);

        String type = story == null ? null : story.story_type;
        TextView typeView = (TextView) findViewById(R.id.text_type);
        typeView.setText(type);
        typeView.setCompoundDrawablesWithIntrinsicBounds(getTypeDrawable(type), 0, 0, 0);

        TextView stateView = (TextView) findViewById(R.id.text_state);
        stateView.setText(story == null ? null : story.current_state);
    }

    private void setupContent(View contentView, @Nullable Story story) {
        TextView valueView;

        valueView = (TextView) contentView.findViewById(R.id.text_id);
        valueView.setText(story == null ? null : String.valueOf(story.id));

        valueView = (TextView) contentView.findViewById(R.id.text_points);
        valueView.setText(story == null ? null : new DecimalFormat("0.##").format(story.estimate));

        valueView = (TextView) contentView.findViewById(R.id.text_name);
        valueView.setText(story == null ? null : story.name);

        valueView = (TextView) contentView.findViewById(R.id.text_description);
        valueView.setText(story == null ? null : story.description);
    }

    private void setupFab(FloatingActionButton fab) {
        fab.setOnClickListener(view -> Snackbar.make(view,
                "Replace with your own action",
                Snackbar.LENGTH_LONG).setAction("Action", null).show());
    }

    private static int getTypeDrawable(@Story.Type String storyType) {
        if (storyType == null) return 0;

        switch (storyType) {
        case Story.TYPE_FEATURE:
            return R.drawable.ic_star_white_24dp;
        case Story.TYPE_BUG:
            return R.drawable.ic_bug_report_white_24dp;
        case Story.TYPE_CHORE:
            return R.drawable.ic_settings_white_24dp;
        case Story.TYPE_RELEASE:
            return R.drawable.ic_flag_white_24dp;
        default:
            return 0;
        }
    }
}
