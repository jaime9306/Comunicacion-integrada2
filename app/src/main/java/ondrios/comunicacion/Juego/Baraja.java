package ondrios.comunicacion.Juego;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Implementa el mazo de cartas de la baraja española que utilizan los jugadores
 */
public class Baraja {

    private final ArrayList<Carta> baraja;


    /**
     * Inicializa una nueva baraja española
     */
    public Baraja(){
        int i;
        baraja = new ArrayList<>();
        String[] palos = {"o", "c", "e", "b"};
        for(String palo: palos){
            //Cartas de 1 al 7
            for(i = 1; i <= 7; i++){
                baraja.add(new Carta(i, palo));
            }
            //Cartas de la sota al rey
            for(i = 10; i <= 12; i++){
                baraja.add(new Carta(i, palo));
            }
        }
    }

    /**
     * Mezcla las cartas de la baraja aleatoriamente
     */
    public void mezclar(){
        Collections.shuffle(baraja);
    }

    /**
     * Devuelve la primera carta de la baraja
     * @return Carta que se encuentra en la primera posición de la baraja
     */
    public Carta saca(){
        return baraja.remove(0);
    }

    /**
     * Devuelve true si no hay cartas en la baraja
     * @return true si no hay Cartas en la baraja y false en caso contrario
     */
    public boolean noAcabada(){
        return baraja.size() != 0;
    }

}
