package devv.abubakar.brainfusion

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devv.abubakar.brainfusion.adapter.QuestionAdapter
import devv.abubakar.brainfusion.databinding.ActivityQuestionsBinding
import devv.abubakar.brainfusion.model.Question

class QuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionsBinding
    private lateinit var level: String
    private lateinit var questionsArrayList: ArrayList<Question>

    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        level = intent.getStringExtra("level").toString()
        binding.levelTextview.text = "Level $level"

        setUpQuestions()

        binding.levelSubmitTextview.setOnClickListener {
            submitResult()
        }
    }

    private fun setUpQuestions() {

        auth = FirebaseAuth.getInstance()
        val userAuth = auth.currentUser
        val anonymousUserId = userAuth?.uid

        // Initialize RecyclerView and its adapter
        binding.questionRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.questionRecyclerview.setHasFixedSize(true)
        binding.questionRecyclerview.isNestedScrollingEnabled = false
        questionsArrayList = arrayListOf()

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.questionRecyclerview)

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val questionRef =
            firebaseDatabase.getReference("levels").child(level).child("questions")

        questionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (questionSnapshot in snapshot.children) {
                        try {
                            // Attempt to convert the value to a Question object
                            val question = questionSnapshot.getValue(Question::class.java)

                            // Check if the conversion was successful and the question is not null
                            if (question != null) {
                                questionsArrayList.add(question)
                            } else {
                                // Handle the case where the conversion failed
                                Log.e(
                                    TAG,
                                    "Failed to convert question data at ${questionSnapshot.key} to Question object."
                                )
                            }
                        } catch (e: DatabaseException) {
                            // Handle any exceptions that may occur during the conversion
                            Log.e(
                                TAG,
                                "Error converting question data at ${questionSnapshot.key}",
                                e
                            )
                        }
                    }

                    binding.questionRecyclerview.adapter =
                        QuestionAdapter(
                            questionsArrayList,
                            this@QuestionsActivity,
                            anonymousUserId.toString()
                        )
                    binding.questionProgressBar.visibility = View.GONE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@QuestionsActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun submitResult() {
        auth = FirebaseAuth.getInstance()
        val userAuth = auth.currentUser
        val anonymousUserId = userAuth?.uid

        // Database reference
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userReference = firebaseDatabase.getReference("user").child(anonymousUserId.toString())
        val levelReference = userReference.child("levels").child("level$level")


        val questionsScoreReference = levelReference.child("questionsScore")


        val numberOfQuestions = 9

        var totalScore = 0

        for (questionNumber in 1..numberOfQuestions) {
            val questionReference = questionsScoreReference.child("question$questionNumber")

            questionReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val questionScore =
                        dataSnapshot.child("questionScore").getValue(Int::class.java)


                    if (questionScore != null) {
                        totalScore += questionScore
                    }

                    if (questionNumber == numberOfQuestions) {
                        levelReference.child("score").setValue(totalScore)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@QuestionsActivity,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
        startActivity(Intent(this, HomeActivity::class.java))
    }
}