package com.heroup.bacexams.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heroup.bacexams.R
import com.heroup.bacexams.models.Screen
import kotlinx.android.synthetic.main.item_intro.view.*

class IntroAdapter(private val intros:List<Screen>) : RecyclerView.Adapter<IntroAdapter.IntroViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = IntroViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.item_intro, parent, false)
	)


	override fun onBindViewHolder(holder: IntroViewHolder, position: Int) = holder.bindTo(intros[position])


	override fun getItemCount(): Int = intros.size

	inner class IntroViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

		fun bindTo(screen:Screen) = with(itemView){
			item_intro_title.text = screen.title
			item_intro_description.text = screen.description
			item_intro_image.setImageResource(screen.image)

		}

	}
}