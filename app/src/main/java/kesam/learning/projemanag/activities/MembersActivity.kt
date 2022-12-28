package kesam.learning.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kesam.learning.projemanag.databinding.ActivityMainBinding
import kesam.learning.projemanag.databinding.ActivityMembersBinding

class MembersActivity : BaseActivity() {

    private var binding: ActivityMembersBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)


    }


}