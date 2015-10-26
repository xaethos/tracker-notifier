package net.xaethos.trackernotifier.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.StoryActivity;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.models.Project;
import net.xaethos.trackernotifier.models.Resource;
import net.xaethos.trackernotifier.models.Story;

public class ResourceViewHolder extends RecyclerView.ViewHolder {

    public final ImageView icon;
    public final TextView title;
    public final TextView summary;

    private final OnClickShowStory mShowStoryListener;

    public static ResourceViewHolder create(
            LayoutInflater layoutInflater, int viewType, ViewGroup parent) {
        final View itemView = layoutInflater.inflate(viewType, parent, false);
        return new ResourceViewHolder(itemView, viewType);
    }

    private ResourceViewHolder(View itemView, int viewType) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        title = (TextView) itemView.findViewById(R.id.title);
        summary = (TextView) itemView.findViewById(R.id.summary);

        switch (viewType) {
        case R.layout.item_notification:
        case R.layout.item_story:
            mShowStoryListener = new OnClickShowStory();
            itemView.setOnClickListener(mShowStoryListener);
            break;
        default:
            mShowStoryListener = null;
        }
    }

    public void bind(Resource item) {
        if (item instanceof Notification) {
            final Notification notification = (Notification) item;
            mShowStoryListener.story = notification.story;
            title.setText(notification.message);
            if (TextUtils.isEmpty(notification.context)) {
                summary.setVisibility(View.GONE);
            } else {
                summary.setText(notification.context);
                summary.setVisibility(View.VISIBLE);
            }
        } else if (item instanceof Story) {
            mShowStoryListener.story = (Story) item;
            title.setText(((Story) item).name);
            switch (((Story) item).story_type) {
            case Story.TYPE_FEATURE:
                icon.setImageResource(R.drawable.ic_star_black_18dp);
                break;
            case Story.TYPE_CHORE:
                icon.setImageResource(R.drawable.ic_settings_black_18dp);
                break;
            case Story.TYPE_BUG:
                icon.setImageResource(R.drawable.ic_bug_report_black_18dp);
                break;
            case Story.TYPE_RELEASE:
                icon.setImageResource(R.drawable.ic_flag_black_18dp);
                break;
            default:
                icon.setImageBitmap(null);
            }
        } else if (item instanceof Project) {
            title.setText(((Project) item).name);
        }
    }

    private static class OnClickShowStory implements View.OnClickListener {
        public Story story;

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            context.startActivity(StoryActivity.forStory(context, story));
        }
    }

}
