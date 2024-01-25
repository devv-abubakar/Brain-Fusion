package devv.abubakar.brainfusion.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import devv.abubakar.brainfusion.QuestionsActivity
import devv.abubakar.brainfusion.R
import devv.abubakar.brainfusion.model.Level

class LevelAdapter(private val arrayList: ArrayList<Level>) :
    RecyclerView.Adapter<LevelAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.level_recyclerview_item, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = arrayList[position]

        holder.levelCount.text = "Level ${currentItem.number}"
        holder.levelStatus.text = currentItem.status
        holder.levelScore.text = currentItem.score.toString() + "/9"

        holder.levelLayout.setOnClickListener {
            if (currentItem.status == "Unlocked") {
                val intent = Intent(holder.itemView.context, QuestionsActivity::class.java)
                intent.putExtra("level", currentItem.number.toString())
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "Level is Locked", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val levelLayout: MaterialCardView = itemView.findViewById(R.id.level_background_layout)
        val levelCount: TextView = itemView.findViewById(R.id.level_count)
        val levelStatus: TextView = itemView.findViewById(R.id.level_status)
        val levelScore: TextView = itemView.findViewById(R.id.level_score)
    }
}