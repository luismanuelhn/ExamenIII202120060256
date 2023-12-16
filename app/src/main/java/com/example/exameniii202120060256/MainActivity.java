package com.example.exameniii202120060256;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.exameniii202120060256.Config.Entrevista2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exameniii202120060256.Config.Entrevista;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    static final int REQUEST_IMAGE = 101;
    static final int ACCESS_CAMERA = 201;
    ImageView imageView;
    Button btntakefoto;
    Button btnGuardar;
    Button btnListar;
    EditText descripcion, periodista, fecha;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private RequestQueue requestQueue;

    String RutaDelmacenamiento="fotos/";
    String RutaDeBaseDeDatos="ENTREVISTA";
    Uri RutaArchivoUri;
    String currentPhotoPath;

    StorageReference mStorageReference;
    DatabaseReference DatabaseReference;

    ProgressDialog progressDialog;

    int CODIGO_DE_SOLICITUD_IMAGEN=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageViewFoto);
        btntakefoto = (Button) findViewById(R.id.btnCaptura);
        btnGuardar = (Button) findViewById(R.id.btnGuardarFirebase);
        btnListar = (Button) findViewById(R.id.btnListar);


        descripcion = (EditText) findViewById(R.id.txtDescripcion);
        periodista = (EditText) findViewById(R.id.txtPeriodista);
        fecha = (EditText) findViewById(R.id.txtFecha);

        mStorageReference= FirebaseStorage.getInstance().getReference();
        DatabaseReference=FirebaseDatabase.getInstance().getReference(RutaDeBaseDeDatos);
        progressDialog=new ProgressDialog(MainActivity.this);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar imagen"),CODIGO_DE_SOLICITUD_IMAGEN);

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuardarDatos();
            }
        });




        btntakefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermisosCamera();
            }
        });

        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ListaEntrevista.class);
                startActivity(intent);
            }
        });


    }

    private void GuardarDatos() {

        if(RutaArchivoUri !=null){
            progressDialog.setTitle("Espere por favor");
            progressDialog.show();
            progressDialog.setCancelable(false);
            StorageReference storageReference2 =mStorageReference.child(RutaDelmacenamiento+System.currentTimeMillis()+"."+ObtenerExtencionDelArchivo(RutaArchivoUri));
            storageReference2.putFile(RutaArchivoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());

                            Uri downloadURI=uriTask.getResult();

                            String mdescriocion=descripcion.getText().toString();
                            String mPeriodista=periodista.getText().toString();
                            String mfecha=fecha.getText().toString();

                            Entrevista2 entrev = new Entrevista2(mdescriocion,mPeriodista,mfecha,downloadURI.toString());
                            String ID_IMAGEN=DatabaseReference.push().getKey();

                            DatabaseReference.child(ID_IMAGEN).setValue(entrev);

                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Guardado con exito",Toast.LENGTH_SHORT).show();


                          /*  entrev.setIpOrden(UUID.randomUUID().toString());*/




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                          progressDialog.setTitle("Publicando");
                          progressDialog.setCancelable(false);
                        }
                    });


        }
        else{
            Toast.makeText(this,"Debe asignar una imagen",Toast.LENGTH_SHORT).show();
        }

    }





    private void PermisosCamera() {
        // Metodo para obtener los permisos requeridos de la aplicacion
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, ACCESS_CAMERA);
        } else {
            dispatchTakePictureIntent();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getApplicationContext(), "se necesita el permiso de la camara", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.exameniii202120060256.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }
    /*Metodo para extraer extencion de la imagen*/
    private String ObtenerExtencionDelArchivo(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_DE_SOLICITUD_IMAGEN && resultCode==RESULT_OK
         && data !=null && data.getData() != null
        ) {
            RutaArchivoUri = data.getData();
            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),RutaArchivoUri);
                imageView.setImageBitmap(bitmap);
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String ConvertImageBase64(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imagearray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagearray, Base64.DEFAULT);

    }

    private void SendData(){

        Entrevista entrev = new Entrevista();

        entrev.setIpOrden(UUID.randomUUID().toString());
        entrev.setDescripcion(descripcion.getText().toString());
        entrev.setPeriodista(periodista.getText().toString());
        entrev.setFecha(fecha.getText().toString());
        entrev.setImagen(ConvertImageBase64(currentPhotoPath));



          databaseReference.child("entrevista").child(entrev.getIpOrden()).setValue(entrev);
          Toast.makeText(this,"Agregado",Toast.LENGTH_LONG).show();


     }


}