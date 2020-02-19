package com.heroup.bacexams.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.heroup.bacexams.R
import com.heroup.bacexams.models.NavItem
import kotlinx.android.synthetic.main.item_nav.view.*

class NavigationAdapter  :
		ListAdapter<NavItem, NavigationAdapter.ViewHolder>(DIFF_CALLBACK) {


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.item_nav, parent, false)
	)

	override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(getItem(position)) }

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		fun bind(item: NavItem) = itemView.apply {
			val color = Color.parseColor(item.color)
			nav_card.setCardBackgroundColor(color)
			nav_title.text = item.title
			item.description?.let {
				nav_description.visibility =View.VISIBLE
				nav_description.text = it
			}
			if (item.exist) nav_soon.visibility =View.VISIBLE
			nav_icon.load(item.icon)
			itemView.setOnClickListener {
				item.action()
			}


		}

	}
	companion object{
		val  DIFF_CALLBACK = object : DiffUtil.ItemCallback<NavItem>() {
			override fun areItemsTheSame(oldItem: NavItem, newItem: NavItem): Boolean {
				return oldItem.title == newItem.title
			}

			override fun areContentsTheSame(oldItem: NavItem, newItem: NavItem): Boolean {
				return oldItem == newItem
			}
		}
	}
}