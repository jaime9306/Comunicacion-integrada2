package ondrios.comunicacion.Juego;

/**
 * Implementa una carta de la baraja española
 */
public class Carta {

    private final int numero;
    private final String palo;

    /**
     * Inicializa una nueva carta con el número y el palo indicados
     * @param numero representa el número de la carta
     * @param palo representa el palo al que pertenece la carta
     */
    public Carta(int numero, String palo){
        this.numero = numero;
        this.palo = palo;
    }

    /**
     * Devuelve la puntuación de la carta para el conteo final
     * @return entero que representa la puntuación de la carta
     */
    public int getPuntos() {
        int p;
        switch (numero){
            case 1:
                p = 11;
                break;
            case 3:
                p = 10;
                break;
            case 10:
                p = 2;
                break;
            case 11:
                p = 3;
                break;
            case 12:
                p = 4;
                break;
            default:
                p = 0;
                break;
        }

        return p;
    }

    /**
     * Devuelve el palo de la carta
     * @return String que representa el palo al que pertenece la carta
     */
    public String getPalo(){
        return palo;
    }

    /**
     * Devuelve el número de la carta
     * @return entero que representa el numero de la carta
     */
    public int getNumero(){
        return numero;
    }

    /**
     * Devuelve el valor de la carta para determinar la ganadora de una baza. No es la puntuación
     * de la carta para el conteo final, solo se utiliza para la baza.
     * @return entero que representa el valor de la carta
     */
    public int getValor(){
        int valor = 0;

        switch (numero){
            case 1:
                valor = 12;
                break;
            case 2:
                valor = 2;
                break;
            case 3:
                valor = 11;
                break;
            case 4:
                valor = 4;
                break;
            case 5:
                valor = 5;
                break;
            case 6:
                valor = 6;
                break;
            case 7:
                valor = 7;
                break;
            case 10:
                valor = 8;
                break;
            case 11:
                valor = 9;
                break;
            case 12:
                valor = 10;
                break;
        }

        return valor;
    }
}
