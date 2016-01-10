package ondrios.comunicacion.Juego;

import android.util.Log;

import java.util.ArrayList;

import ondrios.comunicacion.Conexion.Mensaje;
import ondrios.comunicacion.Conexion.Servidor.Server;

import static java.lang.Thread.sleep;

/**
 * Created by Mario on 09/01/2016.
 */
public class MotorJuego {

    private String TAG = "MotorJuego";

    private Server servidor;
    private Partida partida;

    private int njugadores;
    private ArrayList<Carta> baza;
    private ArrayList<String> orden;
    private ArrayList<Carta> jugadaEq1;
    private ArrayList<Carta> jugadaEq2;

    private Carta pinte;

    public MotorJuego(Server server, Partida partida){
        this.servidor = server;
        this.partida = partida;
        this.njugadores=this.partida.getListajug().length;
        this.baza=new ArrayList<>();
        this.orden= new ArrayList<>();
        this.jugadaEq1= new ArrayList<>();
        this.jugadaEq2= new ArrayList<>();
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
                orden.add(nombre);
                tirada=listaJugadores[i].echar(carta);
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
                 Carta ganadora = partida.determinarCartaGanadora(baza.get(0), baza.get(1));
                if (jugadaEq1.contains(ganadora)){
                    partida.getEquipos()[0].añadeMonton(baza.get(0));
                    partida.getEquipos()[0].añadeMonton(baza.get(1));

                }else if (jugadaEq2.contains(ganadora)){
                    partida.getEquipos()[1].añadeMonton(baza.get(0));
                    partida.getEquipos()[1].añadeMonton(baza.get(1));
                }
                String juadorGanador = orden.get(baza.indexOf(ganadora));
                //Mensaje mensajeGanador = new Mensaje(null,juadorGanador,"ganador_baza");

                this.baza=new ArrayList<>();
                this.orden= new ArrayList<>();
                this.jugadaEq1= new ArrayList<>();
                this.jugadaEq2= new ArrayList<>();

                servidor.setTurno(Integer.valueOf(juadorGanador));
                //servidor.enviaMensaje(mensajeGanador);
                roba();
            } else if(njugadores == 4){
                Carta ganadora = partida.determinarCartaGanadora(baza.get(0),baza.get(1));
                ganadora = partida.determinarCartaGanadora(ganadora ,baza.get(2));
                ganadora = partida.determinarCartaGanadora(ganadora ,baza.get(3));

                if (jugadaEq1.contains(ganadora)){
                    partida.getEquipos()[0].añadeMonton(baza.get(0));
                    partida.getEquipos()[0].añadeMonton(baza.get(1));
                    partida.getEquipos()[0].añadeMonton(baza.get(2));
                    partida.getEquipos()[0].añadeMonton(baza.get(3));

                }else if (jugadaEq2.contains(ganadora)){
                    partida.getEquipos()[1].añadeMonton(baza.get(0));
                    partida.getEquipos()[1].añadeMonton(baza.get(1));
                    partida.getEquipos()[1].añadeMonton(baza.get(2));
                    partida.getEquipos()[1].añadeMonton(baza.get(3));
                }
                String juadorGanador = orden.get(baza.indexOf(ganadora));
                //Mensaje mensajeGanador = new Mensaje(null,juadorGanador,"ganador_baza");
                servidor.setTurno(Integer.valueOf(juadorGanador));
               // servidor.enviaMensaje(mensajeGanador);
                roba();
            }

        }else{
            Mensaje mensajeTurno = new Mensaje(servidor.getClientes().get(servidor.getTurno()),"null","tira");
            servidor.enviaMensaje(mensajeTurno);
        }

    }

    public void roba(){
        Jugador [] lista = partida.getListajug();
        Carta [] cartasRobadas = new Carta [njugadores];
        for (int i =0; i<njugadores;i++){
            if (!partida.getBaraja().estaAcabada()){
                Carta cartaRobada = partida.getBaraja().saca();
                lista[i].robar(cartaRobada);
                cartasRobadas[i]=cartaRobada;
            }else {
                Carta cartaRobada = pinte;
                lista[i].robar(cartaRobada);
                cartasRobadas[i]=cartaRobada;
            }
        }
        for (int i =0; i<njugadores;i++){
            Mensaje mensaje = new Mensaje(servidor.getClientes().get(i),cartasRobadas[i].getPalo()+cartasRobadas[i].getNumero()+"::"+servidor.getTurno(),"roba");
            servidor.enviaMensaje(mensaje);
        }
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Mensaje mensajeTira = new Mensaje(servidor.getClientes().get(servidor.getTurno()),"null","tira");
        //servidor.enviaMensaje(mensajeTira);

    }




}
