@file:Suppress("DEPRECATION")

package devv.abubakar.brainfusion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import devv.abubakar.brainfusion.model.Level
import devv.abubakar.brainfusion.model.User

class Splash : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val splashDelayWithAccount: Long = 2500
    private val splashDelayWithoutAccount: Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkInternet()
    }

    private fun checkInternet() {
        if (!isInternetAvailable(this)) {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        } else {
            checkUserLoginStatus()
        }
    }

    private fun checkUserLoginStatus() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.isAnonymous) {
            handleSignedInGuestUser(splashDelayWithAccount)
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val userAuth = auth.currentUser
                val anonymousUserId = userAuth?.uid

                val firebaseReference = FirebaseDatabase.getInstance().getReference("user")
                val user = User(anonymousUserId.toString(), "", 0)
                firebaseReference.child(anonymousUserId.toString()).setValue(user)

                setUpLevelsForNewUsers()

            } else {
                showErrorToast()
            }
        }
    }

    private fun setUpLevelsForNewUsers() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val levelsReference = firebaseDatabase.getReference("levels")

        val userAuth = auth.currentUser
        val anonymousUserId = userAuth?.uid

        levelsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val levelCount = snapshot.childrenCount.toInt()

                if (anonymousUserId != null) {
                    val userLevelReference = firebaseDatabase.getReference("user")
                        .child(anonymousUserId)
                        .child("levels")

                    for (i in 1..levelCount) {
                        val level = Level(anonymousUserId, i, if (i == 1) "Unlocked" else "Locked", 0)
                        userLevelReference.child("level$i").setValue(level)
                    }
                    handleSignedInGuestUser(splashDelayWithoutAccount)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showErrorToast()
            }
        })
    }

    private fun handleSignedInGuestUser(splashDelay: Long) {
        Handler().postDelayed({
            startActivity(Intent(this@Splash, MainActivity::class.java))
            finish()
        }, splashDelay)
    }

    private fun showErrorToast() {
        Toast.makeText(this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show()
    }
}
