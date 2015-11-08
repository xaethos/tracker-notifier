package net.xaethos.trackernotifier.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.models.Story
import rx.Subscription
import java.text.DecimalFormat

class StoryDetailsFragment : BaseResourceFragment<Story>() {

    private var subscription: Subscription? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.content_story_details, container, false)

        val idView = rootView.findViewById(R.id.text_id) as TextView
        val pointsView = rootView.findViewById(R.id.text_points) as TextView
        val nameView = rootView.findViewById(R.id.text_name) as TextView
        val descriptionView = rootView.findViewById(R.id.text_description) as TextView

        subscription = resourceObservable?.subscribe(({ story ->
            idView.text = if (story == null) null else story.id.toString()
            pointsView.text = if (story == null) null else DecimalFormat("0.##").format(story.estimate.toDouble())
            nameView.text = story?.name
            descriptionView.text = story?.description
        }))

        return rootView
    }

    override fun onDestroyView() {
        subscription?.unsubscribe()
        super.onDestroyView()
    }

}
