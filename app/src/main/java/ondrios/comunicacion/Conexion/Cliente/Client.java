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
import ondrios.comunicacion.ServerActivity;

import static java.lang.Thread.sleep;

/**
 * Clase cliente, se encarga de enviar y recibir los mensajes del servidor y notificar a la vista
 * en funcion de los mensajes que recibe del servidor.
 */
public class Client  {

    private final Context context;
    private final int contextID;

    private NsdFinder finder;
    private Socket socketServidor;

    private String id;
    private int quedan=99999;

    /**
     * Constructor de la clase cliente. Inicia la busqueda de servicios en la red. Para el cliente que no es local.
     * @param context Contexto donde se instancia la clase cliente (Solo puede ser ClientAcivity o Server Activity).
     * @param contextID Identificador del contexto, 0 si es ServerActivity, 1 si es ClientActivity.
     */
    public Client (Context context, int contextID){
        this.context   =context;
        this.contextID = contextID;
        this.finder    = new NsdFinder(this);
        this.finder.iniciaBusqueda();
    }
    /**
     * Constructor de la clase cliente. Crea la conexion con el servidor local. Para el cliente que es local.
     * @param context Contexto donde se instancia la clase cliente (Solo puede ser ClientAcivity o Server Activity).
     * @param port Puerto local del servidor.
     * @param contextID Identificador del contexto, 0 si es ServerActivity, 1 si es ClientActivity.
     */
    public Client (Context context, int port, int contextID){
        this.context     = context;
        this.contextID   = contextID;
        NsdServiceInfo n = new NsdServiceInfo();
        n.setHost(null);
        n.setPort(port);
        GetSocketServidorLocalTarea tareaGetSocket = new GetSocketServidorLocalTarea();
        tareaGetSocket.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, port);
    }

    /**
     * Devuelve los servicios diponibles.
     * @return Array de nombres de servicios dispnibles
     */
    public ArrayList<String> getServiciosDisponibles(){
        return finder.getServiciosDisponibles();
    }

    /**
     * Conecta el cliente al servidor con el nombre de servicio seleccionado.
     * @param servicename Nombre de servicio al que se va a conectar el cliente.
     */
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

    /**
     * Guarda el socket del servidor
     * @param socketServidor socket del servidor al que se tiene que conectar el cliente.
     */
    private void setSocketServidor(Socket socketServidor){
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

    
    private void recibeMensaje(String datos){
        String [] d = datos.split("&");
        String TAG = "Client";
        switch (d[0]){
            case "identificador": //Recibe el identificador que le ha dado el servidor
                id = d[1];
                Log.i(TAG,"Indentificador añadido "+id);
                RecibeMensajeTarea recibeIdentificador= new RecibeMensajeTarea();
                recibeIdentificador.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
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
                    RecibeMensajeTarea recibeTira= new RecibeMensajeTarea();
                    recibeTira.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                }
                break;

            case "ganador_baza":
                RecibeMensajeTarea recibeGanador= new RecibeMensajeTarea();
                recibeGanador.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
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
                        sa.notificaTurnoEl();
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
                        ca.notificaTurnoEl();
                        RecibeMensajeTarea recibeRoba= new RecibeMensajeTarea();
                        recibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                    }
                }
                break;
            case "roba_pinte":
                String [] mrp = d[1].split("::");
                String cartaRobaPinte= mrp[0];
                String turnoRobaPinte = mrp[1];
                quedan=3;
                if (contextID == 0) {
                    ServerActivity sa = (ServerActivity) context;
                    sa.desaparecePinte(cartaRobaPinte);
                    if (turnoRobaPinte.equals(id)){
                        sa.notificaTurno();
                        sa.setTurno();
                    }else {
                        sa.notificaTurnoEl();
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
                        ca.notificaTurnoEl();
                        RecibeMensajeTarea recibeRoba= new RecibeMensajeTarea();
                        recibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                    }
                }
                break;
            case "roba_null":
                String turnoRoban = d[1];
                if (contextID == 0) {
                    ServerActivity sa = (ServerActivity) context;
                    sa.recogeSinMazo();
                    if(quedan<=3){
                        switch (quedan){
                            case 3:
                                sa.eliminaUna();
                                quedan--;
                                break;
                            case 2:
                                sa.eliminaDos();
                                quedan--;
                                break;
                            default:
                                break;
                        }
                    }
                    if (turnoRoban.equals(id)){
                        sa.notificaTurno();
                        sa.setTurno();

                    }else {
                        sa.notificaTurnoEl();
                        RecibeMensajeTarea recibeRoba= new RecibeMensajeTarea();
                        recibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                    }
                } else {
                    ClientActivity ca = (ClientActivity) context;
                    ca.recogeSinMazo();
                    if(quedan<=3){
                        switch (quedan){
                            case 3:
                                ca.eliminaUna();
                                quedan--;
                                break;
                            case 2:
                                ca.eliminaDos();
                                quedan--;
                                break;
                            default:
                                break;
                        }
                    }
                    if (turnoRoban.equals(id)) {
                        ca.notificaTurno();
                        ca.setTurno();

                    }else {
                        ca.notificaTurnoEl();
                        RecibeMensajeTarea recibeRoba= new RecibeMensajeTarea();
                        recibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                    }
                }
                break;
            case "fin_partida":
                String [] datosFin = d[1].split("::");

                if(contextID == 0){
                    enviaMensaje("null", "OK_apaga");
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        socketServidor.close();
                        ServerActivity sa = (ServerActivity) context;
                        sa.notificaFinal(datosFin);
                        Log.i(TAG, "Desconectado");
                    } catch (IOException e) {
                        Log.e(TAG, "Error al cerrar el socket servidor", e);
                    }
                } else {
                    enviaMensaje("null", "OK_apaga");
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        socketServidor.close();
                        ClientActivity c = (ClientActivity) context;
                        c.notificaFinal(datosFin);
                        Log.i(TAG, "Desconectado");
                    } catch (IOException e) {
                        Log.e(TAG, "Error al cerrar el socket servidor", e);
                    }
                }


                break;
            case "apaga":
                //No comprobado si null que algunas veces recibia null en este caso
                enviaMensaje("null","OK_apaga");
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    socketServidor.close();
                    if(contextID==1){
                        ClientActivity c = (ClientActivity)context;
                        c.notificaApaga();
                    }else{
                        ServerActivity s = (ServerActivity)context;
                        s.notificaApaga();
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
            case "apaga":
                EnviaMensajeTarea enviaApaga = new EnviaMensajeTarea();
                RecibeMensajeTarea recibeApaga= new RecibeMensajeTarea();
                Mensaje mensajeApaga = new Mensaje(socketServidor,datos,protocolo);
                enviaApaga.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensajeApaga);
                recibeApaga.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketServidor);
                break;
        }
    }

    public void enviaCarta(int i){
        enviaMensaje(Integer.toString(i),"tira_carta");
    }


    /* ***** TAREAS ****** */

    /**
     * Tarea que obtiene el socket del servidor a partir de la direccion ip y puerto obtenidos del procedimiento nsd
     */
    public class GetSocketServidorTarea extends AsyncTask<NsdServiceInfo, Void, Socket> {

        final String TAG = "GetSocketServidorTarea";

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

        final String TAG = "GetSocketServidorLocalTarea";

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
        final String TAG = "ReciveMensajeTarea";
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
        final String TAG = "EnviaMensajeTarea";
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
    public String getIdentificador(){
        return id;
    }
    public Context getContext() {
        return context;
    }

}
