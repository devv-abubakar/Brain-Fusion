package devv.abubakar.brainfusion.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import devv.abubakar.brainfusion.R
import devv.abubakar.brainfusion.model.Question

class QuestionAdapter(private val arrayList: ArrayList<Question>) :
    RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_recyclerview_item, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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



    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionCount: TextView = itemView.findViewById(R.id.question_count_textview)
        val questionImage: ImageView = itemView.findViewById(R.id.question_image)
        val optionImage: ImageView = itemView.findViewById(R.id.options_image)
        val swipeUpText: TextView = itemView.findViewById(R.id.swipe_up_text)

    }
}