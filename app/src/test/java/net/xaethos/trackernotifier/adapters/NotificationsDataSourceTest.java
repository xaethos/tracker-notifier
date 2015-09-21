package net.xaethos.trackernotifier.adapters;

import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.models.Project;
import net.xaethos.trackernotifier.models.Resource;
import net.xaethos.trackernotifier.models.Story;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class NotificationsDataSourceTest {

    NotificationsDataSource dataSource;
    DataSource.Observer observer;

    Project[] projects;
    Story[] stories;
    Notification[] notifications;

    @Before
    public void setUpDataSource() {
        observer = mock(DataSource.Observer.class);
        dataSource = new NotificationsDataSource();
        dataSource.setObserver(observer);
    }

    @Before
    public void setUpNotifications() throws Exception {
        projects = new Project[]{buildProject(0), buildProject(1)};
        stories = new Story[]{buildStory(0), buildStory(1), buildStory(2)};
        notifications = new Notification[]{ // The Tracker API would return these reversed
                buildNotification(0, stories[1], projects[0]),
                buildNotification(1, stories[2], projects[1]),
                buildNotification(2, stories[0], projects[0]),
                buildNotification(3, stories[1], projects[0]),
                buildNotification(4, stories[1], projects[0])
        };
    }

    @Test
    public void itemIdIsResourceHashcode() throws Exception {
        dataSource.addNotification(notifications[3]);
        assertThat(dataSource.getItemId(0), is((long) projects[0].hashCode()));
        assertThat(dataSource.getItemId(1), is((long) stories[1].hashCode()));
        assertThat(dataSource.getItemId(2), is((long) notifications[3].hashCode()));
    }

    @Test
    public void addingItemsOneByOne() throws Exception {
        assertThat(dataSource.getItemCount(), is(0));

        dataSource.addNotification(notifications[3]);
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 3
         */
        assertThat(dataSource.getItemCount(), is(3));

        dataSource.addNotification(notifications[2]);
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 3
          [3]   Story 0
          [4]     Notification 2
         */
        assertThat(dataSource.getItemCount(), is(5));

        dataSource.addNotification(notifications[1]);
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
        assertThat(dataSource.getItemCount(), is(8));

        dataSource.addNotification(notifications[0]);
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
        Resource[] expected = {projects[0],
                stories[1],
                notifications[3],
                notifications[0],
                stories[0],
                notifications[2],
                projects[1],
                stories[2],
                notifications[1],
        };

        for (int i = 0; i < expected.length; ++i) {
            assertThat(dataSource.getResource(i), equalTo(expected[i]));
        }
    }

    @Test
    public void addingNotifiesChanges() {
        dataSource.addNotification(notifications[3]);
        /*
          [0] Project 0
          [1]   Story 1
          [2]     Notification 3
         */
        verify(observer).notifyItemRangeInserted(0, 3);

        dataSource.addNotification(notifications[1]);
        /*
          [ ] Project 0
          [ ]   Story 1
          [ ]     Notification 3
          [3] Project 1
          [4]   Story 2
          [5]     Notification 1
         */
        verify(observer).notifyItemRangeInserted(3, 3);

        dataSource.addNotification(notifications[2]);
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
        verify(observer).notifyItemRangeInserted(3, 2);

        dataSource.addNotification(notifications[0]);
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
        verify(observer).notifyItemRangeInserted(3, 1);
    }

    @Test
    public void removingItems() {
        List<Notification> removed;
        int i = notifications.length;
        while (i-- > 0) dataSource.addNotification(notifications[i]);
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
        verify(observer, never()).notifyItemRangeRemoved(anyInt(), anyInt());

        removed = dataSource.removeItem(3);
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
        assertThat(dataSource.getItemCount(), is(9));
        assertThat(removed, contains(notifications[3]));
        verify(observer).notifyItemRangeRemoved(3, 1);

        removed = dataSource.removeItem(1);
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
        assertThat(dataSource.getItemCount(), is(6));
        assertThat(removed, contains(notifications[4], notifications[0]));
        verify(observer).notifyItemRangeRemoved(1, 3);

        removed = dataSource.removeItem(5);
        /*
          [0] Project 0
          [1]   Story 0
          [2]     Notification 2
          [ ] Project 1
          [ ]   Story 2
          [-]     Notification 1
         */
        assertThat(dataSource.getItemCount(), is(3));
        assertThat(removed, contains(notifications[1]));
        verify(observer).notifyItemRangeRemoved(3, 3);

        removed = dataSource.removeItem(0);
        /*
          [-] Project 0
          [ ]   Story 0
          [ ]     Notification 2
         */
        assertThat(dataSource.getItemCount(), is(0));
        assertThat(removed, contains(notifications[2]));
        verify(observer).notifyItemRangeRemoved(0, 3);
    }

    private Project buildProject(long id) {
        Project project = new Project();
        project.id = id;
        project.kind = "project";
        return project;
    }

    private Story buildStory(long id) {
        Story story = new Story();
        story.id = id;
        story.kind = "story";
        return story;
    }

    private Notification buildNotification(long id, Story story, Project project) {
        Notification notification = new Notification();
        notification.id = id;
        notification.kind = "notification";
        notification.story = story;
        notification.project = project;
        return notification;
    }
}