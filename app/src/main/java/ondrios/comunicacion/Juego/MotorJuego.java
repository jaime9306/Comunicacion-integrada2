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
    private int numeroJugadores;
    private int numeroJuegos;
    private ArrayList<String> salaDeEspera;
    private Partida partida;

    public MotorJuego(Server server, int numeroJugadores, int numeroJuegos){
        this.servidor        = server;
        this.numeroJugadores = numeroJugadores;
        this.numeroJuegos    = numeroJuegos;
        this.salaDeEspera    = new ArrayList<>();
    }

    public void añadeJugador(String nombre){
        this.salaDeEspera.add(nombre);
    }

    public void inicia(){
        if(numeroJugadores==2){
            Jugador jugador1 = new Jugador(salaDeEspera.get(0));
            Jugador jugador2 = new Jugador(salaDeEspera.get(1));
            Equipo equipo1   = new Equipo(jugador1);
            Equipo equipo2   = new Equipo(jugador2);
            partida          = new Partida(equipo1, equipo2, numeroJuegos);
            partida.repartir();
        }else if(numeroJugadores==4){
            Jugador jugador1 = new Jugador(salaDeEspera.get(0));
            Jugador jugador2 = new Jugador(salaDeEspera.get(1));
            Jugador jugador3 = new Jugador(salaDeEspera.get(2));
            Jugador jugador4 = new Jugador(salaDeEspera.get(3));
            Equipo equipo1   = new Equipo(jugador1,jugador3);
            Equipo equipo2   = new Equipo(jugador2,jugador4);
            partida          = new Partida(equipo1, equipo2, numeroJuegos);
            partida.repartir();
        }
        enviaCartas();
        Carta pinte = partida.asignarTriunfo();
        enviaPinte(pinte);
    }



    private void enviaCartas(){
        Jugador [] listaJugadores = partida.getListajug();
        for(int i = 0;i<listaJugadores.length;i++){
            String nombre = listaJugadores[i].getNombre();
            Carta[] cartas = listaJugadores[i].getMano();
            Carta carta1= cartas[0];
            Carta carta2= cartas[1];
            Carta carta3 = cartas[2];
            //Esta es la parte del null pointer exception con las cartas
            Log.i(TAG,"Tamaño mano "+cartas.length+" "+carta1.getNumero()+carta2.getNumero()+carta3.getPalo());
            String cartasFormato=Integer.toString(cartas[0].getNumero())+
                    cartas[0].getPalo()+":"+
                    Integer.toString(cartas[1].getNumero())+
                    cartas[1].getPalo()+":"+
                    Integer.toString(cartas[2].getNumero())+
                    cartas[2].getPalo();
            Mensaje mensaje = new Mensaje(null,nombre+"::"+cartasFormato,"cartas");
            servidor.enviaMensaje(mensaje);
        }
    }

    private void enviaPinte(Carta pinte){
        Mensaje mensaje = new Mensaje(null,pinte.getNumero()+pinte.getPalo(),"pinte");
        servidor.enviaMensaje(mensaje);
    }

    public int getJugadorMano(){
        //Esto solo es valido si se usan los nombres como identificador de clientes
        //e identificador de turno
        Jugador [] listaJugadores = partida.getListajug();
        Jugador mano = listaJugadores[partida.getNumJugadorMano()];
        return Integer.valueOf(mano.getNombre());
    }
}
