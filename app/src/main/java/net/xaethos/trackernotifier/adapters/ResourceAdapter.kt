package net.xaethos.trackernotifier.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.xaethos.trackernotifier.models.Resource
import java.util.*

class ResourceAdapter<R : Resource, VH : ResourceViewHolder<R>>(
        val itemViewType: Int = 0,
        val viewHolderFactory: (LayoutInflater, Int, ViewGroup) -> VH) : RecyclerView.Adapter<VH>() {

    private val items = ArrayList<R>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            viewHolderFactory(LayoutInflater.from(parent.context), viewType, parent);

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(get(position))

    override fun getItemId(position: Int) = get(position).id

    override fun getItemViewType(position: Int) = itemViewType

    override fun getItemCount() = items.size

    operator fun get(position: Int) = items[position]

    fun setResources(resources: Collection<R>?) {
        items.clear()
        if (resources != null) items.addAll(resources)
        notifyDataSetChanged()
    }

}
