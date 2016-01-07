package ondrios.comunicacion;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Activity que muestra la interfaz principal del juego.
 */
public class Interfaz extends AppCompatActivity  {

    protected ImageView carta1,carta2,carta3,cartaFin;
    private int modificarX=20;
    private int modificarY=20;
    private int margenX=2000;
    private int margenY=400;
    private PointF ini1,ini2,ini3,fin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_juego);
        carta1 = (ImageView) findViewById(R.id.c1j2);
        carta1.setOnTouchListener(handlerMover1);
        carta2 = (ImageView) findViewById(R.id.c2j2);
        carta2.setOnTouchListener(handlerMover2);
        carta3 = (ImageView) findViewById(R.id.c3j2);
        carta3.setOnTouchListener(handlerMover3);
        cartaFin=(ImageView)findViewById(R.id.c4j1);
        ini1=new PointF();
        ini2=new PointF();
        ini3=new PointF();
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
                    Log.d("Fin: ", "X" + cartaFin.getX() + " Y: " + cartaFin.getY());
                    Log.d("Posicion Actual: ",""+v.getX()+" Y: "+v.getY());
                    Log.d("Entre: X>",cartaFin.getX()+" X<"+(cartaFin.getX()+cartaFin.getWidth()) + " Y<" + cartaFin.getY() + " Y>"+(cartaFin.getY()-cartaFin.getHeight()));
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
}
