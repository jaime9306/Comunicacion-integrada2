package ondrios.comunicacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ondrios.comunicacion.Conexion.Cliente.Client;
import ondrios.comunicacion.Conexion.Cliente.MyParcelableClient;

import static java.lang.Thread.sleep;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener{

    private Button boton_entrar;
    private Button boton_enviar;
    private EditText entrada_nombre;
    private EditText edit_entrada;
    private TextView estado;
    private TextView texto_entrada;
    private Spinner spinner;

    private MyParcelableClient parcelable=new MyParcelableClient();
    private Client cliente;
    private String m;
    private ArrayList<String> lista= new ArrayList();
    private String servicioSeleccionado;

    private int primeravez=0;
    private final String TAG="ClientActivity";
    private final int id = 1; //Indica que es la actividad cliente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);


        boton_entrar   = (Button) findViewById(R.id.boton_entrar);
        //entrada_nombre = (EditText) findViewById(R.id.editText_nombre);
        estado         = (TextView) findViewById(R.id.textView_estado);

        boton_enviar  = (Button) findViewById(R.id.botonClient_enviar);
        edit_entrada  = (EditText) findViewById(R.id.editTextClient_entrada);
        texto_entrada = (TextView) findViewById(R.id.textViewClient_entrada);

        boton_entrar.setOnClickListener(this);
        boton_enviar.setOnClickListener(this);

        boton_enviar.setVisibility(View.GONE);
        edit_entrada.setVisibility(View.GONE);
        texto_entrada.setVisibility(View.GONE);

        if(primeravez==0) {


            cliente = new Client(this, id);
            lista = cliente.getServiciosDisponibles();
            Log.i(TAG, "TAmaño " + lista.size());
            lista.add(0, "Selecciona una...");
            addServices(lista);
            primeravez++;
        }
    }

    public void addServices(ArrayList lista){
        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptador);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String servicio = arg0.getItemAtPosition(arg2).toString();
                if (!servicio.equals("null")) {
                    //Toast.makeText(arg0.getContext(), "Seleccionado: " + servicio, Toast.LENGTH_SHORT).show();
                    servicioSeleccionado = servicio;
                    //conectar(servicio);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.boton_entrar:
                //String serviceName=entrada_nombre.getText().toString();
                //cliente = new Client(this, serviceName, id);
                if (servicioSeleccionado.equals("Selecciona una...")){
                    estado.setText("Debes selecionar una");
                }else {
                    cliente.conectar(servicioSeleccionado);
                }
                break;
            case R.id.botonClient_enviar:
                String mensaje = edit_entrada.getText().toString();
                //boton_enviar.setVisibility(View.GONE);
                cliente.enviaMensaje(mensaje,"mensaje");
                break;
        }
    }

    public void notificaServidorEncontrado(){
        //Cambia al chat
        boton_entrar.setVisibility(View.GONE);
        //entrada_nombre.setVisibility(View.GONE);
        estado.setVisibility(View.GONE);
        edit_entrada.setVisibility(View.VISIBLE);
        texto_entrada.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
        setContentView(R.layout.activity_interfaz_juego);
    }

    public void muestraBotones(){
        boton_enviar.setVisibility(View.VISIBLE);
    }

    public void publicaMensaje(String mensaje){
        this.m=mensaje;
        texto_entrada.setText(m);
        edit_entrada.setText("");
    }

    public void notificaFalloServicio(){
        estado.setText("No se ha encontrado el servicio.");

    }

    public void notificaApaga(){
        Intent intent = new Intent(ClientActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        parcelable.setCliente(cliente);
        parcelable.setM(m);
        parcelable.setLista(lista);
        parcelable.setServicioSeleccionado(servicioSeleccionado);
        parcelable.setPrimeravez(primeravez);

        outState.putParcelable("Parce", parcelable);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        parcelable=savedInstanceState.getParcelable("Parce");
        cliente=parcelable.getCliente();
        m=parcelable.getM();
        lista=parcelable.getLista();
        servicioSeleccionado=parcelable.getServicioSeleccionado();
        primeravez=parcelable.getPrimeravez();

    }



}
