package devv.abubakar.brainfusion.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import devv.abubakar.brainfusion.R
import devv.abubakar.brainfusion.model.Rule

class RuleAdapter : RecyclerView.Adapter<RuleAdapter.RuleViewHolder>() {

    private val ruleList: MutableList<Rule> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newRules: List<Rule>) {
        ruleList.clear()
        ruleList.addAll(newRules)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rule_recyclerview_item, parent, false)
        return RuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        holder.bind(ruleList.getOrNull(position))
    }

    override fun getItemCount(): Int {
        return ruleList.size
    }

    inner class RuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.rule_title_text)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.rule_description_text)

        fun bind(rule: Rule?) {
            rule?.let {
                titleTextView.text = it.title
                descriptionTextView.text = it.description
            }
        }
    }
}
