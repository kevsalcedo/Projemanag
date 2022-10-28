package kesam.learning.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kesam.learning.projemanag.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }


}