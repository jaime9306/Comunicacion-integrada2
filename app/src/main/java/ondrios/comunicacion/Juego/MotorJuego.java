package ondrios.comunicacion.Juego;

import android.util.Log;

import java.util.ArrayList;

import ondrios.comunicacion.Conexion.Mensaje;
import ondrios.comunicacion.Conexion.Servidor.Server;

/**
 * Created by Mario on 09/01/2016.
 */
public class MotorJuego {

    private final String TAG = "MotorJuego";

    private final Server servidor;
    private final Partida partida;

    private final int njugadores;
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

    /**
     * Envia las cartas iniciales a cada jugador.
     */
    private void enviaCartas(){
        Jugador [] listaJugadores = partida.getListajug();
        ArrayList<Mensaje> mensajes = new ArrayList <>();
        for (Jugador listaJugadore : listaJugadores) {
            String nombre = listaJugadore.getNombre();
            Carta[] cartas = listaJugadore.getMano();

            String cartasFormato = cartas[0].getPalo() + Integer.toString(cartas[0].getNumero()) + ":" +
                    cartas[1].getPalo() + Integer.toString(cartas[1].getNumero()) + ":" +
                    cartas[2].getPalo() + Integer.toString(cartas[2].getNumero());
            Mensaje mensaje = new Mensaje(null, nombre + "::" + cartasFormato + "::" + pinte.getPalo() + pinte.getNumero(), "cartas");
            mensajes.add(mensaje);
        }
        for(Mensaje m: mensajes){
            servidor.enviaMensaje(m);
        }
    }

    /**
     * Devuelve el turno interno de la partida
     * @return turno.
     */
    public int getTurno(){
        return partida.getNumJugadorMano();
    }

