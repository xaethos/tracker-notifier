package net.xaethos.trackernotifier.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.models.Story;

import java.text.DecimalFormat;

import rx.Subscription;

public class StoryDetailsFragment extends BaseResourceFragment<Story> {

    Subscription mContentSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_story_details, container, false);

        TextView idView = (TextView) rootView.findViewById(R.id.text_id);
        TextView pointsView = (TextView) rootView.findViewById(R.id.text_points);
        TextView nameView = (TextView) rootView.findViewById(R.id.text_name);
        TextView descriptionView = (TextView) rootView.findViewById(R.id.text_description);

        mContentSubscription = observeResource().subscribe((story -> {
            idView.setText(story == null ? null : String.valueOf(story.id));
            pointsView.setText(
                    story == null ? null : new DecimalFormat("0.##").format(story.estimate));
            nameView.setText(story == null ? null : story.name);
            descriptionView.setText(story == null ? null : story.description);
        }));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        mContentSubscription.unsubscribe();
        super.onDestroyView();
    }

}
