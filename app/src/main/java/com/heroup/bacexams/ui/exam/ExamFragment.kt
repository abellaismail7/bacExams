package com.heroup.bacexams.ui.exam



import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.RadioButton
import androidx.lifecycle.Observer
import coil.api.load
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.heroup.bacexams.R
import kotlinx.android.synthetic.main.fragment_exam.*
import com.google.android.material.snackbar.Snackbar
import com.heroup.bacexams.util.FileResource
import kotlinx.android.synthetic.main.loading_state.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import com.google.android.gms.ads.MobileAds
import com.heroup.bacexams.BuildConfig


/**
 * A simple [Fragment] subclass.
 */
class ExamFragment : Fragment() {

	private val model:MainViewModel by sharedViewModel()

	private val material by lazy {
		model.currentMaterial
	}
	private val path:String
		get() = "${material.shortName}-$year-$mode.pdf"
	private var  year = 0
	private var mode = "n"
	private lateinit var mInterstitialAd: InterstitialAd
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_exam, container, false)
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		activity?.let { activity ->

			MobileAds.initialize(activity) {}
			mInterstitialAd = InterstitialAd(activity)
			mInterstitialAd.apply {
				this.adUnitId = if(BuildConfig.DEBUG) TEST_AD_UNIT_ID else AD_UNIT_ID
				loadAd(AdRequest.Builder().build())
				adListener = object: AdListener() {
					override fun onAdLoaded() {
						show()
					}

					override fun onAdLeftApplication() {
						// wld Elhram
					}
				}
			}

			// 100% years !null
			year = model.year ?: material.years[0]
			mode = model.mode ?: "n"

			// change toolbar title
			exam_title_bar.text =getString(R.string.exam_title,material.name,year)
			exam_material_icon.background.setColorFilter(Color.parseColor(material.color), PorterDuff.Mode.SRC_ATOP)
			exam_material_icon.load(material.icon)

			//load from network or memory
			model.path = path



			// observe live data triggered when path changed
			model.getPdfBytes().observe(viewLifecycleOwner, Observer {
				when(it){
					// Success
					is FileResource.FromCache->{
						errorVisibility(View.GONE)
						progressBar.visibility =View.GONE
						download.translationX = 0f

						toggleDownloadButton(true)
						Handler().postDelayed({
							pdfView.fromFile(it.file).load()
							pdfView.animate().translationX(0f).start()
						},200)


						download.setOnClickListener { _ ->
								Snackbar.make(view,"هل ترغب في حذف الامتحان", Snackbar.LENGTH_LONG)
										.setAction("حدف"){ _ ->
											it.file.delete()
											toggleDownloadButton(false)
										}
										.setActionTextColor(Color.parseColor("#ffd400"))
										.show()



						}
					}
					is FileResource.FromNetwork ->{
						errorVisibility(View.GONE)
						progressBar.visibility =View.GONE
						download.translationX = 0f
						pdfView.animate().translationX(0f).start()

						toggleDownloadButton(false)
						if (it.byteArray.isNotEmpty())
							pdfView.fromBytes(it.byteArray).load()
						download.setOnClickListener {_ ->
							Snackbar.make(view,"هل ترغب في حفظ الامتحان", Snackbar.LENGTH_LONG)
									.setAction("حفظ"){_ ->
										storeFile(it.byteArray,it.file)
										toggleDownloadButton(true)
									}
									.setActionTextColor(Color.parseColor("#ffd400"))
									.show()
						}
					}
					// Failure
					is FileResource.Error ->{
						errorVisibility(View.VISIBLE)
						progressBar.visibility =View.GONE
						error_image.setImageResource(it.illustrationId)
						error_message.setText(it.msg)
					}
					// Loading
					is FileResource.Loading -> {
						pdfView.animate().translationX(pdfView.width.toFloat()).start()
						errorVisibility(View.GONE)
						progressBar.visibility =View.VISIBLE
						download.translationX = -3000f
					}
				}
			})

			// add radio button to grid layout
			material.years.forEach {
				val btn = makeBtn(activity)
				btn.text = it.toString()
				exam_years.addView(btn)

				if(it == year){
					btn.isChecked = true
				}
			}



			load_pdf.setOnClickListener {
				// get new options
				val id = exam_years.getCheckedRadioButtonId()
				year = activity.findViewById<RadioButton>(id).text.toString().toInt()
				mode = if(exam_mode.isChecked) "r" else "n"

				// load only if something changed
				if (model.year !=  year || model.mode != mode ){
					// change year and mode for configuration
					model.mode = mode
					model.year= year

					model.path = path
				}
				// change title in toolbar
				exam_title_bar.text =getString(R.string.exam_title,material.name,year)
				// transitionToStart : popup
				motion_exam.transitionToStart()
			}

		}


	}

	private fun toggleDownloadButton(exist:Boolean){
		if (exist){
			download.setIconResource(R.drawable.ic_delete)
			download.text = "حذف"
			download.strokeWidth = 0
			download.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2d2b36"))
			download.setTextColor(Color.WHITE)
			download.iconTint = ColorStateList.valueOf(Color.WHITE)
		}
		else{
			download.setIconResource(R.drawable.ic_download)
			download.text = "تحميل"
			download.strokeWidth = 5
			download.setBackgroundColor(Color.TRANSPARENT)
			download.setTextColor(Color.BLACK)
			download.iconTint = ColorStateList.valueOf(Color.BLACK)
		}

	}

	private fun storeFile(bytes: ByteArray, file: File) {
		file.writeBytes(bytes)
	}

	private fun errorVisibility(visibility:Int){
		error_image.visibility = visibility
		error_message.visibility = visibility
		error_retry.visibility = visibility
	}

	private fun makeBtn(context:Context): RadioButton {
		val btn = RadioButton(context)
		btn.setButtonDrawable(R.drawable.radio)
		btn.setBackgroundResource(R.drawable.radio)
		val layoutParams = GridLayout.LayoutParams()
		layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT
		layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT
		layoutParams.rightMargin = 10
		layoutParams.topMargin = 10
		btn.layoutParams = layoutParams
		return btn
	}

	companion object {

		private const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
		private const val AD_UNIT_ID = "ca-app-pub-7525961676199755/3923802527"

	}

}
