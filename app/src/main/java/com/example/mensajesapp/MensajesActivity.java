package com.example.mensajesapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mensajesapp.Adapter.MensajeAdapter;
import com.example.mensajesapp.Model.Chat;
import com.example.mensajesapp.Model.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MensajesActivity extends AppCompatActivity {


    TextView username;
    ImageView imageView;

    RecyclerView recyclerViewy;
    EditText msg_editText;
    ImageButton enviarBtn;

    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    MensajeAdapter mensajeAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    String userid;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);


        imageView = findViewById(R.id.imageview_profile);
        username = findViewById(R.id.usernamey);

        enviarBtn = findViewById(R.id.btn_enviar);
        msg_editText = findViewById(R.id.text_send);


        //
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        intent = getIntent();
        userid = intent.getStringExtra("usuarioid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MisUsuarios").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuarios user = snapshot.getValue(Usuarios.class);
                username.setText(user.getUsuario());

                if (user.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(MensajesActivity.this)
                            .load(user.getImageURL())
                            .into(imageView);
                }

                leerMensaje(fuser.getUid(),userid, user.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        enviarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msg_editText.getText().toString();
                if(!msg.equals("")){
                    mandarMensaje(fuser.getUid(),userid,msg);
                }
                else{
                    Toast.makeText(MensajesActivity.this, "No puede enviar un mensaje vacío", Toast.LENGTH_SHORT).show();
                }

                msg_editText.setText("");
            }
        });
    }

    private void mandarMensaje(String emisor, String receptor, String mensaje)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor", emisor);
        hashMap.put("receptor", receptor);
        hashMap.put("mensaje", mensaje);

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(fuser.getUid())
                .child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if(!datasnapshot.exists())
                {
                    chatRef.child("id").setValue(userid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void leerMensaje(String miId, String usuarioId, String imageurl)
    {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceptor().equals(miId) && chat.getEmisor().equals(usuarioId) || chat.getReceptor().equals(usuarioId) && chat.getEmisor().equals(miId))
                    {
                        mChat.add(chat);
                    }

                    mensajeAdapter = new MensajeAdapter(MensajesActivity.this, mChat, imageurl);
                    recyclerView.setAdapter(mensajeAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}