package kesam.learning.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.FirebaseApp
import kesam.learning.projemanag.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        FirebaseApp.initializeApp(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val typeFace: Typeface = Typeface.createFromAsset(assets,"carbon bl.ttf")

        binding?.tvAppName?.typeface = typeFace


        // Here we will launch the Intro Screen after the splash screen using the handler.
        // As using handler the splash screen will disappear after what we give to the handler.
        // Adding the handler to after the a task after some delay.
        Handler(Looper.getMainLooper()).postDelayed(
            {
                // Start the Intro Activity
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))

                // Call this when your activity is done and should be closed.
                finish()
            },
            2500) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.

    }

}