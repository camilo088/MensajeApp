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

public class IngresarActivity extends AppCompatActivity {


    EditText usuarioETLogin, contrasenaETLogin;
    Button ingresarBtn, registrarseBtn;

    //FIREBASE
    FirebaseAuth auth;
    FirebaseUser firebaseUser;


    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //Verificar cuenta del usuario
        if (firebaseUser != null){
            Intent i = (new Intent(IngresarActivity.this,MainActivity.class));
            startActivity(i);
            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar);


        usuarioETLogin = findViewById(R.id.UsuarioLoginEditText);
        contrasenaETLogin = findViewById(R.id.ContrasenaLoginEditText);
        ingresarBtn = findViewById(R.id.buttonIngresar);
        registrarseBtn = findViewById(R.id.buttonRegistrarseLog);

        //Firebase
        auth = FirebaseAuth.getInstance();


        //Al oprimir el botón de registrarse
        registrarseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IngresarActivity.this, RegistrarseActivity.class);
                startActivity(i);
            }
        });


        //Al oprimir el botón ingresar
        ingresarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario_text = usuarioETLogin.getText().toString();
                String contrasena_text = contrasenaETLogin.getText().toString();

                //Verificar si se rellenaron los espacios
                if (TextUtils.isEmpty(usuario_text) || TextUtils.isEmpty(contrasena_text)) {
                    Toast.makeText(IngresarActivity.this, "Por favor rellene todos los espacios", Toast.LENGTH_SHORT).show();
                }else{
                    auth.signInWithEmailAndPassword(usuario_text,contrasena_text)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent i = new Intent(IngresarActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(IngresarActivity.this, "Error al momento de ingresar con su usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}