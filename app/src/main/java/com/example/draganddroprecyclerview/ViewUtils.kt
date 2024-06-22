package com.example.draganddroprecyclerview

import android.view.View

fun View?.setViewGone() {
    if (this != null) {
        this.visibility = View.GONE
    }
}

fun View?.setViewVisible() {
    if (this != null) {
        this.visibility = View.VISIBLE
    }
}

fun View?.setViewInVisible() {
    if (this != null) {
        this.visibility = View.INVISIBLE
    }
}