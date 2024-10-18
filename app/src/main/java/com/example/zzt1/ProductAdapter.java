package com.example.zzt1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<Product> productList;
    private final Context context;

    // Constructor for the adapter
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each product item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Get the product at the current position
        Product product = productList.get(position);

        // Set the product name and price to the corresponding views
        holder.productName.setText(product.getProduct_name());
        holder.productPrice.setText("R " + product.getPrice());

        // Change the text colors to your desired color
        holder.productName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.YY));
        holder.productPrice.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.YY));

        // Use the AsyncTask to load the image from the URL without Glide
        if (product.getImage_name() != null && !product.getImage_name().isEmpty()) {
            new DownloadImageTask(holder.productImage).execute(product.getImage_name());
        } else {
            // Set a placeholder image if no image URL is provided
            holder.productImage.setImageResource(R.drawable.sweetwine);  // Use a default image in your drawable
        }

        // Handle click on product image to open ProductDetailActivity
        holder.productImage.setOnClickListener(v -> {
            // Create an intent to open ProductDetailActivity
            Intent intent = new Intent(context, ProductDetailActivity.class);

            // Pass the product name instead of the document ID
            intent.putExtra("PRODUCT_NAME", product.getProduct_name());

            // Start the ProductDetailActivity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder class for holding references to the views
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.price);
            productImage = itemView.findViewById(R.id.image_name);
        }
    }

    // AsyncTask to load the image from a URL in the background
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap productImage = null;
            try {
                InputStream in = new URL(urlDisplay).openStream();
                productImage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return productImage;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the downloaded image to the ImageView
            if (result != null) {
                bmImage.setImageBitmap(result);
            } else {
                // Set a default image if the download fails
                bmImage.setImageResource(R.drawable.sweetwine);  // Use a default image in your drawable folder
            }
        }
    }
}
