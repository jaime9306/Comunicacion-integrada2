package ondrios.comunicacion;

import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    protected ImageView carta1,carta2,carta3,cartaFin,cartaPinte;
    private int modificarX=20;
    private int modificarY=20;
    private int margenX=2000;
    private int margenY=400;
    private PointF ini1,ini2,ini3,fin;


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
        carta1 = (ImageView) findViewById(R.id.c1j2);
        carta1.setOnTouchListener(handlerMover1);
        carta2 = (ImageView) findViewById(R.id.c2j2);
        carta2.setOnTouchListener(handlerMover2);
        carta3 = (ImageView) findViewById(R.id.c3j2);
        carta3.setOnTouchListener(handlerMover3);
        cartaFin=(ImageView)findViewById(R.id.c4j1);
        cartaPinte=(ImageView)findViewById(R.id.pinta);
        ini1=new PointF();
        ini2=new PointF();
        ini3=new PointF();
        fin=new PointF();
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
    View.OnTouchListener handlerMover1 = new View.OnTouchListener(){
        PointF DownPT=new PointF();
        @Override
        public boolean onTouch(View v, MotionEvent event){
            PointF StartPT = new PointF();
            int eid = event.getAction();
            switch(eid) {
                case MotionEvent.ACTION_MOVE:
                    StartPT = new PointF(v.getX(), v.getY());
                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                    v.setX((StartPT.x + mv.x) - modificarX);
                    v.setY((StartPT.y + mv.y) - modificarY);
                    break;
                case MotionEvent.ACTION_DOWN:
                    DownPT.x = event.getX();
                    ini1.set(carta1.getX(),carta1.getY());
                    DownPT.y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if(v.getX()>cartaFin.getX() && v.getX()<cartaFin.getX()+cartaFin.getWidth()
                            && v.getY()>cartaFin.getY() && v.getY()<cartaFin.getY()+cartaFin.getHeight()){
                        v.setX(cartaFin.getX()+carta1.getWidth());
                        v.setY(cartaFin.getY()+carta1.getHeight()/2);
                    } else {
                        v.setX(ini1.x);
                        v.setY(ini1.y);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    View.OnTouchListener handlerMover2 = new View.OnTouchListener(){
        PointF DownPT=new PointF();
        @Override
        public boolean onTouch(View v, MotionEvent event){
            PointF StartPT = new PointF();
            int eid = event.getAction();
            switch(eid) {
                case MotionEvent.ACTION_MOVE:
                    StartPT = new PointF(v.getX(), v.getY());
                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                    v.setX((StartPT.x + mv.x) - modificarX);
                    v.setY((StartPT.y + mv.y) - modificarY);
                    break;
                case MotionEvent.ACTION_DOWN:
                    DownPT.x = event.getX();
                    ini2.set(carta2.getX(), carta2.getY());
                    DownPT.y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if(v.getX()>cartaFin.getX() && v.getX()<cartaFin.getX()+cartaFin.getWidth()
                            && v.getY()>cartaFin.getY() && v.getY()<cartaFin.getY()+cartaFin.getHeight()){
                        v.setX(cartaFin.getX()+carta2.getWidth());
                        v.setY(cartaFin.getY()+carta2.getHeight()/2);
                    } else {
                        v.setX(ini2.x);
                        v.setY(ini2.y);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    View.OnTouchListener handlerMover3 = new View.OnTouchListener(){
        PointF DownPT=new PointF();
        @Override
        public boolean onTouch(View v, MotionEvent event){
            PointF StartPT = new PointF();
            int eid = event.getAction();
            switch(eid) {
                case MotionEvent.ACTION_MOVE:
                    StartPT = new PointF(v.getX(), v.getY());
                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                    v.setX((StartPT.x + mv.x) - modificarX);
                    v.setY((StartPT.y + mv.y) - modificarY);
                    break;
                case MotionEvent.ACTION_DOWN:
                    DownPT.x = event.getX();
                    ini3.set(carta3.getX(),carta3.getY());
                    DownPT.y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if(v.getX()>cartaFin.getX() && v.getX()<cartaFin.getX()+cartaFin.getWidth()
                            && v.getY()>cartaFin.getY() && v.getY()<cartaFin.getY()+cartaFin.getHeight()){
                        v.setX(cartaFin.getX()+carta3.getWidth());
                        v.setY(cartaFin.getY()+carta3.getHeight()/2);
                    } else {
                        v.setX(ini3.x);
                        v.setY(ini3.y);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    protected int getCarta(String c){
        switch(c){
            case("o1"):
                return R.drawable.o1;
            case("o2"):
                return R.drawable.o2;
            case("o3"):
                return R.drawable.o3;
            case("o4"):
                return R.drawable.o4;
            case("o5"):
                return R.drawable.o5;
            case("o6"):
                return R.drawable.o6;
            case("o7"):
                return R.drawable.o7;
            case("o10"):
                return R.drawable.o10;
            case("o11"):
                return R.drawable.o11;
            case("o12"):
                return R.drawable.o12;
            case("c1"):
                return R.drawable.c1;
            case("c2"):
                return R.drawable.c2;
            case("c3"):
                return R.drawable.c3;
            case("c4"):
                return R.drawable.c4;
            case("c5"):
                return R.drawable.c5;
            case("c6"):
                return R.drawable.c6;
            case("c7"):
                return R.drawable.c7;
            case("c10"):
                return R.drawable.c10;
            case("c11"):
                return R.drawable.c11;
            case("c12"):
                return R.drawable.c12;
            case("e1"):
                return R.drawable.e1;
            case("e2"):
                return R.drawable.e2;
            case("e3"):
                return R.drawable.e3;
            case("e4"):
                return R.drawable.e4;
            case("e5"):
                return R.drawable.e5;
            case("e6"):
                return R.drawable.e6;
            case("e7"):
                return R.drawable.e7;
            case("e10"):
                return R.drawable.e10;
            case("e11"):
                return R.drawable.e11;
            case("e12"):
                return R.drawable.e12;
            case("b1"):
                return R.drawable.b1;
            case("b2"):
                return R.drawable.b2;
            case("b3"):
                return R.drawable.b3;
            case("b4"):
                return R.drawable.b4;
            case("b5"):
                return R.drawable.b5;
            case("b6"):
                return R.drawable.b6;
            case("b7"):
                return R.drawable.b7;
            case("b10"):
                return R.drawable.b10;
            case("b11"):
                return R.drawable.b11;
            case("b12"):
                return R.drawable.b12;
            default:
                return R.drawable.v0;
        }
    }
    public void setCarta1(String c){
        carta1.setBackgroundResource(getCarta(c));
    }
    public void setCarta2(String c){
        carta2.setBackgroundResource(getCarta(c));
    }
    public void setCarta3(String c){
        carta3.setBackgroundResource(getCarta(c));
    }
    public void setPinte(String c){
        cartaPinte.setBackgroundResource(getCarta(c));
    }


}
