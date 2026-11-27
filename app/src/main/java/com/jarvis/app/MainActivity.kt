package com.jarvis.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var tv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv = TextView(this)
        tv.text = "자비스 온라인"
        tv.textSize = 24f
        tv.gravity = Gravity.CENTER
        setContentView(tv)

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startJarvis()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS), 1
            )
        }
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(code, perms, results)
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startJarvis()
        } else {
            tv.text = "마이크 권한이 필요해요"
        }
    }

    private fun startJarvis() {
        try {
            startForegroundService(Intent(this, JarvisService::class.java))
            tv.text = "자비스 온라인\n서비스 시작 요청 완료"
        } catch (e: Throwable) {
            tv.text = "에러:\n" + e.toString()
        }
    }
}
