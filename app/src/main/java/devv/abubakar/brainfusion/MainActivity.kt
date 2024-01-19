package devv.abubakar.brainfusion

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import devv.abubakar.brainfusion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val ANIMATION_DURATION = 2000
        private const val INTERNET_CHECK_DELAY = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkInternetConnection()


    }

    private fun setupButtonClick() {
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun checkInternetConnection() {
        binding.root.postDelayed({
            if (!isInternetAvailable(this)) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                finish()
            } else {
                startBounceAnimations()
                setupButtonClick()
            }
        }, INTERNET_CHECK_DELAY.toLong())
    }

    private fun startBounceAnimations() {
        bounceAnimation(binding.brainAnimation, ANIMATION_DURATION)
        bounceAnimation(binding.textTagLine, ANIMATION_DURATION + 50)
        bounceAnimation(binding.textAppDescription, ANIMATION_DURATION + 100)
        bounceAnimation(binding.btnStart, ANIMATION_DURATION + 150)
    }

    private fun bounceAnimation(view: View, duration: Int) {
        YoYo.with(Techniques.Bounce)
            .duration(duration.toLong())
            .repeat(10)
            .playOn(view)
    }
}
