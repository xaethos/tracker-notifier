package net.xaethos.trackernotifier

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import net.xaethos.trackernotifier.api.TrackerClient
import net.xaethos.trackernotifier.fragments.BaseResourceFragment
import net.xaethos.trackernotifier.fragments.StoryCommentsFragment
import net.xaethos.trackernotifier.fragments.StoryDetailsFragment
import net.xaethos.trackernotifier.models.Story
import net.xaethos.trackernotifier.subscribers.toastError
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.BehaviorSubject

private val EXTRA_PROJECT_ID = "net.xaethos.trackernotifier.projectId"
private val EXTRA_STORY_ID = "net.xaethos.trackernotifier.storyId"
private val EXTRA_STORY_HINT = "net.xaethos.trackernotifier.storyHint"

private fun getTypeDrawable(@Story.Type storyType: String?) = when (storyType) {
    Story.TYPE_FEATURE -> R.drawable.ic_star_white_24dp
    Story.TYPE_BUG -> R.drawable.ic_bug_report_white_24dp
    Story.TYPE_CHORE -> R.drawable.ic_settings_white_24dp
    Story.TYPE_RELEASE -> R.drawable.ic_flag_white_24dp
    else -> 0
}

class StoryActivity : AppCompatActivity(), BaseResourceFragment.ResourceSource<Story> {

    private var projectId: Long = 0
    private var storyId: Long = 0
    private val storySubject = BehaviorSubject.create<Story>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = intent.getLongExtra(EXTRA_PROJECT_ID, 0)
        storyId = intent.getLongExtra(EXTRA_STORY_ID, 0)
        val storyHint = intent.getSerializableExtra(EXTRA_STORY_HINT) as Story

        setContentView(R.layout.activity_story)

        val toolbarLayout = findViewById(R.id.toolbar_layout) as CollapsingToolbarLayout

        setSupportActionBar(toolbarLayout.findViewById(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupAppBar(toolbarLayout, storyHint)

        val viewPager = findViewById(R.id.pager) as ViewPager
        viewPager.adapter = SectionsPagerAdapter()

        val tabBar = findViewById(R.id.tab_bar) as TabLayout
        tabBar.setupWithViewPager(viewPager)

        val storyObservable: Observable<Story>
        if (projectId == 0L) {
            storyObservable = TrackerClient.instance.stories.show(storyId)
        } else {
            storyObservable = TrackerClient.instance.stories.show(projectId, storyId)
        }

        storyObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { story ->
                            storySubject.onNext(story)
                            setupAppBar(toolbarLayout, story)

                        },
                        { toastError(it) }
                )
    }

    private fun setupAppBar(appBar: CollapsingToolbarLayout, story: Story?) {
        appBar.title = story?.name

        val type = story?.story_type
        val typeView = findViewById(R.id.text_type) as TextView
        typeView.text = type
        typeView.setCompoundDrawablesWithIntrinsicBounds(getTypeDrawable(type), 0, 0, 0)

        val stateView = findViewById(R.id.text_state) as TextView
        stateView.text = story?.current_state
    }

    override val resourceObservable: Observable<Story>
        get() = storySubject.asObservable()

    private inner class SectionsPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int) = when (position) {
            0 -> StoryDetailsFragment()
            1 -> StoryCommentsFragment.newInstance(projectId)
            else -> null
        }

        override fun getCount() = 2

        override fun getPageTitle(position: Int) = when (position) {
            0 -> getString(R.string.title_fragment_story_details)
            1 -> getString(R.string.title_fragment_story_comments)
            else -> null
        }
    }

    companion object {

        fun forStory(context: Context, projectId: Long, story: Story): Intent {
            val intent = Intent(context, StoryActivity::class.java)
            intent.putExtra(EXTRA_PROJECT_ID, projectId)
            intent.putExtra(EXTRA_STORY_ID, story.id)
            intent.putExtra(EXTRA_STORY_HINT, story)
            return intent
        }
    }

}
