package com.example.rft_2.Uitel

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.example.rft_2.R

@GlideModule
class AppGlideModule : AppGlideModule()

fun getProgressDrawable(c:Context): CircularProgressDrawable{
    return CircularProgressDrawable(c).apply {
        strokeWidth = 5f
        centerRadius = 40f
        start()
    }
}

/** set Images */

fun ImageView.loadImage(uri: String?, progressDrawable:CircularProgressDrawable){
    val option = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_launcher)

    Glide.with(context)
        .setDefaultRequestOptions(option)
        .load(uri)
        .into(this)

}
@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url:String?){
    view.loadImage(url, getProgressDrawable(view.context))
}