package net.xaethos.trackernotifier.adapters;

import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.models.Project;
import net.xaethos.trackernotifier.models.Resource;
import net.xaethos.trackernotifier.models.Story;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
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
        /*
        Build notifications for the following UI:
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
        projects = new Project[]{buildProject(0), buildProject(1)};
        stories = new Story[]{buildStory(0), buildStory(1), buildStory(2)};
        notifications = new Notification[]{ // The Tracker API would return them reversed
                buildNotification(0, stories[1], projects[0]),
                buildNotification(1, stories[2], projects[1]),
                buildNotification(2, stories[0], projects[0]),
                buildNotification(3, stories[1], projects[0]),
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
    public void testAddingItemsOneByOne() throws Exception {
        assertThat(dataSource.getItemCount(), is(0));

        dataSource.addNotification(notifications[3]);
        assertThat(dataSource.getItemCount(), is(3));

        dataSource.addNotification(notifications[2]);
        assertThat(dataSource.getItemCount(), is(5));

        dataSource.addNotification(notifications[1]);
        assertThat(dataSource.getItemCount(), is(8));

        dataSource.addNotification(notifications[0]);

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
        verify(observer).notifyItemRangeInserted(0, 3);

        dataSource.addNotification(notifications[2]);
        verify(observer).notifyItemRangeInserted(3, 2);

        dataSource.addNotification(notifications[1]);
        verify(observer).notifyItemRangeInserted(5, 3);

        dataSource.addNotification(notifications[0]);
        verify(observer).notifyItemRangeInserted(3, 1);
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