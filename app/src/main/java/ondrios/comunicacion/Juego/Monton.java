package ondrios.comunicacion.Juego;

import java.util.ArrayList;

/**
 * Implementa el montón de cartas que los jugadores han ganado a lo largo de la partida
 */
public class Monton {

    private final ArrayList<Carta> monton;

    /**
     * Constructor. Inicializa un nuevo Montón
     */
    public Monton(){
        monton = new ArrayList<>();
    }

    /**
     * Añade una carta al montón
     * @param c Carta a añadir al montón
     */
    public void meter(Carta c){
        monton.add(c);
    }

    /**
     * Saca una carta del montón
     *
     * @return monton.remove(0) carta que elimina.
     */
    private Carta sacar(){
        return monton.remove(0);
    }

    /**
     * Devuelve la puntuación total del montón
     *
     * @return puntos totales del montón.
     */
    public int contar(){
        int puntos = 0;
        Carta carta;

        while(!monton.isEmpty()){
            carta = sacar();
            puntos += carta.getPuntos();
        }

        return puntos;
    }

}
