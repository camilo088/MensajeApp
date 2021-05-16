package com.example.mensajesapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mensajesapp.MensajesActivity;
import com.example.mensajesapp.Model.Usuarios;
import com.example.mensajesapp.R;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private Context context;
    private List<Usuarios> mUsuarios;

    //Constructor


    public UsuarioAdapter(Context context, List<Usuarios> mUsuarios) {
        this.context = context;
        this.mUsuarios = mUsuarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usuario_item,
                parent,
                false);
        return new UsuarioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Usuarios usuarios = mUsuarios.get(position);
        holder.usuario.setText(usuarios.getUsuario());

        if (usuarios.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(context)
                    .load(usuarios.getImageURL())
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MensajesActivity.class);
                i.putExtra("usuarioid", usuarios.getId());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsuarios.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView usuario;
        public ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.textViewUser);
            imageView = itemView.findViewById(R.id.imageView);

        }


    }

}
