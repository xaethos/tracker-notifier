package net.xaethos.trackernotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.fragments.BaseResourceFragment;
import net.xaethos.trackernotifier.fragments.StoryCommentsFragment;
import net.xaethos.trackernotifier.fragments.StoryDetailsFragment;
import net.xaethos.trackernotifier.models.Story;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;

public class StoryActivity extends AppCompatActivity
        implements BaseResourceFragment.ResourceSource<Story> {

    private static final String EXTRA_PROJECT_ID = "net.xaethos.trackernotifier.projectId";
    private static final String EXTRA_STORY_ID = "net.xaethos.trackernotifier.storyId";
    private static final String EXTRA_STORY_HINT = "net.xaethos.trackernotifier.storyHint";

    public static Intent forStory(Context context, long projectId, Story story) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putExtra(EXTRA_PROJECT_ID, projectId);
        intent.putExtra(EXTRA_STORY_ID, story.id);
        intent.putExtra(EXTRA_STORY_HINT, story);
        return intent;
    }

    long mProjectId;
    long mStoryId;
    private final BehaviorSubject<Story> mStorySubject = BehaviorSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProjectId = getIntent().getLongExtra(EXTRA_PROJECT_ID, 0);
        mStoryId = getIntent().getLongExtra(EXTRA_STORY_ID, 0);
        Story storyHint = (Story) getIntent().getSerializableExtra(EXTRA_STORY_HINT);

        setContentView(R.layout.activity_story);

        CollapsingToolbarLayout toolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        setSupportActionBar((Toolbar) toolbarLayout.findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupAppBar(toolbarLayout, storyHint);
        //setupFab((FloatingActionButton) findViewById(R.id.fab));

        SectionsPagerAdapter adapter = new SectionsPagerAdapter();

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        TabLayout tabBar = (TabLayout) findViewById(R.id.tab_bar);
        tabBar.setupWithViewPager(viewPager);

        Observable<Story> storyObservable;
        if (mProjectId == 0) {
            storyObservable = TrackerClient.getInstance().stories.show(mStoryId);
        } else {
            storyObservable = TrackerClient.getInstance().stories.show(mProjectId, mStoryId);
        }

        storyObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorToastSubscriber<Story>(this) {
                    @Override
                    public void onNext(Story story) {
                        mStorySubject.onNext(story);
                        setupAppBar(toolbarLayout, story);
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

    @Override
    public Observable<Story> getResourceObservable() {
        return mStorySubject.asObservable();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public BaseResourceFragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            BaseResourceFragment fragment;
            switch (position) {
            case 0:
                fragment = new StoryDetailsFragment();
                break;
            case 1:
                fragment = StoryCommentsFragment.newInstance(mProjectId);
                break;
            default:
                return null;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return getString(R.string.title_fragment_story_details);
            case 1:
                return getString(R.string.title_fragment_story_comments);
            }
            return null;
        }
    }

}
