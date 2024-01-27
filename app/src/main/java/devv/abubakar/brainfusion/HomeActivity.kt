package devv.abubakar.brainfusion

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import devv.abubakar.brainfusion.adapter.LevelAdapter
import devv.abubakar.brainfusion.adapter.RuleAdapter
import devv.abubakar.brainfusion.databinding.ActivityHomeBinding
import devv.abubakar.brainfusion.model.Level
import devv.abubakar.brainfusion.model.Rule

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var ruleAdapter: RuleAdapter
    private lateinit var userLevelReference: DatabaseReference
    private lateinit var levelArrayList: ArrayList<Level>
    private var totalScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rulesProgressbar.visibility = View.VISIBLE
        binding.levelsProgressBar.visibility = View.VISIBLE

        binding.refreshLayout.setOnRefreshListener {
            setupRulesRecyclerView()
            setupLevelRecyclerView()
            binding.refreshLayout.isRefreshing = false
        }

        setupRulesRecyclerView()
        setupLevelRecyclerView()
        syncUserScore()
    }

    private fun setupLevelRecyclerView() {
        binding.levelsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            levelArrayList = arrayListOf()
            fetchLevelDataFromFirebase()
        }
    }

    private fun fetchLevelDataFromFirebase() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val anonymousUserId = auth.currentUser?.uid
        val firebaseDatabase = FirebaseDatabase.getInstance()
        userLevelReference = firebaseDatabase.getReference("user").child(anonymousUserId!!)
            .child("levels")

        userLevelReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    levelArrayList.addAll(snapshot.children.map { it.getValue(Level::class.java)!! })
                    binding.levelsProgressBar.visibility = View.GONE
                    binding.levelsRecyclerView.adapter = LevelAdapter(levelArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Something went wrong")
            }
        })
    }

    private fun setupRulesRecyclerView() {
        val ruleRef = FirebaseDatabase.getInstance().reference.child("rules").child("rulesList")
        binding.rulesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            ruleAdapter = RuleAdapter()
            adapter = ruleAdapter
            fetchRuleDataFromFirebase(ruleRef)
        }
    }

    private fun fetchRuleDataFromFirebase(ruleRef: DatabaseReference) {
        ruleRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rulesList = dataSnapshot.children.mapNotNull { it.getValue(Rule::class.java) }
                ruleAdapter.setData(rulesList)
                binding.rulesProgressbar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast("Error: ${databaseError.message}")
            }
        })
    }

    private fun syncUserScore() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val anonymousUserId = auth.currentUser?.uid
        val levelReference = FirebaseDatabase.getInstance().getReference("user").child(anonymousUserId!!)
            .child("levels")

        levelReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                calculateScore(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Something went wrong")
            }
        })
    }

    private fun calculateScore(levelCount: Int) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val anonymousUserId = auth.currentUser?.uid

        for (levelNumber in 1..levelCount) {
            val levelRef = FirebaseDatabase.getInstance().getReference("user").child(anonymousUserId!!)
                .child("levels").child("level$levelNumber")

            levelRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val questionScore = snapshot.child("score").getValue(Int::class.java)
                        questionScore?.let { totalScore += it }
                        if (levelNumber == levelCount) {
                            updateScore(totalScore)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Not yet implemented")
                }
            })
        }
    }

    private fun updateScore(totalScore: Int) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val anonymousUserId = auth.currentUser?.uid
        val userScoreRef = FirebaseDatabase.getInstance().getReference("user").child(anonymousUserId!!)
            .child("score")
        userScoreRef.setValue(totalScore)
        binding.userEarnedPointsText.text = totalScore.toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
    }
}
