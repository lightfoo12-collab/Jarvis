package com.jarvis.app

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "자비스 온라인"
        tv.textSize = 28f
        tv.gravity = Gravity.CENTER
        setContentView(tv)
    }
}
