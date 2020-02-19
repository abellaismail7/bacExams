package com.heroup.bacexams.models

import androidx.annotation.DrawableRes

data class NavItem(val title:String,
                   val color:String,
                   val description:String?,
                   @DrawableRes val icon:Int,
                   val exist:Boolean = true,
                   val action : () -> Unit )