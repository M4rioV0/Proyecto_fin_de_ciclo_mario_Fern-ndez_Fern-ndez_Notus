package com.example.proyectofinciclo.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectofinciclo.fragments.NotasFragment;
import com.example.proyectofinciclo.R;
import com.example.proyectofinciclo.database.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearNotasActivity extends AppCompatActivity {

    EditText editTextTitulo;
    EditText editTextContenido;
    FloatingActionButton salirGuardarNota;
    Button buttonDelete;
    Button buttonAddImage;


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    String notaIdFirebase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_notas);

        //UI para crear notas
        editTextTitulo = findViewById(R.id.et_titulo_nota);
        editTextContenido = findViewById(R.id.et_contenido_nota);
        salirGuardarNota = findViewById(R.id.fab_exit_crear_notas_layout);
        buttonDelete = findViewById(R.id.btt_eliminar_nota);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (NotasFragment.editar==true){
            rellenarCampos();
        }


        salirGuardarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextContenido.getText().toString().isEmpty()||editTextTitulo.getText().toString().isEmpty()){
                    finish();
                }else{
                   if (NotasFragment.editar==true){
                       actualizarNota();
                       if (firebaseUser!=null){
                           actualizarNotaFirebase();
                       }
                   }else {
                       registrarNota();
                       if (firebaseUser!=null){
                           registrarNotaFireBase();
                       }
                   }
                    finish();
                }

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextContenido.getText().toString().isEmpty()||editTextTitulo.getText().toString().isEmpty()){
                    finish();
                }else{
                    if (NotasFragment.editar==true){
                        eliminarNota();
                        if (firebaseUser!=null){
                            eliminarNotaFirebase();
                        }
                        editTextTitulo.setText("");
                        editTextContenido.setText("");
                        finish();
                    }else {
                        finish();
                    }
                }
            }
        });

    }

    private void eliminarNotaFirebase() {
        DocumentReference documentReference = firebaseFirestore.collection("notes")
                .document(firebaseUser.getUid())
                .collection("myNotes").document(String.valueOf(NotasFragment.nota.getId()));

        documentReference
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CrearNotasActivity.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CrearNotasActivity.this, "Error al intentar eliminar la nota", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void actualizarNotaFirebase() {
        DocumentReference documentReference = firebaseFirestore
                .collection("notes")
                .document(firebaseUser.getUid())
                .collection("myNotes").document(String.valueOf(NotasFragment.nota.getId()));

        Map<String ,Object> note = new HashMap<>();
        String titulo = editTextTitulo.getText().toString();
        String contenido = editTextContenido.getText().toString();
        note.put("title",titulo);
        note.put("content",contenido);

        documentReference
                .update(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(CrearNotasActivity.this, "Titulo actualizado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(CrearNotasActivity.this, "fallo al actualizar el titulo", Toast.LENGTH_SHORT).show();
                    }
                });
        documentReference
                .update(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(CrearNotasActivity.this, "Contenido actualizado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(CrearNotasActivity.this, "fallo al actualizar el contenido", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrarNotaFireBase() {
        DocumentReference documentReference = firebaseFirestore
                .collection("notes")
                .document(firebaseUser.getUid())
                .collection("myNotes").document(notaIdFirebase);
        Map<String ,Object> note = new HashMap<>();
        note.put("title",editTextTitulo.getText().toString());
        note.put("content",editTextContenido.getText().toString());
        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CrearNotasActivity.this, "Nota guardada", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CrearNotasActivity.this, "error al guardar la nota", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rellenarCampos() {

        editTextTitulo.setText(NotasFragment.nota.getTitulo());
        editTextContenido.setText(NotasFragment.nota.getContenido());

    }

    public void registrarNota(){

        Toast.makeText(this,"registrar",Toast.LENGTH_LONG);

        SQLiteDatabase db = NotasFragment.conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.campoTitulo,editTextTitulo.getText().toString());
        values.put(Utilidades.campoContenido,editTextContenido.getText().toString());



        db.insert(Utilidades.tablaNotas,null,values);

        String select = "SELECT "+Utilidades.campoId+" FROM "+Utilidades.tablaNotas;

        Cursor cursor = db.rawQuery(select,null);

        do {
            cursor.moveToNext();
        }while (cursor.isLast()!=true);

        notaIdFirebase = String.valueOf(cursor.getInt(0));
    }

    public void actualizarNota(){

        SQLiteDatabase db = NotasFragment.conn.getWritableDatabase();

        String[] id = {String.valueOf(NotasFragment.nota.getId())};
        ContentValues values = new ContentValues();
        values.put(Utilidades.campoTitulo,editTextTitulo.getText().toString());
        values.put(Utilidades.campoContenido,editTextContenido.getText().toString());

        db.update(Utilidades.tablaNotas,values,Utilidades.campoId+"=?",id);
        NotasFragment.editar = false;

    }

    public void eliminarNota(){

        SQLiteDatabase db = NotasFragment.conn.getWritableDatabase();
        String[] id = {String.valueOf(NotasFragment.nota.getId())};

        db.delete(Utilidades.tablaNotas,Utilidades.campoId+"=?",id);
        NotasFragment.editar = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}