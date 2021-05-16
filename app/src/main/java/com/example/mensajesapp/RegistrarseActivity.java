package com.example.mensajesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrarseActivity extends AppCompatActivity {

    //WIDGETS
    EditText usuarioET, contrasenaET, correoET, edadET;
    Button registrarseBtn;


    //FIREBASE
    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        //INICIALIZAR WIDGETS:
        usuarioET = findViewById(R.id.usuarioEditText);
        contrasenaET = findViewById(R.id.contrasenaEditText);
        correoET = findViewById(R.id.correoEditText);
        edadET = findViewById(R.id.edadEditText);
        registrarseBtn = findViewById((R.id.buttonRegistrarse));

        // Firebase Auth
       // auth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();

        // Añadir Event Listener al botón de registrarse
        registrarseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario_text = usuarioET.getText().toString();
                String correo_text = correoET.getText().toString();
                String contrasena_text = contrasenaET.getText().toString();
                String edad_text = edadET.getText().toString();
                if (TextUtils.isEmpty(usuario_text) || TextUtils.isEmpty(correo_text) || TextUtils.isEmpty(contrasena_text) || TextUtils.isEmpty(edad_text)){
                    Toast.makeText(RegistrarseActivity.this, "Por favor rellene todos los espacios", Toast.LENGTH_SHORT).show();
                }else{
                    RegistrarseAhora(usuario_text,contrasena_text,correo_text,edad_text);
                }


            }
        });

    }

    private void RegistrarseAhora(final String usuario, String contrasena, String correo, String edad)
    {
        auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid  = firebaseUser.getUid();

                            myRef = FirebaseDatabase.getInstance()
                                    .getReference("MisUsuarios")
                                    .child(userid);
                            //HASHMAPS
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("usuario",usuario + " - " + edad);
                            hashMap.put("imageURL","default");

                            //Abrir MainActivity después de registrarse existosamente
                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent i = new Intent(RegistrarseActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(RegistrarseActivity.this, "Contraseña o correo no válidos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}