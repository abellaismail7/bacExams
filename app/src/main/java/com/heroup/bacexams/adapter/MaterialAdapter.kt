package com.heroup.bacexams.adapter


import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.heroup.bacexams.R
import com.heroup.bacexams.models.Item
import com.heroup.bacexams.models.Material
import kotlinx.android.synthetic.main.item_material.view.*

class MaterialAdapter<T>(val itemClickCallback: (T) -> Unit)  :
		ListAdapter<Item, MaterialAdapter<T>.ViewHolder>(DIFF_CALLBACK) {



	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.item_material, parent, false)
	)

	override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(getItem(position)) }

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		fun bind(item: Item) = itemView.apply {
			val color = Color.parseColor(item.color)

			material_name.text = item.name
			material_icon.load(item.icon)
			material_icon.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)

			if(item is Material){
				val  years =item.years
				material_description.text = context.getString(R.string.material_description,years[0],years.last())
			}

			setOnClickListener {
				@Suppress("UNCHECKED_CAST")
				itemClickCallback(item as T)
			}


		}

	}
	companion object{
		val  DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
			override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
				return oldItem.id == newItem.id
			}

			override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
				return oldItem.id == newItem.id
			}
		}
	}
}