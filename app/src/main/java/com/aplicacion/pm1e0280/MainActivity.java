package com.aplicacion.pm1e0280;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import configuraciones.SQLiteConexion;
import configuraciones.Transacciones;

public class MainActivity extends AppCompatActivity {

    TextView textSeleccionarFoto;
    ImageView imageViewIngresar;
    EditText txtNombre, txtTelefono, txtNota;
    Spinner spinnerPais;

    Button btnSalvar, btnMostrar;
    ArrayList<String> arrayListPaises;

    String currentPhotoPath;
    ActivityResultLauncher<Intent> launcherTomarFoto;


    AlertDialog.Builder builder;
    AlertDialog dialog;


    static final int PETICION_ACCESO_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentPhotoPath = "";
        builder = null;
        dialog = null;

        arrayListPaises = new ArrayList<>();

        arrayListPaises.add("Honduras(504)");
        arrayListPaises.add("Costa Rica(506)");
        arrayListPaises.add("Guatemala(502)");
        arrayListPaises.add("El Salvador(503)");

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayListPaises);
        spinnerPais = (Spinner) findViewById(R.id.spinnerPaisIngresar);

        spinnerPais.setAdapter(spinnerArrayAdapter);


        textSeleccionarFoto = (TextView) findViewById(R.id.textViewTomarFoto);
        imageViewIngresar = (ImageView) findViewById(R.id.imageViewIngresar);

        txtNombre = (EditText) findViewById(R.id.editTextRegistrarNombre);
        txtTelefono = (EditText) findViewById(R.id.editTextRegistrarTelefono);
        txtNota = (EditText) findViewById(R.id.editTextRegistrarNota);

        btnSalvar = (Button) findViewById(R.id.btnRegistroSalvarContacto);
        btnMostrar = (Button) findViewById(R.id.btnRegistroMostrarContactos);

        textSeleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisosTomarFoto();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                guardarContacto();
            }
        });

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ActivityMostrar.class);
                startActivity(intent);
            }
        });

        launcherTomarFoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        Intent data = result.getData();

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Uri uri = Uri.parse(currentPhotoPath);
                            imageViewIngresar.setImageURI(uri);
                        }
                    }
                });

    }

    private void guardarContacto() {

       if(permitirGuardar()){
           SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NAME_DATABASE, null, 1);
           SQLiteDatabase db = conexion.getWritableDatabase();

           ContentValues values = new ContentValues();

           values.put(Transacciones.NOMBRE, txtNombre.getText().toString());
           values.put(Transacciones.TELEFONO, txtTelefono.getText().toString());
           values.put(Transacciones.NOTA, txtNota.getText().toString());
           values.put(Transacciones.PAIS, spinnerPais.getSelectedItem().toString());
           values.put(Transacciones.IMAGEN, currentPhotoPath);

           Long result = db.insert(Transacciones.TABLA_CONTACTOS, Transacciones.ID, values);

           if(result>0){
               Toast.makeText(getApplicationContext(), "Registro Exitoso!!"
                       ,Toast.LENGTH_LONG).show();

               limpiarEntradas();
           }else {
               Toast.makeText(getApplicationContext(), "Error: No se pudo realizar el registro"
                       ,Toast.LENGTH_LONG).show();
           }
       }
    }

    private void limpiarEntradas() {

        imageViewIngresar.setImageResource(R.drawable.ic_person);
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
        spinnerPais.setSelection(0);
        currentPhotoPath = "";
    }

    private boolean permitirGuardar() {

        String pais = spinnerPais.getSelectedItem().toString();
        String photoPath = currentPhotoPath;
        String nombre = txtNombre.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String nota = txtNota.getText().toString();

        String mensaje="";

        if(isTextEmpty(nombre)) mensaje = "Debe escribir un nombre";
        else if(!isText(nombre)) mensaje = "El campo nombre solo admite letras y espacios";
        else if(isTextEmpty(telefono))mensaje = "Debe escribir un telefono";
        else if(!isPhone(telefono))mensaje = "El telefono ingresado no es valido solo se aceptan numeros";
        else if(isTextEmpty(nota)) mensaje = "Debe escribir una nota";


        if(!isTextEmpty(mensaje)){
            mostrarMensaje("Alerta", mensaje);
            return false;
        }

        return true;
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(mensaje).setTitle(titulo);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.show();
    }


    private void permisosTomarFoto() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PETICION_ACCESO_CAM);
        }else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_ACCESO_CAM){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Se nesecitan permisos de acceso a camara", Toast.LENGTH_LONG).show();
        }
    }



    /*******************************************************************************************************/
//  String currentPhotoPath;

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
                // Error occurred while creating the File
                ex.toString();
            }
            // Continue only if the File was successfully created
            try {
                if (photoFile != null) {

                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.aplicacion.pm1e0280.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    takePictureIntent.putExtra("request_code", TAKE_PIC_REQUEST);

                    launcherTomarFoto.launch(takePictureIntent);
                }
            }catch (Exception e){
                Log.i("Error", "dispatchTakePictureIntent: " + e.toString());
            }
        }
    }

    private static boolean isPhone(String cadena){
        int temp;
        for(int i = 0; i < cadena.length(); i++){
            try {
                temp = Integer.parseInt(cadena.charAt(i)+"");
            }catch (Exception e){

                return false;
            }
        }

        return true;
    }

    private static boolean isText(String text){

        // Validando un texto que solo acepte letras sin importar tamaño
        Pattern pat = Pattern.compile("^[a-zA-ZáéíóúÁÉÓÚÍ ]+$");
        Matcher mat = pat.matcher(text);
        return (mat.matches());
    }

    //Si el texto esta vacio
    private static boolean isTextEmpty(String text){
        return (text.length()==0)?true:false;
    }

}