    /**
     * Actualiza el estado interno del juego en funcion del jugador que ha tirado y su posicion de la
     * mano que ha tirado.
     * @param jugador Jugador que tira
     * @param carta Posicion de la carta en la mano, que tira.
     */
    public void tiraCarta(int jugador, int carta){

        Jugador [] listaJugadores = partida.getListajug();
        Carta tirada = null;

        //Busca el jugador que ha tirado y le hace echar la carta.
        for (Jugador listaJugadore : listaJugadores) {
            String nombre = listaJugadore.getNombre();
            if (nombre.equals(Integer.toString(jugador))) {
                orden.add(nombre);
                tirada = listaJugadore.echar(carta);
            }
        }

        //Guarda la carta tirada en la baza
        baza.add(tirada);

        Equipo [] equipos = partida.getEquipos();
        Equipo eq1        = equipos[0];
        Equipo eq2        = equipos[1];
        String nombre     = Integer.toString(jugador);

        // Guarda la carta tirada del jugador en la jugada de cada equipo para luego saber de que
        // equipo es la carta ganadora.
        for(int i = 0; i<eq1.jugadores.length;i++){
            if(nombre.equals(eq1.jugadores[i].getNombre())){
                jugadaEq1.add(tirada);
            }else if (nombre.equals(eq2.jugadores[i].getNombre())){
                jugadaEq2.add(tirada);
            }
        }

        //Envia la carta que ha tirado
        assert tirada != null;
        Mensaje mensaje = new Mensaje(null,Integer.toString(jugador)+"::"+tirada.getPalo()+Integer.toString(tirada.getNumero()),"muestra_carta");
        servidor.enviaMensaje(mensaje);

        //Si ya han tirado todos resuelve el ganador de la baza.
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
                Log.i(TAG,"Jugador ganador "+juadorGanador);

                this.baza=new ArrayList<>();
                this.orden= new ArrayList<>();
                this.jugadaEq1= new ArrayList<>();
                this.jugadaEq2= new ArrayList<>();

                servidor.setTurno(Integer.valueOf(juadorGanador));
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
                Log.i(TAG,"Jugador ganador "+juadorGanador);
                servidor.setTurno(Integer.valueOf(juadorGanador));
                roba();
            }

        }else{ // Si no manda al siguiente jugador tirar.
            Mensaje mensajeTurno = new Mensaje(servidor.getClientes().get(servidor.getTurno()),Integer.toString(servidor.getTurno()),"tira");
            servidor.enviaMensaje(mensajeTurno);
        }

    }

    /**
     * Roba una carta para cada jugador y se la manda.
     */
    public void roba(){

        Jugador [] lista = partida.getListajug();
        Carta [] cartasRobadas = new Carta [njugadores];
        boolean vacia = true;
        for(int i =0; i<njugadores;i++){
            vacia = vacia && lista[i].manoVacia();
        }

        if(!partida.getBaraja().estaAcabada()){
            int j = servidor.getTurno();
            //Para cada jugador roba una carta
            for (int i =0; i<njugadores;i++) {
                // Si la baraja no esta acabada la saca de la baraja
                if (!partida.getBaraja().estaAcabada()) {
                    Carta cartaRobada = partida.getBaraja().saca();
                    lista[j].robar(cartaRobada);
                    cartasRobadas[j] = cartaRobada;
                } else { // Si esta acabada coge la del pinte.
                    Carta cartaRobada = pinte;
                    lista[j].robar(cartaRobada);
                    cartasRobadas[j] = cartaRobada;
                }
                j = (j + 1) % njugadores;

            }
            if(!partida.getBaraja().estaAcabada()) {
                //Envia un mensaje a todos los jugadores de su carta robada
                for (int i = 0; i < njugadores; i++) {
                    // Mensaje para todos los clientes. El formato del cuerpo: <<carta robada palo-numero>>::<<turno del que tira despues de robar>>
                    Mensaje mensaje = new Mensaje(servidor.getClientes().get(i), cartasRobadas[i].getPalo() + cartasRobadas[i].getNumero() + "::" + servidor.getTurno(), "roba");
                    servidor.enviaMensaje(mensaje);
                }
            }else{
                for (int i = 0; i < njugadores; i++) {
                    // Mensaje para todos los clientes. El formato del cuerpo: <<carta robada palo-numero>>::<<turno del que tira despues de robar>>
                    Mensaje mensaje = new Mensaje(servidor.getClientes().get(i), cartasRobadas[i].getPalo() + cartasRobadas[i].getNumero() + "::" + servidor.getTurno(), "roba_pinte");
                    servidor.enviaMensaje(mensaje);
                }
            }
        } else if(!vacia){ //Si la baraja está acabada solo puede tirar
            for(int i = 0;i<servidor.getClientes().size();i++) {
                Mensaje mensajeTurno = new Mensaje(servidor.getClientes().get(i), Integer.toString(servidor.getTurno()), "roba_null");
                servidor.enviaMensaje(mensajeTurno);
            }
        }else {
            Equipo [] equipos = partida.getEquipos();
            Monton monton0 = equipos[0].getMonton();
            Monton monton1 = equipos[1].getMonton();

            int puntuacion0;
            int puntuacion1;
            String ganador;

            puntuacion0 = monton0.contar();
            puntuacion1 = monton1.contar();

            //Ganador equipo0
            if(puntuacion0 > puntuacion1){
                Log.d("Monton0: ",Integer.toString(puntuacion0));
                Log.d("Monton1: ",Integer.toString(puntuacion1));
                ganador = lista[0].getNombre();
                for (int j = 0; j<servidor.getClientes().size();j++) {
                    Mensaje mensaje = new Mensaje(servidor.getClientes().get(j),ganador+"::"+puntuacion0,"fin_partida");
                    servidor.enviaMensaje(mensaje);
                }
            } else{
                //Ganador equipo1
                if(puntuacion0 < puntuacion1){
                    Log.d("Monton0: ",Integer.toString(puntuacion0));
                    Log.d("Monton1: ",Integer.toString(puntuacion1));
                    ganador = lista[1].getNombre();
                    for (int j = 0; j<servidor.getClientes().size();j++) {
                        Mensaje mensaje = new Mensaje(servidor.getClientes().get(j),ganador+"::"+puntuacion1,"fin_partida");
                        servidor.enviaMensaje(mensaje);
                    }
                  //Empate
                } else{
                    Log.d("Monton0: ",Integer.toString(puntuacion0));
                    Log.d("Monton1: ",Integer.toString(puntuacion1));
                    ganador = "Empate";
                    for (int j = 0; j<servidor.getClientes().size();j++) {
                        Mensaje mensaje = new Mensaje(servidor.getClientes().get(j),ganador+"::"+"60","fin_partida");
                        servidor.enviaMensaje(mensaje);
                    }
                }
            }

        }


    }

}
