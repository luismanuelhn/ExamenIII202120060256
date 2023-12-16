package com.example.exameniii202120060256;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolderEntrevista extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderEntrevista.ClickListener mClickListener;

    public interface  ClickListener{
        void onItemClick(View view,int position);
        void OnItemLongClick(View view, int position);

    }

    public void setOnClickListener(ViewHolderEntrevista.ClickListener clickListener){
        mClickListener=clickListener;
    }
    public ViewHolderEntrevista(@NonNull View itemView) {
        super(itemView);
        mView=itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view,getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.OnItemLongClick(view,getBindingAdapterPosition());
                return true;
            }
        });
    }

    public void seteoDatos(Context context,String descripcion,String imagen){
        ImageView imageViewEntrevista;
        TextView DescripcionEntrevista;

        imageViewEntrevista=mView.findViewById(R.id.imageViewEntrevista);
        DescripcionEntrevista=mView.findViewById(R.id.DescripcionEntrevista);

        DescripcionEntrevista.setText(descripcion);

        try{
            Picasso.get().load(imagen).into(imageViewEntrevista);
        }
        catch (Exception e){
            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }
}
