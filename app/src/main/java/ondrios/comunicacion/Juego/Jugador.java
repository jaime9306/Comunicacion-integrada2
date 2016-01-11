package ondrios.comunicacion.Juego;

/**
 * Implementa un jugador de la partida
 */
public class Jugador {

    String nombre;
    private Carta[] mano;

    /**
     * Inicializa un jugador con el nombre indicado
     * @param nombre representa el nombre que se le asigna al jugador
     */
    public Jugador(String nombre){
        this.nombre = nombre;
        mano = new Carta[3];
    }

    /**
     * Devuelve la primera posición del array mano que está vacía
     * @param mano array que representa la cartas que tiene un jugador
     * @return entero con la primera posición vacía del array mano
     */
    private int posicionVacia(Carta[] mano){
        if(mano[0] == null){
            return 0;
        }
        else{
            if(mano[1] == null){
                return 1;
            }
            else{
                return 2;
            }
        }
    }

    /**
     * Añade una carta a la mano del jugador
     * @param c representa la Carta que se va a añadir a la mano del jugador
     */
    public void robar(Carta c){
        int pos = posicionVacia(mano);
        mano[pos] = c;
    }

    /**
     * Devuelve la carta de la posición i del array y la elimina de la mano del jugador
     * @param i representa la posición de la carta que se va a echar
     * @return Carta que está en la posición indicada por el parámetro i
     */
    public Carta echar(int i){
        Carta c = mano[i];
        mano[i] = null;
        return c;
    }

    public String getNombre() {
        return nombre;
    }

    public Carta[] getMano() {
        return mano;
    }

    public boolean manoVacia(){
        return mano[0]==null&&mano[1]==null&mano[2]==null;
    }
}