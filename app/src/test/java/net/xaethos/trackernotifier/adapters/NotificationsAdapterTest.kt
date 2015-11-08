package net.xaethos.trackernotifier.adapters

import android.support.v7.widget.RecyclerView
import net.xaethos.trackernotifier.models.Notification
import net.xaethos.trackernotifier.models.Project
import net.xaethos.trackernotifier.models.Story
import net.xaethos.trackernotifier.test.RobolectricTest
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class NotificationsAdapterTest : RobolectricTest() {

    lateinit var adapter: NotificationsAdapter
    lateinit var observer: RecyclerView.AdapterDataObserver

    lateinit var projects: Array<Project>
    lateinit var stories: Array<Story>
    lateinit var notifications: Array<Notification>

    @Before
    fun setUpDataSource() {
        observer = mock(RecyclerView.AdapterDataObserver::class.java)
        adapter = NotificationsAdapter()
        adapter.registerAdapterDataObserver(observer)
    }

    @Before
    fun setUpNotifications() {
        projects = arrayOf(buildProject(0), buildProject(1))
        stories = arrayOf(buildStory(0), buildStory(1), buildStory(2))
        notifications = arrayOf(// The Tracker API would return these reversed
                buildNotification(0, stories[1], projects[0]),
                buildNotification(1, stories[2], projects[1]),
                buildNotification(2, stories[0], projects[0]),
                buildNotification(3, stories[1], projects[0]),
                buildNotification(4, stories[1], projects[0]))
    }

    @Test
    fun itemIdIsResourceHashcode() {
        adapter.addNotification(notifications[3])
        assertThat(adapter.getItemId(0), `is`(projects[0].hashCode().toLong()))
        assertThat(adapter.getItemId(1), `is`(stories[1].hashCode().toLong()))
        assertThat(adapter.getItemId(2), `is`(notifications[3].hashCode().toLong()))
    }

    @Test
    fun addingItemsOneByOne() {
        assertThat(adapter.itemCount, `is`(0))

        adapter.addNotification(notifications[3])
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 3
         */
        assertThat(adapter.itemCount, `is`(3))

        adapter.addNotification(notifications[2])
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 3
          [3]   Story 0
          [4]     Notification 2
         */
        assertThat(adapter.itemCount, `is`(5))

        adapter.addNotification(notifications[1])
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 3
          [3]   Story 0
          [4]     Notification 2
          [5] Project 1
          [6]   Story 2
          [7]     Notification 1
         */
        assertThat(adapter.itemCount, `is`(8))

        adapter.addNotification(notifications[0])
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 3
          [3]     Notification 0
          [4]   Story 0
          [5]     Notification 2
          [6] Project 1
          [7]   Story 2
          [8]     Notification 1
         */
        val expected = arrayOf(
                projects[0],
                stories[1], notifications[3], notifications[0],
                stories[0], notifications[2],
                projects[1],
                stories[2], notifications[1])

        for (i in expected.indices) {
            assertThat(adapter[i], equalTo(expected[i]))
        }
    }

    @Test
    fun addingNotifiesChanges() {
        adapter.addNotification(notifications[3])
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 3
         */
        verify(observer).onItemRangeInserted(0, 3)

        adapter.addNotification(notifications[1])
        /*
          [ ] Project 0
          [ ]   Story 1
          [ ]     Notification 3
          [3] Project 1
          [4]   Story 2
          [5]     Notification 1
         */
        verify(observer).onItemRangeInserted(3, 3)

        adapter.addNotification(notifications[2])
        /*
          [ ] Project 0
          [ ]   Story 1
          [ ]     Notification 3
          [3]   Story 0
          [4]     Notification 2
          [ ] Project 1
          [ ]   Story 2
          [ ]     Notification 1
         */
        verify(observer).onItemRangeInserted(3, 2)

        adapter.addNotification(notifications[0])
        /*
          [ ] Project 0
          [ ]   Story 1
          [ ]     Notification 3
          [3]     Notification 0
          [ ]   Story 0
          [ ]     Notification 2
          [ ] Project 1
          [ ]   Story 2
          [ ]     Notification 1
         */
        verify(observer).onItemRangeInserted(3, 1)
    }

    @Test
    fun removingItems() {
        var removed: List<Notification>
        var i = notifications.size
        while (i-- > 0) adapter.addNotification(notifications[i])
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 4
          [3]     Notification 3
          [4]     Notification 0
          [5]   Story 0
          [6]     Notification 2
          [7] Project 1
          [8]   Story 2
          [9]     Notification 1
         */
        verify(observer, never()).onItemRangeRemoved(anyInt(), anyInt())

        removed = adapter.removeItem(3)
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 4
          [-]     Notification 3
          [3]     Notification 0
          [4]   Story 0
          [5]     Notification 2
          [6] Project 1
          [7]   Story 2
          [8]     Notification 1
         */
        assertThat(adapter.itemCount, `is`(9))
        assertThat(removed, contains(notifications[3]))
        verify(observer).onItemRangeRemoved(3, 1)

        removed = adapter.removeItem(1)
        /*
          [0] Project 0
          [-]   Story 1
          [ ]     Notification 4
          [ ]     Notification 0
          [1]   Story 0
          [2]     Notification 2
          [3] Project 1
          [4]   Story 2
          [5]     Notification 1
         */
        assertThat(adapter.itemCount, `is`(6))
        assertThat(removed, contains(notifications[4], notifications[0]))
        verify(observer).onItemRangeRemoved(1, 3)

        removed = adapter.removeItem(5)
        /*
          [0] Project 0
          [1]   Story 0
          [2]     Notification 2
          [ ] Project 1
          [ ]   Story 2
          [-]     Notification 1
         */
        assertThat(adapter.itemCount, `is`(3))
        assertThat(removed, contains(notifications[1]))
        verify(observer).onItemRangeRemoved(3, 3)

        removed = adapter.removeItem(0)
        /*
          [-] Project 0
          [ ]   Story 0
          [ ]     Notification 2
         */
        assertThat(adapter.itemCount, `is`(0))
        assertThat(removed, contains(notifications[2]))
        verify(observer).onItemRangeRemoved(0, 3)
    }

    @Test
    fun moreRemovingItems() {
        val project = buildProject(0)
        val story = buildStory(0)
        val notifications = arrayOf(
                buildNotification(0, story, project),
                buildNotification(1, story, project))

        adapter.addNotification(notifications[0])
        adapter.addNotification(notifications[1])

        /*
          [0] Project 0
          [1]   Story 0
          [2]     Notification 0
          [3]     Notification 1
         */
        assertThat(adapter.removeItem(2), contains(notifications[0]))
        assertThat(adapter.removeItem(2), contains(notifications[1]))
        assertThat(adapter.itemCount, `is`(0))
    }

    private fun buildProject(id: Long): Project {
        val project = Project()
        project.id = id
        project.kind = "project"
        return project
    }

    private fun buildStory(id: Long): Story {
        val story = Story()
        story.id = id
        story.kind = "story"
        return story
    }

    private fun buildNotification(id: Long, story: Story, project: Project): Notification {
        val notification = Notification()
        notification.id = id
        notification.kind = "notification"
        notification.story = story
        notification.project = project
        return notification
    }
}