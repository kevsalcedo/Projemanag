package kesam.learning.projemanag.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kesam.learning.projemanag.R
import kesam.learning.projemanag.models.Board

open class BoardItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_board,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){

            Glide
                .with(context)
                .load(model.image) // URI of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_board_place_holder) // A default place holder
                .into(holder.itemView.findViewById(R.id.iv_item_board_image)) // the view in which the image will be loaded.

            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_created_by).text = "Created By : ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    /**
     * A function to bind the onclickListener.
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

}