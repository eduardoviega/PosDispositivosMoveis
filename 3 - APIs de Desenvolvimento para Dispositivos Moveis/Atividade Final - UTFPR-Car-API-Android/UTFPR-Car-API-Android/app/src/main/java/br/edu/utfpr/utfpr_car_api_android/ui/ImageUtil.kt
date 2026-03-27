package br.edu.utfpr.utfpr_car_api_android.ui

import android.widget.ImageView
import br.edu.utfpr.utfpr_car_api_android.R
import com.squareup.picasso.Picasso

fun ImageView.loadUrl(url: String?) {
    if (url.isNullOrEmpty()) {
        Picasso.get()
            .load(R.drawable.ic_error)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_error)
            .transform(CircleTransform())
            .into(this)
        return
    }

    Picasso.get()
        .load(url)
        .placeholder(R.drawable.ic_download)
        .error(R.drawable.ic_error)
        .transform(CircleTransform())
        .into(this)
}