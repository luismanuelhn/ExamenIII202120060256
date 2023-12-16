package com.example.exameniii202120060256;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import  static  com.google.firebase.storage.FirebaseStorage.getInstance;
import com.example.exameniii202120060256.Config.Entrevista2;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class ListaEntrevista extends AppCompatActivity {

    RecyclerView recyclerViewEntrevista;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Entrevista2,ViewHolderEntrevista>firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Entrevista2>options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_entrevista);


        recyclerViewEntrevista= findViewById(R.id.recyclerViewEntrevista);
        recyclerViewEntrevista.setHasFixedSize(true);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("ENTREVISTA");

        ListarImagenesEntrevista();
    }

    private void ListarImagenesEntrevista(){
        options=new FirebaseRecyclerOptions.Builder<Entrevista2>().setQuery(mRef,Entrevista2.class).build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Entrevista2, ViewHolderEntrevista>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderEntrevista holder, int position, @NonNull Entrevista2 model) {
                holder.seteoDatos(
                        getApplicationContext(),
                        model.getDescripcion(),
                        model.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderEntrevista onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrevista,parent,false);

                ViewHolderEntrevista viewHolderEntrevista=new ViewHolderEntrevista(itemView);

                viewHolderEntrevista.setOnClickListener(new ViewHolderEntrevista.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(ListaEntrevista.this,"ITEM CLICK",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                       final String descripcion=getItem(position).getDescripcion();
                       final String Imagen=getItem(position).getImagen();
                        String periodista=getItem(position).getPeriodista();
                        String fecha=getItem(position).getFecha();



                        AlertDialog.Builder builder=new AlertDialog.Builder(ListaEntrevista.this);

                        String[] opciones={"Actualizar","Eliminar"};
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    Toast.makeText(ListaEntrevista.this,"ACTUALIZAR",Toast.LENGTH_SHORT).show();
                                }
                                if(i==1){
                                    EliminarDatos(descripcion,Imagen);
                                }
                            }
                        });

                        builder.create().show();




                    }
                });
                return viewHolderEntrevista;
            }
        };
        recyclerViewEntrevista.setLayoutManager(new GridLayoutManager(ListaEntrevista.this,2));
        firebaseRecyclerAdapter.startListening();
        recyclerViewEntrevista.setAdapter(firebaseRecyclerAdapter);
    }

    private void EliminarDatos(final String Nombre,final String ImagenActual){
        AlertDialog.Builder builer=new AlertDialog.Builder(ListaEntrevista.this);
        builer.setTitle("Eliminar");
        builer.setMessage("¿Desea eliminar los datos?");

        builer.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Query query=mRef.orderByChild("descripcion").equalTo(Nombre);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(ListaEntrevista.this,"Los datos se han borrado",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ListaEntrevista.this,error.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

                StorageReference ImagenSeleccionada=getInstance().getReferenceFromUrl(ImagenActual);
                ImagenSeleccionada.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ListaEntrevista.this,"Eliminando",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListaEntrevista.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        builer.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ListaEntrevista.this,"Cancelado",Toast.LENGTH_SHORT).show();

            }
        });

        builer.create().show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }
}