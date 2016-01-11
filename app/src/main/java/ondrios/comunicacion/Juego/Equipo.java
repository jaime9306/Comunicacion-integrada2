package ondrios.comunicacion.Juego;

/* Implementa un equipo de la partida que puede ser de uno o dos jugadores */
public class Equipo {

    private Monton monton;
    public Jugador[] jugadores;
    private int tamEquipo;

    /**
     *  Inicializa un nuevo equipo de un componente con los jugador indicado
     *  @param jug1 jugador para crear el equipo
     */
    public Equipo(Jugador jug1){
        this.monton = new Monton();
        this.jugadores = new Jugador[1];
        this.jugadores[0] = jug1;
        this.tamEquipo = 1;
    }

    /**
     *  Inicializa un nuevo equipo de dos componentes con los jugadores indicados
     *  @param jug1 jugador 1
     *  @param jug2 jugador 2
     */
    public Equipo(Jugador jug1, Jugador jug2){
        this.monton = new Monton();
        this.jugadores = new Jugador[2];
        this.jugadores[0] = jug1;
        this.jugadores[1] = jug2;
        this.tamEquipo = 2;
    }

    /**
     * Devuelve el número de integrantes del equipo
     * @return tamEquipo el tamano del equipo
     */
    public int getTamEquipo(){
        return tamEquipo;
    }

    /**
     * Añade una Carta al Monton del equipo
     * @param carta carta que es añadida al monton
     */
    public void añadeMonton(Carta carta){
        monton.meter(carta);
    }

    /**
     * Devuelve el montón del equipo
     * @return monton del equipo
     */
    public Monton getMonton() {
        return monton;
    }
}
