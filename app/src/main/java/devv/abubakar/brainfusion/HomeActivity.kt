package devv.abubakar.brainfusion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devv.abubakar.brainfusion.adapter.RuleAdapter
import devv.abubakar.brainfusion.databinding.ActivityHomeBinding
import devv.abubakar.brainfusion.model.Rule

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var rulesRef: DatabaseReference
    private lateinit var adapter: RuleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rulesRef = FirebaseDatabase.getInstance().reference.child("rules").child("rulesList")

        // Initialize RecyclerView and its adapter
        binding.rulesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = RuleAdapter()
        binding.rulesRecyclerView.adapter = adapter

        // Fetch data from Firebase and update the RecyclerView
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        rulesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rulesList = mutableListOf<Rule>()

                for (ruleSnapshot in dataSnapshot.children) {
                    val rule = ruleSnapshot.getValue(Rule::class.java)
                    rule?.let {
                        rulesList.add(it)
                    }
                }

                adapter.setData(rulesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                val errorMessage = "Error: ${databaseError.message}"
                Toast.makeText(this@HomeActivity, errorMessage, Toast.LENGTH_SHORT).show()

            }
        })
    }
}