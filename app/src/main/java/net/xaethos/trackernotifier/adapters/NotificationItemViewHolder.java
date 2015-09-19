package net.xaethos.trackernotifier.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.xaethos.trackernotifier.R;

public class NotificationItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView initials;
    public final TextView title;
    public final TextView summary;

    public NotificationItemViewHolder(View itemView) {
        super(itemView);
        initials = (TextView) itemView.findViewById(R.id.initials);
        title = (TextView) itemView.findViewById(R.id.title);
        summary = (TextView) itemView.findViewById(R.id.summary);
    }
}
