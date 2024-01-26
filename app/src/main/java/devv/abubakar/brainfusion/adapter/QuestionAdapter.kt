package devv.abubakar.brainfusion.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devv.abubakar.brainfusion.R
import devv.abubakar.brainfusion.model.LevelScore
import devv.abubakar.brainfusion.model.Question

class QuestionAdapter(
    private val arrayList: ArrayList<Question>,
    private val context: Context,
    private val userId: String
) :
    RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_recyclerview_item, parent, false)
        return ViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Reset the checkbox states
        holder.checkBoxA.isChecked = false
        holder.checkBoxB.isChecked = false
        holder.checkBoxC.isChecked = false

        val currentQuestion = arrayList[position]

        val questionCount = currentQuestion.number
        val questionNumber = questionCount.substring(8)
        holder.questionCount.text = " $questionNumber"

        if (position == 8) {
            holder.swipeUpText.visibility = View.INVISIBLE
        }

        Glide.with(holder.itemView.context)
            .load(currentQuestion.question)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
            .into(holder.questionImage)



        Glide.with(holder.itemView.context)
            .load(currentQuestion.option)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
            .into(holder.optionImage)


        holder.checkBoxA.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.checkBoxB.isChecked = false
                holder.checkBoxC.isChecked = false
                val isCorrect = isOptionCorrect(position, "a")
                updatePoints(isCorrect, position)
            }
        }

        holder.checkBoxB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.checkBoxA.isChecked = false
                holder.checkBoxC.isChecked = false
                val isCorrect = isOptionCorrect(position, "b")
                updatePoints(isCorrect, position)
            }
        }

        holder.checkBoxC.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.checkBoxA.isChecked = false
                holder.checkBoxB.isChecked = false
                val isCorrect = isOptionCorrect(position, "c")
                updatePoints(isCorrect, position)
            }
        }
    }

    private fun isOptionCorrect(adapterPosition: Int, selectedOption: String): Boolean {
        val question = arrayList[adapterPosition]
        return question.correctOption == selectedOption
    }

    private fun updatePoints(isCorrect: Boolean, adapterPosition: Int) {
        val currentQuestion = arrayList[adapterPosition]


        val point: Int = (if (isCorrect) 1 else 0)
        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference: DatabaseReference =
            firebaseDatabase.getReference("user").child(userId).child("levels")
                .child("level${currentQuestion.level}").child("questionsScore")
                .child("question" + adapterPosition.plus(1))

        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val score =
                        LevelScore(currentQuestion.level, adapterPosition + 1, point, userId)
                    databaseReference.setValue(score)

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }

            })

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionCount: TextView = itemView.findViewById(R.id.question_count_textview)
        val questionImage: ImageView = itemView.findViewById(R.id.question_image)
        val optionImage: ImageView = itemView.findViewById(R.id.options_image)
        val swipeUpText: TextView = itemView.findViewById(R.id.swipe_up_text)

        val checkBoxA: CheckBox = itemView.findViewById(R.id.option_a_checkbox)
        val checkBoxB: CheckBox = itemView.findViewById(R.id.option_b_checkbox)
        val checkBoxC: CheckBox = itemView.findViewById(R.id.option_c_checkbox)

    }
}