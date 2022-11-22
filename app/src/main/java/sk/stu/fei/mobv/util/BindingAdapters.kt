package sk.stu.fei.mobv.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.ui.adapter.BarsListItemAdapter


@BindingAdapter("listData")
fun setList(recyclerView: RecyclerView, listData: List<Bar>?) {
    val adapter = recyclerView.adapter as BarsListItemAdapter
    adapter.submitList(listData)
}