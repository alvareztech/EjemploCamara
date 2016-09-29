package tech.alvarez.ejemplocamara;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ImageView fotoImageView;
    private String fotoPath;

    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fotoImageView = (ImageView) findViewById(R.id.fotoImageView);

        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void tomarFoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 777);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("MIAPP", "onActivityResult: " + requestCode + " " + resultCode + " " + data);

        if (requestCode == 777 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            fotoImageView.setImageBitmap(bitmap);
        } else if (requestCode == 888 && resultCode == RESULT_OK) {
            obtenerFotoDeArchivo();
        } else if (requestCode == 999 && resultCode == RESULT_OK && data != null) {

            Uri imagenUri = data.getData();
            Log.i("MIAPP", imagenUri.getPath());
            fotoImageView.setImageURI(imagenUri);

            // subir imagen


            StorageReference riversRef = storageRef.child("images/foto.jpg");

            riversRef.putFile(imagenUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(MainActivity.this, "Se subio correctamente", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void obtenerFotoDeArchivo() {
        if (fotoPath != null) {
            colocarFoto();
            fotoPath = null;
        }
    }

    public void tomarFotoYGuardar(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File imagenFile = crearImagenFile();
            if (imagenFile != null) {

                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this,
                        "tech.alvarez.ejemplocamara.fileprovider",
                        imagenFile));

                startActivityForResult(intent, 888);
            }
        }
    }


    private File crearImagenFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String nombreImagen = "ABC_" + timeStamp + "_";
            File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imagenFile = File.createTempFile(nombreImagen, ".jpg", directorio);
            fotoPath = imagenFile.getAbsolutePath();
            Log.i("MIAPP", "Path> " + fotoPath);
            return imagenFile;
        } catch (IOException e) {
            return null;
        }
    }

    private void colocarFoto() {
        int anchoImageView = fotoImageView.getWidth();
        int altoImageView = fotoImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fotoPath, bmOptions);
        int anchoFoto = bmOptions.outWidth;
        int altoFoto = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((anchoImageView > 0) || (altoImageView > 0)) {
            scaleFactor = Math.min(anchoFoto / anchoImageView, altoFoto / altoImageView);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(fotoPath, bmOptions);
        fotoImageView.setImageBitmap(bitmap);
    }

    public void tomarFotoGaleria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 999);
    }

    public void tomarDocumentos(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Selecciona"), 999);
    }
}
