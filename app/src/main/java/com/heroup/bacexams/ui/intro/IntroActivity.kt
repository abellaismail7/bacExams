package com.heroup.bacexams.ui.intro

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.size
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.heroup.bacexams.MainActivity
import com.heroup.bacexams.R
import com.heroup.bacexams.adapter.IntroAdapter
import com.heroup.bacexams.adapter.MaterialAdapter
import com.heroup.bacexams.models.Orient
import com.heroup.bacexams.models.Screen
import com.heroup.bacexams.util.Constants
import com.heroup.bacexams.util.Resource
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.loading_state.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroActivity : AppCompatActivity() {

    private val model:IntroViewModel by viewModel()

    private val sharedPref: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        val list= mutableListOf<Screen>()
        list.add(
                Screen(R.drawable.welcome_one,getString(R.string.welcome_one),getString(R.string.welcome_desc_one))
        )
        list.add(
                Screen(R.drawable.welcome_two,getString(R.string.welcome_two),getString(R.string.welcome_desc_two))
        )
        list.add(
                Screen(R.drawable.welcome_three,getString(R.string.welcome_three),getString(R.string.welcome_desc_three))
        )

        pager.adapter = IntroAdapter(list)

        intro_next.setOnClickListener {
            Log.e("Pager","${pager.currentItem} ${pager.size}")
            if (pager.currentItem < list.size -1 ) pager.currentItem += 1
        }

        intro_back.setOnClickListener {
            if(pager.currentItem != 0) pager.currentItem -= 1
        }

        var selectedView = intro_dot_1
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{
                        choose_orient.animate().setDuration(300).translationY(1000f).start()
                        intro_back.animate().setDuration(300).translationY(1000f).start()
                        selectedView.isSelected =false
                        selectedView =intro_dot_1
                        intro_dot_1.isSelected = true
                    }
                    1->{
                        intro_back.animate().setDuration(300).translationY(0f).start()
                        intro_next.animate().setDuration(300).translationY(0f).start()
                        choose_orient.animate().setDuration(300).translationY(1000f).start()
                        intro_motion.transitionToStart()
                        selectedView.isSelected =false
                        selectedView =intro_dot_2
                        intro_dot_2.isSelected = true
                    }
                    2->{
                        intro_next.animate().setDuration(300).translationY(1000f).start()
                        choose_orient.animate().setDuration(300).translationY(0f).start()
                        selectedView.isSelected =false
                        selectedView =intro_dot_3
                        intro_dot_3.isSelected = true
                    }
                }

            }
        })

        model.saveSocial().observe(this, Observer {
            val editor= sharedPref.edit()
             when(it){
                 is Resource.Success -> {
                     it.data.let {map ->
                         for((k,v) in map) editor.putString(k,v).apply()
                     }

                 }
                 is Resource.Error ->{

                 }
                 is Resource.Loading ->{

                 }
             }
        })

        val materialAdapter = MaterialAdapter<Orient>{ orient ->
            sharedPref.edit().let {
                it.putString(Constants.orient, orient.shortName)
                it.putString(Constants.orientName, orient.name)
                it.apply()
            }
            startActivity(Intent(this, MainActivity::class.java))
        }
        orients.apply {
            adapter = materialAdapter
            layoutManager = LinearLayoutManager(this@IntroActivity)
        }

        error_retry.setOnClickListener {
            model.getOrients()
        }

        model.getOrients().observe(this, Observer{
            when(it){
                is Resource.Success -> {
                    materialAdapter.submitList(it.data)
                    progressBar.visibility = View.GONE
                    errorVisibility(View.GONE)

                }

                is Resource.Error ->{
                    progressBar.visibility = View.GONE
                    errorVisibility(View.VISIBLE)
                }
                is Resource.Loading -> {
                    errorVisibility(View.GONE)
                    progressBar.visibility = View.VISIBLE
                }

            }
        })

    }

    private fun errorVisibility(visibility:Int){
        error_image.visibility = visibility
        error_message.visibility = visibility
        error_retry.visibility = visibility
    }
}
