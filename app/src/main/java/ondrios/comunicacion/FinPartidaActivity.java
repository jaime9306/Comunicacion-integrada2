package ondrios.comunicacion;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ondrios.comunicacion.BD.AdminSQLiteOpenHelper;

public class FinPartidaActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tperdedor;
    private TextView tganador;
    private TextView hasGanado;
    private Button volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_partida);
        String[] datos = getIntent().getStringArrayExtra("ganador");
        Long duracionPartida=getIntent().getLongExtra("duracion", 0);
        String quien = getIntent().getStringExtra("quien");
        boolean hasGanadoV = Boolean.parseBoolean(quien);
        int puntuacionG = Integer.parseInt(datos[1]);
        int puntuacionP = 120-puntuacionG;
        tperdedor=(TextView)findViewById(R.id.textPerdedor);
        tganador=(TextView) findViewById(R.id.textGanador);
        tperdedor.setText(getString(R.string.puntuacionPer)+ " "+Integer.toString(puntuacionP));
        tganador.setText(getString(R.string.puntuacion)+ " "+Integer.toString(puntuacionG));
        volver=(Button)findViewById(R.id.boton_volver);
        volver.setOnClickListener(this);
        hasGanado=(TextView)findViewById(R.id.textoGanado);
        if(hasGanadoV){
            hasGanado.setText(getString(R.string.hasGanado));
            guardarDatos(duracionPartida,puntuacionG,puntuacionG);
        } else {
            hasGanado.setText(getString(R.string.hasPerdido));
            guardarDatos(duracionPartida,puntuacionP,puntuacionP);
        }




    }

    private void guardarDatos(Long duracionPartida, int puntuacionG, int puntuacionG1) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this
        );
        SQLiteDatabase db = admin.getWritableDatabase();
        Long tiempo=System.currentTimeMillis();
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion)" +
                " values("+tiempo+","+puntuacionG+","+puntuacionG1+","+(duracionPartida/1000)+");");
        db.close();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_volver:
                Intent principal = new Intent(FinPartidaActivity.this, MainActivity.class);
                startActivity(principal);
                finish();
                break;
        }
    }
}
