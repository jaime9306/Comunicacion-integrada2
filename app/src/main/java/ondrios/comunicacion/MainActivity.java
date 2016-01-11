package ondrios.comunicacion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button botonReglas;
    Button botonInstrucciones;
    Button botonCrearPartida;
    Button botonUnirPartida;
    Button botonEstadisticas;
    Button botonPreferencias;
    Button botonGraficas;
    Button botonBD;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        botonReglas=(Button) findViewById(R.id.buttonReglas);
        botonReglas.setOnClickListener(this);

        botonInstrucciones=(Button) findViewById(R.id.buttonInstrucciones);
        botonInstrucciones.setOnClickListener(this);

        botonCrearPartida=(Button) findViewById(R.id.buttonCrearPartida);
        botonCrearPartida.setOnClickListener(this);

        botonUnirPartida=(Button) findViewById(R.id.buttonUnirPartida);
        botonUnirPartida.setOnClickListener(this);

        botonEstadisticas=(Button) findViewById(R.id.buttonEstadisticas);
        botonEstadisticas.setOnClickListener(this);

        botonPreferencias=(Button) findViewById(R.id.buttonPreferencias);
        botonPreferencias.setOnClickListener(this);

        botonBD=(Button) findViewById(R.id.buttonBD);
        botonBD.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View v) {
        /*Para cada boton de la actividad se inicia la actividad correspondiente a ese boton*/
        switch (v.getId()){
            case R.id.buttonReglas:
                Intent intentReglas = new Intent(MainActivity.this,ReglasBrisca.class);
                startActivity(intentReglas);
                break;

            case R.id.buttonInstrucciones:
                Intent intentInstrucciones = new Intent(MainActivity.this,InstruccionesBrisca.class);
                startActivity(intentInstrucciones);
                break;

            case R.id.buttonEstadisticas:
                Intent intentEstadisticas = new Intent(MainActivity.this,GraficasPrueba.class);
                startActivity(intentEstadisticas);
                break;

            case R.id.buttonCrearPartida:
                Intent intentCrearPartida = new Intent(MainActivity.this,ServerActivity.class);
               /* Bundle datos=new Bundle();
                datos.putBoolean("crear",true);
                intentCrearPartida.putExtras(datos);*/
                startActivity(intentCrearPartida);
                break;

            case R.id.buttonUnirPartida:
                Intent intentUnirPartida = new Intent(MainActivity.this,ClientActivity.class);
               /*Bundle datos2=new Bundle();
                datos2.putBoolean("crear",false);
                intentUnirPartida.putExtras(datos2);*/
                startActivity(intentUnirPartida);
                break;

            case R.id.buttonPreferencias:
                Intent intentPreferencias = new Intent(MainActivity.this,Preferencias.class);
                startActivity(intentPreferencias);
                break;

            case R.id.buttonBD:
                Intent intentBD = new Intent(MainActivity.this,GuardarPartida.class);
                startActivity(intentBD);
                break;



            default:
                break;
        }
    }
}
