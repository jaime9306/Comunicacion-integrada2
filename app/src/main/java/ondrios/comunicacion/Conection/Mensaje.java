package ondrios.comunicacion.Conection;

import java.net.Socket;

/**
 * Created by Mario on 02/01/2016.
 */
public class Mensaje {
    Socket socket;
    String mensaje;
    String turno;

    public Mensaje(Socket socket, String mensaje){
        this.socket=socket;
        this.mensaje=mensaje;

    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
