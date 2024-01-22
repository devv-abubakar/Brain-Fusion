@file:Suppress("DEPRECATION")

package devv.abubakar.brainfusion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Check if there is a current user (whether anonymous or not)
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.isAnonymous) {

            handleSignedInGuestUser(splashDelayWithAccount)

        } else {

            createUserAccount()
        }
    }


    private fun createUserAccount() {
// Sign in the user anonymously
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in success, update UI with the signed-in user's information
                    val userAuth = auth.currentUser
                    val anonymousUserId = userAuth?.uid

                    val firebaseDatabase = FirebaseDatabase.getInstance()
                    val firebaseReference: DatabaseReference =
                        firebaseDatabase.getReference("user")
                    val user = User(anonymousUserId.toString(), "", 0)
                    firebaseReference.child(anonymousUserId.toString()).setValue(user)

                    setUpLevelsForNewUsers()



                } else {
                    Toast.makeText(
                        this,
                        "Something went wrong please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Handle the error or proceed with alternative logic
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
                        val level = if (i == 1) {
                            Level(anonymousUserId, i, "Unlock", 0)
                        } else {
                            Level(anonymousUserId, i, "Locked", 0)
                        }

                        // Assuming you want to set values to Firebase, you need to push the data.
                        userLevelReference.child("level$i").setValue(level)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Splash, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
        handleSignedInGuestUser(splashDelayWithoutAccount)
    }

    private fun handleSignedInGuestUser(splashDelay: Long) {
        Handler().postDelayed({

            val mainIntent = Intent(this@Splash, MainActivity::class.java)
            startActivity(mainIntent)
            finish()

        }, splashDelay)
    }
}