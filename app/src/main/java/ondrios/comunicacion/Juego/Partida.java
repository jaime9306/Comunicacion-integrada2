package ondrios.comunicacion.Juego;

/**
 * Implementa la lógica de la partida de brisca
 */
public class Partida {

    private Baraja baraja;
    private final Equipo[] equipos = new Equipo[2];
    private Jugador[] listajug;
    private String paloTriunfo;
    private int numJugadorMano;

    /**
     * Inicializa una partida con los equipos indicados y le asigna una baraja.
     * El número de integrantes del equipo debe ser el mismo.
     * @param eq1 representa uno de los equipos de la partida
     * @param eq2 representa uno de los equipos de la partida
     */
    public Partida(Equipo eq1, Equipo eq2){
        if(eq1.getTamEquipo() == eq2.getTamEquipo()) {
            equipos[0] = eq1;
            equipos[1] = eq2;
            baraja = new Baraja();
            int tamEq = eq1.getTamEquipo();
            listajug = new Jugador[2* tamEq];

            //Array con la lista de jugadores alternados por equipos
            for(int i = 0; i < tamEq; i++){
                listajug[i*2] = eq1.jugadores[i];
                listajug[(i*2)+1] = eq2.jugadores[i];
            }

            //Asigna aleatoriamente el jugador mano
            numJugadorMano = (int) Math.floor(Math.random()* tamEq *2);
        }
    }

    /**
     *  Mezcla la baraja y reparte 3 cartas de la baraja a cada jugador de la partida
     */
    public void repartoInicial(){
        int i, j;

        baraja.mezclar();

        for(i = 0; i < 3; i++){
            for(j = 0; j < listajug.length; j++){
                listajug[j].robar(baraja.saca());
            }
        }
    }

    /**
     * Devuelve la primera carta de la baraja, determinando que palo es triunfo
     * @return primera Carta de la baraja
     */
    public Carta asignarTriunfo(){
        Carta c = baraja.saca();
        paloTriunfo = c.getPalo();
        return c;
    }

    /**
     * Devuelve true si la carta es del palo del triunfo y false en caso contrario
     * @param c representa la Carta que se quiere determinar si es triunfo
     * @return true si la carta es triunfo y false en caso contrario
     */
    private boolean esTriunfo(Carta c){
        return c.getPalo().equals(paloTriunfo);
    }

    /**
     * Devuelve la carta ganadora de la baza. La carta "primera" es la que se ha jugado en primer lugar
     * @param primera representa la Carta que se ha jugado en primer lugar en la baza
     * @param segunda representa la Carta que se ha jugado en segundo lugar en la baza
     * @return Carta que resulta ganadora de la comparación de las dos cartas en la baza
     */
    public Carta determinarCartaGanadora(Carta primera, Carta segunda){

        String palo1, palo2;
        int valor1, valor2;
        boolean triunfo1, triunfo2;

        palo1 = primera.getPalo();
        palo2 = segunda.getPalo();
        valor1 = primera.getValor();
        valor2 = segunda.getValor();
        triunfo1 = esTriunfo(primera);
        triunfo2 = esTriunfo(segunda);

        if(triunfo1) { //Primera carta es triunfo
            if (triunfo2) { //Segunda carta es triunfo
                if (valor1 > valor2) {
                    return primera;
                } else {
                    return segunda;
                }
            } else { //Segunda carta no es triunfo
                return primera;
            }
        } else{ //Primera carta no es triunfo
            if(triunfo2){ //Segunda carta es triunfo
                return segunda;
            } else{ //Segunda carta no es triunfo
                if(palo1.equals(palo2)){ //Las dos cartas son del mismo palo no triunfo
                    if (valor1 > valor2) {
                        return primera;
                    } else {
                        return segunda;
                    }
                } else{ //Las dos cartas son de distinto palo no triunfo
                    return primera;
                }
            }

        }
    }

    /**
     * Devuelve la lista de jugadores de la partida
     * @return array de Jugadores de la partida
     */
    public Jugador[] getListajug() {
        return listajug;
    }

    /**
     * Entero que representa el número de jugador que es mano
     * @return numero de jugador que es mano
     */
    public int getNumJugadorMano() {
        return numJugadorMano;
    }

    /**
     * Devuelve los Equipos de la partida
     * @return array con los equipos de la partida
     */
    public Equipo[] getEquipos() {
        return equipos;
    }

    /**
     * Devuelve la Baraja de la partida
     * @return la baraja de la partida
     */
    public Baraja getBaraja() {
        return baraja;
    }
}
