package net.xaethos.trackernotifier.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.models.Project;
import net.xaethos.trackernotifier.models.Resource;
import net.xaethos.trackernotifier.models.Story;

public class ResourceViewHolder extends RecyclerView.ViewHolder {

    public final TextView initials;
    public final TextView title;
    public final TextView summary;

    public ResourceViewHolder(View itemView) {
        super(itemView);
        initials = (TextView) itemView.findViewById(R.id.initials);
        title = (TextView) itemView.findViewById(R.id.title);
        summary = (TextView) itemView.findViewById(R.id.summary);
    }

    public void bind(Resource item) {
        if (item instanceof Notification) {
            title.setText(((Notification) item).message);
            summary.setText(((Notification) item).context);
            initials.setText(((Notification) item).performer.initials);
        } else if (item instanceof Story) {
            title.setText(((Story) item).name);
        } else if (item instanceof Project) {
            title.setText(((Project) item).name);
        }
    }
}
