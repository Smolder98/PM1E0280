package com.aplicacion.pm1e0280;

import static com.aplicacion.pm1e0280.MainActivity.PETICION_ACCESO_CAM;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import configuraciones.SQLiteConexion;
import configuraciones.Transacciones;
import modelos.Contacto;

public class ActivityMostrar extends AppCompatActivity {

    private static final int PETICION_ACCESO_CALL = 200;
    SQLiteConexion conexion;
    ListView listViewContacto;

    ArrayList<Contacto> listContactos;

    ArrayList<String> listaStringContactos;

    Button btnAtras, btnEliminarContacto, btnMostrarImagen, btnCompartir, btnEditar;

    Contacto contactoSeleccionado;

    EditText textEditBuscar;

    ArrayAdapter adapter;


    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar);

        contactoSeleccionado = null;

        listarListViewContactos();


        textEditBuscar = (EditText) findViewById(R.id.editTextBuscar);



        btnAtras = (Button) findViewById(R.id.btnAtrasMostrar);
        btnEliminarContacto = (Button) findViewById(R.id.btnEliminarContacto);
        btnMostrarImagen = (Button) findViewById(R.id.btnMostrarImagen);
        btnCompartir = (Button) findViewById(R.id.btnCompatir);
        btnEditar = (Button) findViewById(R.id.btnEditar);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnEliminarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(contactoSeleccionado != null){
                    builder = new AlertDialog.Builder(ActivityMostrar.this);

                    builder.setMessage("¿Estas seguro que quieres eliminar este contacto?").setTitle("Alerta");

                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            eliminarContacto();

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    dialog = builder.create();
                    dialog.show();
                }else{
                    mostrarMensaje("Alerta", "No hay ningun contacto seleccionado");
                }


            }
        });

        btnMostrarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactoSeleccionado != null){
                    mostrarImagen();
                }else {
                    mostrarMensaje("Alerta", "No hay ningun contacto seleccionado");
                }
            }
        });

        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactoSeleccionado != null){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, contactoSeleccionado.getContactoString());
                    startActivity(Intent.createChooser(intent, "Share with"));
                }else {
                    mostrarMensaje("Alerta", "No hay ningun contacto seleccionado");
                }
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactoSeleccionado != null){

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contacto", contactoSeleccionado);

                    Intent intent = new Intent(getApplicationContext(), ActivityEditar.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }else {
                    mostrarMensaje("Alerta", "No hay ningun contacto seleccionado");
                }
            }
        });


//        listViewContacto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                contactoSeleccionado = listContactos.get(i);
//            }
//        });

        listViewContacto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private long lastTouchTime = 0;
            private long currentTouchTime = 0;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lastTouchTime = currentTouchTime;
                currentTouchTime = System.currentTimeMillis();

                contactoSeleccionado = listContactos.get(i);

                //Esto es cuando se preciona dos veces
                if (currentTouchTime - lastTouchTime < 250) {

                    Toast.makeText(getApplicationContext(), contactoSeleccionado.getNombre(), Toast.LENGTH_LONG).show();



//                        builder = new AlertDialog.Builder(ActivityMostrar.this);
//
//                        builder.setMessage("¿Desea llamar a "+contactoSeleccionado.getNombre()+" ?").setTitle("Alerta");
//
//                        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                permisosLlamar();
//                            }
//                        });
//
//                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {}
//                        });
//
//                        dialog = builder.create();
//                        dialog.show();

                    lastTouchTime = 0;
                    currentTouchTime = 0;
                }
            }
        });



        textEditBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        obtenerListaContactos();
        llenarListView();

        contactoSeleccionado = null;
    }

    private void llamar() {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(contactoSeleccionado.getTelCall()));//change the number
        startActivity(callIntent);
    }

    private void listarListViewContactos() {

        conexion = new SQLiteConexion(this, Transacciones.NAME_DATABASE, null, 1);
        listViewContacto = (ListView) findViewById(R.id.listViewContactos);
        listViewContacto.setSelector(R.color.blue_200);

        obtenerListaContactos();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaStringContactos);
        listViewContacto.setAdapter(adapter);
    }

    private void obtenerListaContactos() {

        SQLiteDatabase db = conexion.getReadableDatabase();

        Contacto tempCont = null;


        listContactos = new ArrayList<>();

        Cursor cursor = db.rawQuery(Transacciones.SELECT_TABLE_CONTACTOS, null);


        while (cursor.moveToNext()){

            tempCont = new Contacto();

            tempCont.setId(cursor.getInt(0));
            tempCont.setNombre(cursor.getString(1));
            tempCont.setTelefono(cursor.getString(2));
            tempCont.setNota(cursor.getString(3));
            tempCont.setPais(cursor.getString(4));
            tempCont.setImagen(cursor.getString(5));

            listContactos.add(tempCont);

        }

        cursor.close();

        llenarListStringContactos();
    }

    private void llenarListStringContactos() {

        listaStringContactos = new ArrayList<>();

        for(Contacto c: listContactos){

            listaStringContactos.add(c.toString());
        }

    }

    private void eliminarContacto(){

        conexion = new SQLiteConexion(this, Transacciones.NAME_DATABASE, null, 1);
        SQLiteDatabase database = conexion.getWritableDatabase();


        int result = database.delete(Transacciones.TABLA_CONTACTOS, Transacciones.ID+"=?",
                new String[]{contactoSeleccionado.getId()+""});

        if(result>0){
            obtenerListaContactos();
            llenarListView();

            contactoSeleccionado = null;
            Toast.makeText(getApplicationContext(), "Contacto eliminado correctamente", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Error: El contacto no se pudo eliminar", Toast.LENGTH_LONG).show();
        }






    }

    private void llenarListView() {

        adapter = null;
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaStringContactos);
        listViewContacto.setAdapter(adapter);
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        builder = new AlertDialog.Builder(ActivityMostrar.this);

        builder.setMessage(mensaje).setTitle(titulo);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.show();

    }

    public void mostrarImagen(){

        if(isTextEmpty(contactoSeleccionado.getImagen())){
            mostrarMensaje("Alerta", "El contacto no tiene ninguna imagen seleccionada");
            return;
        }

        builder = new AlertDialog.Builder(ActivityMostrar.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_personalizado, null);

        builder.setView(view);

        dialog = builder.create();

        dialog.show();

        TextView text =(TextView) view.findViewById(R.id.textViewDialogPersonalizado);
        text.setText(contactoSeleccionado.getNombre());

        ImageView imagen = (ImageView) view.findViewById(R.id.imageViewDialog);

//        Bitmap image = BitmapFactory.decodeFile(contactoSeleccionado.getImagen());
//        imagen.setImageBitmap(image);

        Uri uri = Uri.parse(contactoSeleccionado.getImagen());

        imagen.setImageURI(uri);

        Button btnCerrar = (Button) view.findViewById(R.id.buttonDialog);

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    //Si el texto esta vacio
    private static boolean isTextEmpty(String text){
        return (text.length()==0)?true:false;
    }

    /*************************************************************************************************************/

    private void permisosLlamar() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PETICION_ACCESO_CALL);
        }else {
            llamar();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_ACCESO_CALL){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                llamar();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Se nesecitan permisos de acceso a llamada", Toast.LENGTH_LONG).show();
        }
    }
}