package com.example.mensajesapp.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mensajesapp.Model.Usuarios;
import com.example.mensajesapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import static android.app.Activity.RESULT_OK;


public class PerfilFragment extends Fragment {


    TextView NombreUsuario;
    ImageView imageView;
    DatabaseReference reference;
    FirebaseUser fuser;

    StorageReference storageReference;
    private static final int PEDIR_IMAGEN = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        imageView = view.findViewById(R.id.imagen_perfil2);
        NombreUsuario = view.findViewById(R.id.NombreUsuario);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MisUsuarios").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Usuarios user = dataSnapshot.getValue(Usuarios.class);
                NombreUsuario.setText(user.getUsuario());

                if (user.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getContext())
                            .load(user.getImageURL())
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeleccionarImagen();
            }
        });


        return view;
    }

    private void SeleccionarImagen()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PEDIR_IMAGEN);
    }

    private String getFileExtention(Uri uri)
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void SubirMiImagen(){


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Subiendo...");
        progressDialog.show();

        if(imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtention(imageUri));


            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){

                        throw  task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("MisUsuarios").child(fuser.getUid());


                        HashMap<String, Object> map = new HashMap<>();

                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        progressDialog.dismiss();
                    }else{

                        Toast.makeText(getContext(), "Hubo un error y no se pudo completar el proceso", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });


        }else
        {
            Toast.makeText(getContext(), "No hay ninguna imagen seleccionada", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PEDIR_IMAGEN && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();


            if (uploadTask != null && uploadTask.isInProgress()) {

                Toast.makeText(getContext(), "Subida en proceso...", Toast.LENGTH_SHORT).show();

            }
            else
            {
                SubirMiImagen();
            }
        }
    }
}