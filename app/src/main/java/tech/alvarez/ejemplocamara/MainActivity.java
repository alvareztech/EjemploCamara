package tech.alvarez.ejemplocamara;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ImageView fotoImageView;
    private String fotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fotoImageView = (ImageView) findViewById(R.id.fotoImageView);
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

        if (requestCode == 777 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            fotoImageView.setImageBitmap(bitmap);
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
            String nombreImagen = "IMAGEN_" + timeStamp + "_";
            File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imagenFile = File.createTempFile(nombreImagen, ".jpg", directorio);
            fotoPath = imagenFile.getAbsolutePath();
            return imagenFile;
        } catch (IOException e) {
            return null;
        }
    }
}
