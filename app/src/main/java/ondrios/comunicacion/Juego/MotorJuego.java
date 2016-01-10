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

    private int njugadores;
    private ArrayList<Carta> baza;
    private ArrayList<Carta> jugadaEq1;
    private ArrayList<Carta> jugadaEq2;

    private Carta pinte;

    public MotorJuego(Server server, Partida partida){
        this.servidor = server;
        this.partida = partida;
        this.njugadores=this.partida.getListajug().length;
        this.baza=new ArrayList<>();
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
            Log.i(TAG,"Tamaño mano "+cartas.length);
            //Esta es la parte del null pointer exception con las cartas
            Log.i(TAG,"Tamaño mano "+cartas.length+" "+carta1.getNumero()+carta2.getNumero()+carta3.getNumero());
            String cartasFormato=cartas[0].getPalo()+Integer.toString(cartas[0].getNumero())+":"+
                    cartas[1].getPalo()+Integer.toString(cartas[1].getNumero())+":"+
                    cartas[2].getPalo()+Integer.toString(cartas[2].getNumero());
            Mensaje mensaje = new Mensaje(null,nombre+"::"+cartasFormato+"::"+pinte.getPalo()+pinte.getNumero(),"cartas");
            mensajes.add(mensaje);
        }
        for(Mensaje m: mensajes){
            servidor.enviaMensaje(m);
        }
    }

    public int getTurno(){
        return partida.getNumJugadorMano();
    }

    public void tiraCarta(int jugador, int carta){
        Jugador [] listaJugadores = partida.getListajug();
        Carta tirada = null;
        for(int i = 0;i<listaJugadores.length;i++){
            String nombre = listaJugadores[i].getNombre();
            if (nombre.equals(Integer.toString(jugador))){
               tirada=listaJugadores[i].echar(i);
            }
        }
        baza.add(tirada);
        Mensaje mensaje = new Mensaje(null,Integer.toString(jugador)+"::"+tirada.getPalo()+Integer.toString(tirada.getNumero()),"muestra_carta");
        servidor.enviaMensaje(mensaje);
        Equipo [] equipos = partida.getEquipos();
        Equipo eq1 = equipos[0];
        Equipo eq2 = equipos[1];
        String nombre = Integer.toString(jugador);
        for(int i = 0; i<eq1.jugadores.length;i++){
            if(nombre.equals(eq1.jugadores[i])){
                jugadaEq1.add(tirada);
            }else if (nombre.equals(eq2.jugadores[i])){
                jugadaEq2.add(tirada);
            }
        }
        if(baza.size()==njugadores){
         if(njugadores==2){
             Carta ganadora = partida.determinarCartaGanadora(baza.get(0),baza.get(1));
         }
        }

    }




}
