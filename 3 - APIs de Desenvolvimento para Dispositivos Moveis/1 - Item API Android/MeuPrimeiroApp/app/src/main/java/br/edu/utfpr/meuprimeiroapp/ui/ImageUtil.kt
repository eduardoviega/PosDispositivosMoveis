package br.edu.utfpr.meuprimeiroapp.ui

import CircleTransform
import android.widget.ImageView
import br.edu.utfpr.meuprimeiroapp.R
import com.squareup.picasso.Picasso

fun ImageView.loadUrl(url: String) {
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.ic_download)
        .error(R.drawable.ic_error)
        .transform(CircleTransform())
        .into(this)
}