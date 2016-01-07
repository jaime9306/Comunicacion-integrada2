package ondrios.comunicacion.Juego;

/* Implementa un equipo de la partida que puede ser de uno, dos o tres jugadores */
public class Equipo {

    private Monton monton;
    private int juegos;
    public Jugador[] jugadores;
    private int tamEquipo;

    /**
     *  Inicializa un nuevo equipo de un componente con los jugador indicado
     *  @param jug1 jugador para crear el equipo
     */
    public Equipo(Jugador jug1){
        this.monton = new Monton();
        this.juegos = 0;
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
        this.juegos = 0;
        this.jugadores = new Jugador[2];
        this.jugadores[0] = jug1;
        this.jugadores[1] = jug2;
        this.tamEquipo = 2;
    }

    /** Inicializa un nuevo equipo de tres componentes con los jugadores indicados
     * @param jug1 jugador 1
     * @param jug2 jugador 2
     * @param jug3 jugador 3
     */
    public Equipo(Jugador jug1, Jugador jug2, Jugador jug3){
        this.monton = new Monton();
        this.juegos = 0;
        this.jugadores = new Jugador[3];
        this.jugadores[0] = jug1;
        this.jugadores[1] = jug2;
        this.jugadores[2] = jug3;
        this.tamEquipo = 3;
    }

    /**
     * Devuelve el número de integrantes del equipo
     * @return tamEquipo el tamano del equipo
     */
    public int getTamEquipo(){
        return tamEquipo;
    }

    /**
     * Suma un juego al equipo
     */
    public void sumarJuego(){
        juegos ++;
    }

    /**
     * Devuelve el numero de juegos del equipo
     * @return numero de juegos que ha ganado el equipo
     */
    public int getJuegos(){
        return juegos;
    }
}
