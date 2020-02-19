package com.heroup.bacexams.util

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.annotation.IdRes
import android.widget.RadioButton
import android.widget.CompoundButton
import androidx.core.view.ViewCompat
import android.content.res.TypedArray
import android.util.Log
import kotlin.math.max


class GridRadioGroup @JvmOverloads constructor(
		context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

	private val columnWidth = 200
	private var mCheckedId = -1
	private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
	private var mChildOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
	private var mPassThroughListener: PassThroughHierarchyChangeListener? = null

	private var mProtectFromCheckedChange = false

	init {
		columnCount = 1
		init()
	}

	private fun init() {
		mChildOnCheckedChangeListener = CheckedStateTracker()
		mPassThroughListener = PassThroughHierarchyChangeListener()
		super.setOnHierarchyChangeListener(mPassThroughListener)
	}

	override fun onMeasure(widthSpec: Int, heightSpec: Int) {
		super.onMeasure(widthSpec, heightSpec)

		val width = MeasureSpec.getSize(widthSpec)
		if (columnWidth > 0 && width > 0) {
			val totalSpace = width - paddingRight - paddingLeft
			val mColumnCount = max(1, totalSpace / columnWidth)
			Log.e("YY","#$mColumnCount")
			columnCount = 4
		} else {
			columnCount = 4
		}
	}

	override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
		if (child is RadioButton) {
			if (child.isChecked) {
				mProtectFromCheckedChange = true
				if (mCheckedId != -1) {
					setCheckedStateForView(mCheckedId, false)
				}
				mProtectFromCheckedChange = false
				setCheckedId(child.id)
			}
		}
		super.addView(child, index, params)
	}

	private fun setCheckedId(@IdRes id: Int) {
		mCheckedId = id
		mOnCheckedChangeListener?.onCheckedChanged(this, mCheckedId)

	}

	private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
		val checkedView = findViewById<View>(viewId)
		if (checkedView != null && checkedView is RadioButton) {
			checkedView.isChecked = checked
		}
	}


	@IdRes
	fun getCheckedRadioButtonId() = mCheckedId


	interface OnCheckedChangeListener {
		fun onCheckedChanged(group: GridRadioGroup, @IdRes checkedId: Int)
	}

	private inner class CheckedStateTracker : CompoundButton.OnCheckedChangeListener {
		override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
			// prevents from infinite recursion
			if (mProtectFromCheckedChange) {
				return
			}
			mProtectFromCheckedChange = true
			if (mCheckedId != -1) {
				setCheckedStateForView(mCheckedId, false)
			}
			mProtectFromCheckedChange = false
			val id = buttonView.id
			setCheckedId(id)
		}
	}

	private inner class PassThroughHierarchyChangeListener : OnHierarchyChangeListener {
		private val mOnHierarchyChangeListener: OnHierarchyChangeListener? = null
		/**
		 * {@inheritDoc}
		 */
		override fun onChildViewAdded(parent: View, child: View) {
			if (parent === this@GridRadioGroup && child is RadioButton) {
				var id = child.getId()
				// generates an id if it's missing
				if (id == View.NO_ID) {
					id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) View.generateViewId()
					else ViewCompat.generateViewId()

					child.setId(id)
				}
				child.setOnCheckedChangeListener(mChildOnCheckedChangeListener)
			}
			mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
		}

		/**
		 * {@inheritDoc}
		 */
		override fun onChildViewRemoved(parent: View, child: View) {
			if (parent === this@GridRadioGroup && child is RadioButton) {
				child.setOnCheckedChangeListener(null)
			}
			mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
		}
	}

	override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
		return LayoutParams(context, attrs)
	}

	override fun generateDefaultLayoutParams(): GridLayout.LayoutParams {
		val lp = LayoutParams()
		lp.width = GridLayout.LayoutParams.MATCH_PARENT
		lp.height = GridLayout.LayoutParams.WRAP_CONTENT
		return  lp
	}

	/**
	 * {@inheritDoc}
	 */
	override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
		return p is GridLayout.LayoutParams
	}



	override fun getAccessibilityClassName(): CharSequence {
		return GridRadioGroup::class.java.name
	}

	class LayoutParams(c: Context, attrs: AttributeSet) : GridLayout.LayoutParams(c, attrs) {


		override fun setBaseAttributes(a: TypedArray,
		                               widthAttr: Int, heightAttr: Int) {
			width = if (a.hasValue(widthAttr)) {
				a.getLayoutDimension(widthAttr, "layout_width")
			} else {
				ViewGroup.LayoutParams.WRAP_CONTENT
			}
			height = if (a.hasValue(heightAttr)) {
				a.getLayoutDimension(heightAttr, "layout_height")
			} else {
				ViewGroup.LayoutParams.WRAP_CONTENT
			}
		}
	}

}