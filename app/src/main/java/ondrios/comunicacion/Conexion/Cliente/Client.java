package ondrios.comunicacion.Conexion.Cliente;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import ondrios.comunicacion.ClientActivity;
import ondrios.comunicacion.Conexion.Mensaje;
import ondrios.comunicacion.Conexion.NsdHelper;
import ondrios.comunicacion.ServerActivity;

import static java.lang.Thread.sleep;

/**
 * Created by Mario on 29/12/2015.
 */
public class Client  {

    private String TAG = "Client";

    private Context context;
    private int contextID;

    private NsdFinder finder;
    private Socket socketServidor;

    private String id;
    private int quedan=99999;

    public Client (Context context, int id){
        this.context=context;
        this.contextID = id;
        this.finder = new NsdFinder(this);
        this.finder.iniciaBusqueda();
    }

    public Client (Context context, int port, int id){
        this.context=context;
        this.contextID = id;
        NsdServiceInfo n = new NsdServiceInfo();
        n.setHost(null);
        n.setPort(port);
        GetSocketServidorLocalTarea tareaGetSocket = new GetSocketServidorLocalTarea();
        tareaGetSocket.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, port);
    }

    public ArrayList<String> getServiciosDisponibles(){
        return finder.getServiciosDisponibles();
    }

    public void conectar(String servicename)  {
        NsdServiceInfo serviceInfo = finder.resuelve(servicename);
        if(serviceInfo!=null) {
            GetSocketServidorTarea tareaGetSocket = new GetSocketServidorTarea();
            tareaGetSocket.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serviceInfo);
        }else {
            ClientActivity ca  = (ClientActivity)context;
            ca.notificaFalloServicio();
        }
    }

    public void setSocketServidor(Socket socketServidor){
        //Guarda el socket del servidor
        this.socketServidor=socketServidor;
        if (contextID == 1) {
            ClientActivity ca = (ClientActivity) context;
            ca.notificaServidorEncontrado();
        }
        //Una vez obtenido el socket espera a que el servidor le asigne un identificador
        RecibeMensajeTarea recibeId= new RecibeMensajeTarea();
        recibeId.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,socketServidor);
    }

    public void recibeMensaje(String datos){
        String [] d = datos.split("&");

        switch (d[0]){
            case "identificador": //Recibe el identificador que le ha dado el servidor
                id = d[1];
                Log.i(TAG,"Indentificador añadido "+id);
                RecibeMensajeTarea recibeIdentificador= new RecibeMensajeTarea();
                recibeIdentificador.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                break;

            case "repite":  //Publica el mensaje que ha repetido el servidor
                String [] mensaje = d[1].split("::");
                String turno = mensaje[0];
                String cuerpoDelMensaje = mensaje[1];
                if (turno.equals(this.id)) {
                    //Notifica a la vista del mensaje si ademas es su turno muestra los botones para mandar mensaje
                    if (contextID == 0) {
                        ServerActivity sa = (ServerActivity) context;
                        sa.publicaMensaje(cuerpoDelMensaje);
                        sa.muestraBotones();
                    } else {
                        ClientActivity ca = (ClientActivity) context;
                        ca.publicaMensaje(cuerpoDelMensaje);
                        ca.muestraBotones();
                    }
                } else {
                    //Si no solo muestra el mensaje y espera la recepcion de nuevos
                    if (contextID == 0) {
                        ServerActivity sa = (ServerActivity) context;
                        sa.publicaMensaje(cuerpoDelMensaje);
                    } else {
                        ClientActivity ca = (ClientActivity) context;
                        ca.publicaMensaje(cuerpoDelMensaje);
                    }
                    RecibeMensajeTarea recibeRepite= new RecibeMensajeTarea();
                    recibeRepite.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                }
                break;
            case "cartas": //Publica las cartas que le ha dado el servidor
                //Formato cartas, ej: o1:o2:o3
                String [] m = d[1].split("::");
                String [] cartas = m[0].split(":");
                //Publica en la vista las cartas
                Log.i(TAG, "Las cartas son: " + cartas[0] + " " + cartas[1] + " " + cartas[2] + " y el pinte es " + m[1]);
                RecibeMensajeTarea recibeCartas= new RecibeMensajeTarea();
                recibeCartas.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                if (contextID == 0) {
                    ServerActivity sa = (ServerActivity) context;
                    sa.setCarta1(cartas[0]);
                    sa.setCarta2(cartas[1]);
                    sa.setCarta3(cartas[2]);
                    sa.setPinte(m[1]);
                } else {
                    ClientActivity ca = (ClientActivity) context;
                    ca.setCarta1(cartas[0]);
                    ca.setCarta2(cartas[1]);
                    ca.setCarta3(cartas[2]);
                    ca.setPinte(m[1]);
                }

                break;
            case "tira": //Notificacion de que le toca tirar al cliete
                //Te toca tirar


                Log.i(TAG, "Te toca tirar");
                String lanzador = d[1];
                if (lanzador.equals(id)){
                    if (contextID == 0) {
                        ServerActivity sa = (ServerActivity) context;
                        sa.notificaTurno();
                        sa.setTurno();
                    } else {
                        ClientActivity ca = (ClientActivity) context;
                        ca.notificaTurno();
                        ca.setTurno();
                    }

                } else {
                    switch(quedan){
                        case 2:
                            if (contextID == 0) {
                                ServerActivity sa = (ServerActivity) context;
                                sa.eliminaUna();
                            } else {
                                ClientActivity ca = (ClientActivity) context;
                                ca.eliminaUna();
                            }
                            quedan--;
                            break;
                        case 1:
                            if (contextID == 0) {
                                ServerActivity sa = (ServerActivity) context;
                                sa.eliminaDos();
                            } else {
                                ClientActivity ca = (ClientActivity) context;
                                ca.eliminaDos();
                            }
                            quedan--;
                            break;
                        default:
                            break;
                    }
                    RecibeMensajeTarea recibeTira= new RecibeMensajeTarea();
                    recibeTira.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                }
               // RecibeMensajeTarea recibeTira= new RecibeMensajeTarea();
                //recibeTira.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);

                break;
            case "ganador_baza":
                String jugador = d[1];

                RecibeMensajeTarea recibeGanador= new RecibeMensajeTarea();
                recibeGanador.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
               // RecibeMensajeTarea recibeGanador2= new RecibeMensajeTarea();
                //recibeGanador2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                break;
            case "muestra_carta":
                String[] mens=d[1].split("::");
                String carta = mens[1];
                if (contextID == 0) {
                    ServerActivity sa = (ServerActivity) context;
                    sa.tiraCartaContrario(carta);
                } else {
                    ClientActivity ca = (ClientActivity) context;
                    ca.tiraCartaContrario(carta);
                }
                RecibeMensajeTarea recibeMuestraCarta= new RecibeMensajeTarea();
                recibeMuestraCarta.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                break;
            case "roba":
                String [] mr = d[1].split("::");
                String cartaRoba= mr[0];
                String turnoRoba = mr[1];
                if (contextID == 0) {
                    ServerActivity sa = (ServerActivity) context;
                    sa.reparteCartas(cartaRoba);
                    if (turnoRoba.equals(id)){
                        sa.notificaTurno();
                        sa.setTurno();
                    }else {
                        RecibeMensajeTarea recibeRoba= new RecibeMensajeTarea();
                        recibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                    }
                } else {
                    ClientActivity ca = (ClientActivity) context;
                    ca.reparteCartas(cartaRoba);
                    if (turnoRoba.equals(id)) {
                        ca.notificaTurno();
                        ca.setTurno();
                    }else {
                        RecibeMensajeTarea recibeRoba= new RecibeMensajeTarea();
                        recibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                    }
                }
                break;
            case "roba_pinte":
                String [] mrp = d[1].split("::");
                String cartaRobaPinte= mrp[0];
                String turnoRobaPinte = mrp[1];
                quedan=2;
                if (contextID == 0) {
                    ServerActivity sa = (ServerActivity) context;
                    sa.desaparecePinte(cartaRobaPinte);
                    if (turnoRobaPinte.equals(id)){
                        sa.notificaTurno();
                        sa.setTurno();
                    }else {
                        RecibeMensajeTarea recibeRoba= new RecibeMensajeTarea();
                        recibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                    }
                } else {
                    ClientActivity ca = (ClientActivity) context;
                    ca.desaparecePinte(cartaRobaPinte);
                    if (turnoRobaPinte.equals(id)) {
                        ca.notificaTurno();
                        ca.setTurno();
                    }else {
                        RecibeMensajeTarea recibeRoba= new RecibeMensajeTarea();
                        recibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                    }
                }
                break;
            case "apaga":
                //No comprobado si null que algunas veces recibia null en este caso
                enviaMensaje("null","OK_apaga");
                try {
                    socketServidor.close();
                    if(contextID==1){
                        ClientActivity c = (ClientActivity)context;
                        c.notificaApaga();
                    }
                    Log.i(TAG,"Desconectado");
                } catch (IOException e) {
                    Log.e(TAG,"Error al cerrar el socket servidor",e);
                }
                break;
        }
    }

    public void enviaMensaje(String datos,String protocolo){

        switch (protocolo){
            case "mensaje":
                EnviaMensajeTarea enviaMensaje = new EnviaMensajeTarea();
                RecibeMensajeTarea recibeMensaje= new RecibeMensajeTarea();
                Mensaje mensaje = new Mensaje(socketServidor,datos,protocolo);
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                recibeMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                break;
            case "tira_carta": //Manda la posicion de la carta
                EnviaMensajeTarea enviaCarta = new EnviaMensajeTarea();
                RecibeMensajeTarea recibeMensajeCarta= new RecibeMensajeTarea();
                Mensaje mensajeCarta = new Mensaje(socketServidor,datos,protocolo);
                enviaCarta.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensajeCarta);
                recibeMensajeCarta.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                break;
        }
    }

    public void enviaCarta(int i){
        //Mensaje mensaje = new Mensaje(socketServidor,Integer.toString(i),"tira_carta");
        enviaMensaje(Integer.toString(i),"tira_carta");
    }


    /* ***** TAREAS ****** */

    /**
     * Tarea que obtiene el socket del servidor a partir de la direccion ip y puerto obtenidos del procedimiento nsd
     */
    public class GetSocketServidorTarea extends AsyncTask<NsdServiceInfo, Void, Socket> {

        String TAG = "GetSocketServidorTarea";

        @Override
        protected Socket doInBackground(NsdServiceInfo... params) {
            NsdServiceInfo nsdServiceInfo = params[0];
            Socket socketServidor = null;
            try {
               socketServidor = new Socket(nsdServiceInfo.getHost(), nsdServiceInfo.getPort());
                Log.i(TAG,"Obtenido socket "+socketServidor.getInetAddress().getHostAddress());
            } catch (IOException e) {
                Log.e(TAG, "ERROR: Al crear el socket con el servidor", e);
            }
            return socketServidor;
        }

        @Override
        protected void onPostExecute(Socket socket){
            setSocketServidor(socket);
        }
    }

    /**
     * Tarea que obtiene el socket del servidor a partir de la direccion ip y puerto obtenidos del procedimiento nsd
     */
    public class GetSocketServidorLocalTarea extends AsyncTask<Integer, Void, Socket> {

        String TAG = "GetSocketServidorLocalTarea";

        @Override
        protected Socket doInBackground(Integer... params) {
            int port = params[0];
            Socket socketServidor = null;
            try {
                socketServidor = new Socket("localhost", port);
                Log.i(TAG, "Obtenido socket " + socketServidor.getInetAddress().getHostAddress());
            } catch (IOException e) {
                Log.e(TAG, "ERROR: Al crear el socket con el servidor", e);
            }
            return socketServidor;
        }

        @Override
        protected void onPostExecute(Socket socket){
            setSocketServidor(socket);
        }
    }

    /**
     * Tarea que se encarga de coger los mensajes que llegan desde el servidor
     */
    public class RecibeMensajeTarea extends AsyncTask<Socket,Void,String>{
        String TAG = "ReciveMensajeTarea";
        @Override
        protected String doInBackground(Socket... params) {
            Socket socket = params[0];
            Log.i(TAG,"Esperando mensaje de "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
            try {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String datos = lector.readLine();
                Log.i(TAG, "Mensaje recibido: " + datos);
                return datos;
            } catch (IOException e) {
                Log.e(TAG, "ERROR: Al recibir el mensaje", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String datos){
            recibeMensaje(datos);
        }
    }

    /**
     * Tarea que se encarga de enviar los mensajes al servidor
     */
    public class EnviaMensajeTarea extends AsyncTask<Mensaje,Void,Void>{
        String TAG = "EnviaMensajeTarea";
        @Override
        protected Void doInBackground(Mensaje... params) {
            Socket socket = params[0].getSocket();
            String datos = params[0].getProtocolo()+"&"+params[0].getMensaje();
            try {
                PrintWriter escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                escritor.println(datos);
                escritor.flush();
                Log.i(TAG, "Enviado mensaje " + datos);
            } catch (IOException e) {
                Log.e(TAG, "ERROR: Al recibir el mensaje", e);
            }
            return null;
        }
    }

    public Context getContext() {
        return context;
    }
}
