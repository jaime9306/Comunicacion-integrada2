package ondrios.comunicacion;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.ContentValues;
import android.widget.Toast;

import java.util.Date;

import ondrios.comunicacion.BD.AdminSQLiteOpenHelper;

public class GuardarPartida extends AppCompatActivity implements View.OnClickListener {
    private TextView textEquipo, textPropio, textDuracion;
    private EditText edPuntEq, edPuntPropio, edDuracion;
    private Button guardarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_partida);

        textEquipo = (TextView) findViewById(R.id.textEquipo);
        textPropio = (TextView) findViewById(R.id.textPropio);
        textDuracion = (TextView) findViewById(R.id.textDuracion);
        edPuntEq = (EditText) findViewById(R.id.edPuntEq);
        edPuntPropio = (EditText) findViewById(R.id.edPuntPropio);
        edDuracion = (EditText) findViewById(R.id.edDuracion);
        guardarButton = (Button) findViewById(R.id.guardarButton);
        guardarButton.setOnClickListener(this);


    }

    public void guardar(View view) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "brisca", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        int puntosEquipo = Integer.parseInt(edPuntEq.getText().toString());
        int puntosPropios = Integer.parseInt(edPuntPropio.getText().toString());
        int duracion = Integer.parseInt(edDuracion.getText().toString());
        Long hoy = System.currentTimeMillis();
        ContentValues registro = new ContentValues();
        registro.put("fecha", hoy);
        registro.put("puntPropia", puntosPropios);
        registro.put("puntEquipo", puntosEquipo);
        registro.put("duracion", duracion);
        bd.insert("partidas", null, registro);
        bd.close();
        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show();


    }

    public void onClick(View v) {
        /*Para cada boton de la actividad se inicia la actividad correspondiente a ese boton*/
        switch (v.getId()) {
            case R.id.guardarButton:
                guardar(v);
                break;


        }
    }
}
