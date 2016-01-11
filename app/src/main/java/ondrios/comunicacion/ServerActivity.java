package ondrios.comunicacion;

import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ondrios.comunicacion.Conexion.Cliente.Client;
import ondrios.comunicacion.Conexion.Servidor.MyParcelableServer;
import ondrios.comunicacion.Conexion.Servidor.Server;

import static java.lang.Thread.sleep;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {

    private Button boton_registrar;
    private TextView titulo;
    private EditText entrada_nombre;

    private ImageButton vs1;
    private ImageButton vs2;
    private int posVacia;
    private int nJugadores=2;

    protected ImageView carta1,carta2,carta3,cartaFin,cartaPinte,carta4,carta5,carta6;
    protected ImageButton cartaMonton;
    private int modificarX=20;
    private int modificarY=20;
    private int margenX=2000;
    private int margenY=400;
    private PointF ini1,ini2,ini3,fin,ini4;
    private boolean turno;

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

        titulo = (TextView) findViewById(R.id.textView_titulo);
        boton_registrar   = (Button)   findViewById(R.id.boton_registrar);
        entrada_nombre    = (EditText) findViewById(R.id.editText_nombre);
        vs1 = (ImageButton) findViewById(R.id.imageView_1vs1);
        vs2 = (ImageButton) findViewById(R.id.imageView_2vs2);

        boton_registrar.setOnClickListener(this);

        vs1.setOnClickListener(this);
        vs2.setOnClickListener(this);




    }

    public void onClick(View v){
        switch (v.getId()){


            case R.id.boton_registrar:
                String serviceName = entrada_nombre.getText().toString();
                servidor = new Server(this,serviceName,nJugadores);
                titulo.setText("Esperando jugadores...");
                vs1.setEnabled(false);
                vs2.setEnabled(false);
                entrada_nombre.setEnabled(false);
                boton_registrar.setVisibility(View.INVISIBLE);
                cliente = new Client(this, servidor.getPort(),id);
                break;


            case R.id.imageView_1vs1:
                nJugadores=2;
                vs1.setBackgroundResource(R.drawable.ico1vs1);
                vs2.setBackgroundResource(R.drawable.ico2vs2gris);
                Toast v1 = Toast.makeText(getApplicationContext(),
                        "Has elegido 1 vs 1", Toast.LENGTH_SHORT);
                v1.show();

                break;
            case R.id.imageView_2vs2:
                nJugadores=4;
                vs1.setBackgroundResource(R.drawable.ico1vs1gris);
                vs2.setBackgroundResource(R.drawable.ico2vs2);
                Toast v2 = Toast.makeText(getApplicationContext(),
                        "Has elegido 2 vs 2", Toast.LENGTH_SHORT);
                v2.show();
                break;

            default:
                break;
        }
    }

    public void notificaClientesCompletados(){
        boton_registrar.setVisibility(View.GONE);
        entrada_nombre.setVisibility(View.GONE);

        setContentView(R.layout.activity_interfaz_juego);
        carta1 = (ImageView) findViewById(R.id.c1j2);
        carta1.setOnTouchListener(handlerMover1);
        carta2 = (ImageView) findViewById(R.id.c2j2);
        carta2.setOnTouchListener(handlerMover2);
        carta3 = (ImageView) findViewById(R.id.c3j2);
        carta3.setOnTouchListener(handlerMover3);
        cartaFin=(ImageView)findViewById(R.id.c4j1);
        cartaPinte=(ImageView)findViewById(R.id.pinta);
        carta4 = (ImageView) findViewById(R.id.c1j1);
        carta5 = (ImageView) findViewById(R.id.c2j1);
        carta6 = (ImageView) findViewById(R.id.c3j1);
        cartaMonton = (ImageButton) findViewById(R.id.mazo);
        ini1=new PointF();
        ini2=new PointF();
        ini3=new PointF();
        ini4=new PointF();
        ini4.set(carta4.getX(),carta4.getY());
        fin=new PointF();
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
                            && v.getY()>cartaFin.getY() && v.getY()<cartaFin.getY()+cartaFin.getHeight()&&turno){
                        v.setX(cartaFin.getX()+carta1.getWidth());
                        v.setY(cartaFin.getY() + carta1.getHeight() / 2);
                        Drawable c = carta1.getBackground();
                        cliente.enviaCarta(0);
                        posVacia=0;
                        turno=false;
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
                            && v.getY()>cartaFin.getY() && v.getY()<cartaFin.getY()+cartaFin.getHeight()&&turno){
                        v.setX(cartaFin.getX() + carta2.getWidth());
                        v.setY(cartaFin.getY() + carta2.getHeight() / 2);
                        cliente.enviaCarta(1);
                        posVacia=1;
                        turno=false;
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
                            && v.getY()>cartaFin.getY() && v.getY()<cartaFin.getY()+cartaFin.getHeight()&&turno){
                        v.setX(cartaFin.getX() + carta3.getWidth());
                        v.setY(cartaFin.getY() + carta3.getHeight()/2);
                        cliente.enviaCarta(2);
                        posVacia=2;
                        turno=false;
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
    public void tiraCartaContrario(String c){
        ini4.set(carta4.getX(),carta4.getY());
        carta4.setX(cartaFin.getX()+carta4.getWidth());
        carta4.setY(cartaFin.getY()+carta4.getHeight());
        carta4.setBackgroundResource(getCarta(c));
    }
    public void reparteCartas(String c){

        switch (posVacia){
            case 0:
                carta1.setX(ini1.x);
                carta1.setY(ini1.y);
                setCarta1(c);
                break;
            case 1:
                carta2.setX(ini2.x);
                carta2.setY(ini2.y);
                setCarta2(c);
                break;
            case 2:
                carta3.setX(ini3.x);
                carta3.setY(ini3.y);
                setCarta3(c);
                break;
        }
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        carta4.setX(ini4.x);
        carta4.setY(ini4.y);
        carta4.setBackgroundResource(getCarta("reverso"));

    }
    public void desaparecePinte(String c){
        cartaPinte.setVisibility(View.INVISIBLE);
        cartaMonton.setVisibility(View.INVISIBLE);
        switch (posVacia) {
            case 0:
                carta1.setX(ini1.x);
                carta1.setY(ini1.y);
                setCarta1(c);
                break;
            case 1:
                carta2.setX(ini2.x);
                carta2.setY(ini2.y);
                setCarta2(c);
                break;
            case 2:
                carta3.setX(ini3.x);
                carta3.setY(ini3.y);
                setCarta3(c);
                break;
        }
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        carta4.setX(ini4.x);
        carta4.setY(ini4.y);
        carta4.setBackgroundResource(getCarta("reverso"));

    }
    public void eliminaUna(){
        carta6.setVisibility(View.INVISIBLE);
        switch (posVacia){
            case 0:
                carta1.setVisibility(View.INVISIBLE);
                break;
            case 1:
                carta2.setVisibility(View.INVISIBLE);
                break;
            case 2:
                carta3.setVisibility(View.INVISIBLE);
                break;
        }
    }
    public void eliminaDos(){
        carta5.setVisibility(View.INVISIBLE);
        switch (posVacia){
            case 0:
                carta1.setVisibility(View.INVISIBLE);
                break;
            case 1:
                carta2.setVisibility(View.INVISIBLE);
                break;
            case 2:
                carta3.setVisibility(View.INVISIBLE);
                break;
        }
    }
    public void notificaTurno(){
        Toast turno = Toast.makeText(getApplicationContext(),
                "Es tu turno", Toast.LENGTH_SHORT);

        turno.show();
    }
    public void setTurno(){
        turno=true;
    }
    public void publicaMensaje(String mensaje){
        this.m=mensaje;
        //texto_entrada.setText(m);
        //edit_entrada.setText("");
    }

    public void muestraBotones(){
        //boton_enviar.setVisibility(View.VISIBLE);
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
