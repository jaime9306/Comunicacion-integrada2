package ondrios.comunicacion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ondrios.comunicacion.Conection.Cliente.Client;
import ondrios.comunicacion.Conection.Servidor.MyParcelableServer;
import ondrios.comunicacion.Conection.Servidor.Server;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {

    private Button boton_registrar;
    private Button boton_parar;
    private Button boton_enviar;
    private EditText entrada_nombre;
    private EditText entrada_nusuarios;
    private EditText edit_entrada;
    private TextView estado;
    private TextView texto_entrada;

    private MyParcelableServer parcelable=new MyParcelableServer();

    private Server servidor;
    private Client cliente;

    private String m;

    private final String TAG = "ServerActivity";
    private final int id = 0; //Indica que es la actividad servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);


        //Elementos de la vista
        boton_parar       = (Button)   findViewById(R.id.boton_parar);
        boton_registrar   = (Button)   findViewById(R.id.boton_registrar);
        entrada_nombre    = (EditText) findViewById(R.id.editText_nombre);
        estado            = (TextView) findViewById(R.id.textView_estado);
        entrada_nusuarios = (EditText) findViewById(R.id.editText_nusuarios);

        boton_enviar  = (Button)   findViewById(R.id.botonServer_enviar);
        edit_entrada  = (EditText) findViewById(R.id.editTextServer_entrada);
        texto_entrada = (TextView) findViewById(R.id.textViewServer_entrada);

        boton_registrar.setOnClickListener(this);
        boton_enviar.setOnClickListener(this);
        boton_parar.setOnClickListener(this);

        boton_enviar.setVisibility(View.GONE);
        edit_entrada.setVisibility(View.GONE);
        texto_entrada.setVisibility(View.GONE);

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.boton_parar:
                servidor.apagar();
                Intent intentCrearPartida = new Intent(ServerActivity.this,MainActivity.class);
                startActivity(intentCrearPartida);
                break;
            case R.id.boton_registrar:
                String serviceName = entrada_nombre.getText().toString();
                int nusuarios = Integer.valueOf(entrada_nusuarios.getText().toString());
                servidor = new Server(this,serviceName,nusuarios);
                estado.setText("Estado del servidor:\nDireccion:"+servidor.getAddress()+"\nPuerto:"+servidor.getPort());
                cliente = new Client(this, servidor.getPort(),id);
                break;

            case R.id.botonServer_enviar:
                String mensaje = edit_entrada.getText().toString();
                Log.e(TAG, "Envia mensaje");
                cliente.enviaMensaje(mensaje);
                boton_enviar.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    public void notificaClientesCompletados(){
        boton_registrar.setVisibility(View.GONE);
        entrada_nombre.setVisibility(View.GONE);
        entrada_nusuarios.setVisibility(View.GONE);
        estado.setVisibility(View.GONE);
        edit_entrada.setVisibility(View.VISIBLE);
        texto_entrada.setVisibility(View.VISIBLE);
    }

    public void publicaMensaje(String mensaje){
        this.m=mensaje;
        texto_entrada.setText(m);
        edit_entrada.setText("");
    }

    public void muestraBotones(){
        boton_enviar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        parcelable.setCliente(cliente);
        parcelable.setM(m);
        parcelable.setServidor(servidor);


        outState.putParcelable("Parce", parcelable);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        parcelable=savedInstanceState.getParcelable("Parce");
        cliente=parcelable.getCliente();
        m=parcelable.getM();
        servidor=parcelable.getServidor();

    }
}
