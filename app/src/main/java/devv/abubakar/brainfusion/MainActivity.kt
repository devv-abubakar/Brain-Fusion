package devv.abubakar.brainfusion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import devv.abubakar.brainfusion.databinding.ActivityMainBinding
import devv.abubakar.brainfusion.model.Level

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

    private fun checkInternetConnection() {
        binding.root.postDelayed({
            if (!isInternetAvailable(this)) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                finish()
            } else {
                startBounceAnimations()
                setupButtonClick()
                syncLevels()
            }
        }, INTERNET_CHECK_DELAY.toLong())
    }

    private fun setupButtonClick() {
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
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

    private fun syncLevels() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val levelsReference = firebaseDatabase.getReference("levels")
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val userAuth = auth.currentUser
        val anonymousUserId = userAuth?.uid

        levelsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val levelCount = snapshot.childrenCount.toInt()

                anonymousUserId?.let { userId ->
                    for (i in 1..levelCount) {
                        val userLevelReference = firebaseDatabase.getReference("user")
                            .child(userId)
                            .child("levels").child("level$i")

                        userLevelReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists()) {
                                    val level = Level(userId, i, "Locked", 0)
                                    userLevelReference.setValue(level)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                showToast("Error: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Something went wrong")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}
