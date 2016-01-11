package ondrios.comunicacion.Conexion;

import java.net.Socket;

/**
 * Clase mensaje. Contiene el socket al que se manda el mensaje, el protocolo
 * y el mensaje correspondiente a ese protocolo.
 */
public class Mensaje {
    private final Socket socket;
    private String mensaje;
    private final String protocolo;

    /**
     * Constuctor del mensaje.
     * @param socket Socket al que se le manda el mensaje.
     * @param mensaje Mensaje en formato cadena de caracteres.
     * @param protocolo Tipo de mensaje.
     */
    public Mensaje(Socket socket, String mensaje, String protocolo){
        this.socket=socket;
        this.mensaje=mensaje;
        this.protocolo=protocolo;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
