package devv.abubakar.brainfusion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devv.abubakar.brainfusion.adapter.LevelAdapter
import devv.abubakar.brainfusion.adapter.RuleAdapter
import devv.abubakar.brainfusion.databinding.ActivityHomeBinding
import devv.abubakar.brainfusion.model.Level
import devv.abubakar.brainfusion.model.Rule

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    //code for rules
    private lateinit var ruleRef: DatabaseReference
    private lateinit var ruleAdapter: RuleAdapter

    //code for level
    private lateinit var userLevelReference: DatabaseReference
    private lateinit var levelArrayList: ArrayList<Level>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rulesProgressbar.visibility = View.VISIBLE
        binding.levelsProgressBar.visibility = View.VISIBLE
        setupRulesRecyclerView()
        setupLevelRecyclerView()
        syncUserScore()
    }


    private fun setupLevelRecyclerView() {
        // Initialize RecyclerView and its adapter
        binding.levelsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.levelsRecyclerView.setHasFixedSize(true)
        binding.levelsRecyclerView.isNestedScrollingEnabled = false
        levelArrayList = arrayListOf()

        fetchLevelDataFromFirebase()

    }

    private fun fetchLevelDataFromFirebase() {

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val userAuth = auth.currentUser
        val anonymousUserId = userAuth?.uid

        val firebaseDatabase = FirebaseDatabase.getInstance()
        userLevelReference = firebaseDatabase.getReference("user")
            .child(anonymousUserId!!)
            .child("levels")

        userLevelReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (levelSnapshot in snapshot.children) {
                        val level = levelSnapshot.getValue(Level::class.java)
                        levelArrayList.add(level!!)
                    }
                    binding.levelsProgressBar.visibility = View.GONE
                    binding.levelsRecyclerView.adapter = LevelAdapter(levelArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

        })

    }


    private fun setupRulesRecyclerView() {
        ruleRef = FirebaseDatabase.getInstance().reference.child("rules").child("rulesList")

        // Initialize RecyclerView and its adapter
        binding.rulesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        ruleAdapter = RuleAdapter()
        binding.rulesRecyclerView.adapter = ruleAdapter

        // Fetch data from Firebase and update the RecyclerView
        fetchRuleDataFromFirebase()
    }

    private fun fetchRuleDataFromFirebase() {
        ruleRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rulesList = mutableListOf<Rule>()

                for (ruleSnapshot in dataSnapshot.children) {
                    val rule = ruleSnapshot.getValue(Rule::class.java)
                    rule?.let {
                        rulesList.add(it)
                    }
                }
                ruleAdapter.setData(rulesList)
                binding.rulesProgressbar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                val errorMessage = "Error: ${databaseError.message}"
                Toast.makeText(this@HomeActivity, errorMessage, Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun syncUserScore() {

    }

}