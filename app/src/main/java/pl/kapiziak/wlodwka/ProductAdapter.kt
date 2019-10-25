package pl.kapiziak.wlodwka

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView

import pl.kapiziak.wlodwka.model.Product


class ProductAdapter(private val products: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_row, parent, false)


        return ViewHolder(view)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ProductAdapter.ViewHolder, position: Int) {

        holder.productTitle.text = products[position].name
        holder.productValidity.text = products[position].validity
        //Picasso.get().load(products[position].image).into(holder.image)
        //holder.productTitle.text = products[position].product.name
        //holder.productValidity.text = products[position].
        //holder.title = products[position].date.subSequence(0,10)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val image: ImageView = itemView.findViewById(R.id.photo)
        val productTitle: TextView = itemView.findViewById(R.id.product_title)
        val productValidity: TextView = itemView.findViewById(R.id.product_validity)
        //val detail: TextView = itemView.findViewById(R.id.detailRow)
    }
}