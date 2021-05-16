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
import com.example.mensajesapp.Model.Chat;
import com.example.mensajesapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {

    private Context context;
    private List<Chat> mChat;
    private String imgURL;

    FirebaseUser fuser;

    public static final int MSG_TIPO_IZQ = 0;
    public static final int MSG_TIPO_DER = 1;



    //Constructor


    public MensajeAdapter(Context context, List<Chat> mChat, String imgURL) {
        this.context = context;
        this.mChat = mChat;
        this.imgURL = imgURL;
    }

    @NonNull
    @Override
    public MensajeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TIPO_DER){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_derecha,
                    parent,
                    false);
            return new MensajeAdapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_izquierda,
                    parent,
                    false);
            return new MensajeAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeAdapter.ViewHolder holder, int position) {


        Chat chat = mChat.get(position);
        holder.mostrar_mensaje.setText(chat.getMensaje());
        if (imgURL.equals("default")){
            holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(context)
                    .load(imgURL)
                    .into(holder.imagen_perfil);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mostrar_mensaje;
        public ImageView imagen_perfil;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mostrar_mensaje = itemView.findViewById(R.id.mostrar_mensaje);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);

        }


    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getEmisor().equals(fuser.getUid())){
            return MSG_TIPO_DER;
        }
        else{
            return MSG_TIPO_IZQ;
        }
    }
}
