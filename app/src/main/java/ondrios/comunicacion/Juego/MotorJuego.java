package ondrios.comunicacion.Juego;

import android.util.Log;

import java.util.ArrayList;

import ondrios.comunicacion.Conexion.Mensaje;
import ondrios.comunicacion.Conexion.Servidor.Server;

/**
 * Created by Mario on 09/01/2016.
 */
public class MotorJuego {

    private String TAG = "MotorJuego";

    private Server servidor;
    private Partida partida;

    private Carta pinte;

    public MotorJuego(Server server, Partida partida){
        this.servidor = server;
        this.partida = partida;
    }

    public void inicia(){
        partida.repartoInicial();
        pinte=partida.asignarTriunfo();
        enviaCartas();
    }

    private void enviaCartas(){
        Jugador [] listaJugadores = partida.getListajug();
        ArrayList<Mensaje> mensajes = new ArrayList <>();
        for(int i = 0;i<listaJugadores.length;i++){
            String nombre = listaJugadores[i].getNombre();
            Carta[] cartas = listaJugadores[i].getMano();
            Carta carta1= cartas[0];
            Carta carta2= cartas[1];
            Carta carta3 = cartas[2];
            //Esta es la parte del null pointer exception con las cartas
            Log.i(TAG,"Tamaño mano "+cartas.length+" "+carta1.getNumero()+carta2.getNumero()+carta3.getNumero());
            String cartasFormato=Integer.toString(cartas[0].getNumero())+
                    cartas[0].getPalo()+":"+
                    Integer.toString(cartas[1].getNumero())+
                    cartas[1].getPalo()+":"+
                    Integer.toString(cartas[2].getNumero())+
                    cartas[2].getPalo();
            Mensaje mensaje = new Mensaje(null,nombre+"::"+cartasFormato+"::"+pinte.getNumero()+pinte.getPalo(),"cartas");
            mensajes.add(mensaje);
        }
        for(Mensaje m: mensajes){
            servidor.enviaMensaje(m);
        }
    }


}
