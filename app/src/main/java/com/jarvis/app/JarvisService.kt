package com.jarvis.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class JarvisService : Service() {

    private var recognizer: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var nm: NotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel("jarvis", "Jarvis", NotificationManager.IMPORTANCE_LOW)
        )
        startForeground(1, buildNotification("자비스 대기 중", "\"자비스\"라고 불러보세요"),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        startListening()
        return START_STICKY
    }

    private fun buildNotification(title: String, text: String): Notification {
        return Notification.Builder(this, "jarvis")
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }

    private fun update(title: String, text: String) {
        nm.notify(1, buildNotification(title, text))
    }

    private fun startListening() {
        handler.post {
            try {
                recognizer?.destroy()
                recognizer = SpeechRecognizer.createSpeechRecognizer(this)
                recognizer?.setRecognitionListener(listener)
                val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                recognizer?.startListening(i)
            } catch (e: Throwable) {
                update("자비스 에러", e.toString())
            }
        }
    }

    private fun restart(delay: Long) {
        handler.postDelayed({ startListening() }, delay)
    }

    private fun check(text: String) {
        val t = text.replace(" ", "")
        if (t.contains("자비스")) {
            update("자비스 호출됨!", "들은 말: $text")
        }
    }

    private val listener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            list?.firstOrNull()?.let { check(it) }
            restart(300)
        }
        override fun onPartialResults(partialResults: Bundle?) {
            val list = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            list?.firstOrNull()?.let { check(it) }
        }
        override fun onError(error: Int) {
            restart(1000)
        }
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        recognizer?.destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
