package com.heroup.bacexams.ui


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.heroup.bacexams.adapter.MaterialAdapter
import com.heroup.bacexams.R
import com.heroup.bacexams.adapter.NavigationAdapter
import com.heroup.bacexams.models.NavItem
import com.heroup.bacexams.ui.intro.IntroActivity
import com.heroup.bacexams.util.Constants
import kotlinx.android.synthetic.main.fragment_home.*
import android.net.Uri
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.heroup.bacexams.models.Material
import com.heroup.bacexams.models.Orient
import com.heroup.bacexams.ui.exam.MainViewModel
import com.heroup.bacexams.util.Resource
import kotlinx.android.synthetic.main.loading_state.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

	private val model:MainViewModel by sharedViewModel()
	private val sharedPref: SharedPreferences by inject()


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment

		if (!sharedPref.contains(Constants.orient)){
			val intent= Intent(activity, IntroActivity::class.java)
			startActivity(intent)
			return null
		}

		val view = inflater.inflate(R.layout.fragment_home, container, false)
		setHasOptionsMenu(true)
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		title_bar.text = getString(R.string.titlebar,sharedPref.getString(Constants.orientName,""))

		val materialAdapter = MaterialAdapter<Material>{ material  ->

			model.currentMaterial  = material
			val action = HomeFragmentDirections.actionHomeFragmentToExamFragment()
			this.findNavController().navigate(action)
		}

		materials.apply {
			adapter = materialAdapter
			layoutManager = LinearLayoutManager(activity)
		}
		val orient=sharedPref.getString(Constants.orient,"opc") ?: "opc"
		// Transformation
		model.orient = orient

		//set retry
		error_retry.setOnClickListener {
			model.retryMaterial()
		}

		model.getMaterials().observe(viewLifecycleOwner, Observer{

			when(it){

				is Resource.Success -> {
					materialAdapter.submitList(it.data)
					errorVisibility(View.GONE)
					progressBar.visibility =View.GONE
				}

				is Resource.Error ->{
					errorVisibility(View.VISIBLE)
					progressBar.visibility =View.GONE
				}

				is Resource.Loading -> {
					errorVisibility(View.GONE)
					progressBar.visibility =View.VISIBLE
				}

			}


		})
		val orientAdapter = MaterialAdapter<Orient>{ _orient ->
			// orient adapted to material
			sharedPref.edit().let {
				it.putString(Constants.orient, _orient.shortName)
				it.putString(Constants.orientName, _orient.name)
				it.apply()
			}
			model.orient = _orient.shortName
		}

		home_orients_list.apply {
			adapter = orientAdapter
			layoutManager = LinearLayoutManager(activity)
		}

		model.getOrients().observe(viewLifecycleOwner, Observer {
			when(it){
				is Resource.Success -> {
					orientAdapter.submitList(it.data)
				}
				is Resource.Loading -> {

				}
				else ->{
					Snackbar.make(view,"وقع خطا غير متوقع",Snackbar.LENGTH_LONG).show()
				}
			}

		})


		val navAdapter = NavigationAdapter()
		home_other.apply {
			adapter = navAdapter
			layoutManager = LinearLayoutManager(activity)
		}

		val navList = mutableListOf<NavItem>()
		navList.add(NavItem("تقييم التطبيق" , "#ffc107" ,null,android.R.drawable.star_on,false){
			browse("https://play.google.com/store/apps/details?id=com.heroup.bacexams")
		})
		navList.add(NavItem("تابعنا على فايسبوك" , "#4267b2" ,null,R.drawable.ic_facebook,false){
			val default = "https://www.facebook.com/Mr.Alpha7/"
			browse(sharedPref.getString("facebook-page",default) ?: default)
		})
		navList.add(NavItem("انضم الى الكروب" , "#76a9ea" ,null,R.drawable.ic_twitter,false){
			val default = "https://www.facebook.com/groups/302957533546396"
			browse(sharedPref.getString("facebook-group",default) ?: default)

		})
		navList.add(NavItem("تواصل مع زملائك" , "#FF20B038" ,null,R.drawable.ic_whatsapp,false){
			val default = "https://wa.me/212619550170"

			browse(sharedPref.getString("$orient-whatsapp",default) ?: default)

		})
		navAdapter.submitList(navList)

	}

	private fun browse(url:String){
		val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		startActivity(browserIntent)
	}
	private fun errorVisibility(visibility:Int){
		error_image.visibility = visibility
		error_message.visibility = visibility
		error_retry.visibility = visibility
	}

	override fun onResume() {
		super.onResume()
		// rest year and mode
		model.resetExam()
	}


	override fun onDestroy() {
		super.onDestroy()
		Log.e("Home","destroy")
	}

}
