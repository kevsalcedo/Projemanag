package kesam.learning.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import kesam.learning.projemanag.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private var binding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
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
        binding?.tvAppTitleName?.typeface = typeFace

        binding?.btnSignUpIntro?.setOnClickListener{
            val intent = Intent(this@IntroActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignInIntro?.setOnClickListener{
            val intent = Intent(this@IntroActivity, SignInActivity::class.java)
            startActivity(intent)
        }


    }
}