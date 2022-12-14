package sk.stu.fei.mobv.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sk.stu.fei.mobv.databinding.BarsListItemBinding
import sk.stu.fei.mobv.domain.Bar

class BarsListItemAdapter (
    private val barsListItemEventListener: BarsListItemEventListener
) : ListAdapter<Bar, BarsListItemAdapter.BarsListItemViewHolder>(DiffCallback) {

    class BarsListItemViewHolder(var binding: BarsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(barsListItemEventListener: BarsListItemEventListener, bar: Bar) {
            binding.bar = bar
            binding.barsListItemEventListener = barsListItemEventListener
        }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Bar>() {
        override fun areItemsTheSame(oldItem: Bar, newItem: Bar): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Bar, newItem: Bar): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarsListItemViewHolder {
        return BarsListItemViewHolder(
            BarsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BarsListItemViewHolder, position: Int) {
        val bar: Bar = getItem(position)
        holder.bind(barsListItemEventListener, bar)
    }

    override fun onCurrentListChanged(
        previousList: MutableList<Bar>,
        currentList: MutableList<Bar>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        barsListItemEventListener.onListChanged()
    }
}

class BarsListItemEventListener(
    val onItemClickListener: (bar: Bar) -> Unit,
    val onListChangedListener: () -> Unit
){
    fun onItemClick(bar: Bar) = onItemClickListener(bar)
    fun onListChanged() = onListChangedListener()

}