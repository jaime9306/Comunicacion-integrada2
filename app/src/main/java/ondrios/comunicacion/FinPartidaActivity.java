package ondrios.comunicacion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinPartidaActivity extends AppCompatActivity implements View.OnClickListener {
    protected TextView tperdedor,tganador,hasGanado;
    protected Button volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_partida);
        String[] datos = getIntent().getStringArrayExtra("ganador");
        String quien = getIntent().getStringExtra("quien");
        boolean hasGanadoV = Boolean.parseBoolean(quien);
        String ganador = datos[0];
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
        } else {
            hasGanado.setText(getString(R.string.hasPerdido));
        }


    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_volver:
                Intent principal = new Intent(FinPartidaActivity.this, MainActivity.class);
                finish();
                break;
        }
    }
}
