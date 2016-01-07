package ondrios.comunicacion.Conection.Cliente;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import ondrios.comunicacion.ClientActivity;
import ondrios.comunicacion.Conection.Mensaje;
import ondrios.comunicacion.Conection.NsdHelper;
import ondrios.comunicacion.Conection.Servidor.Server;
import ondrios.comunicacion.ServerActivity;

import static java.lang.Thread.sleep;

/**
 * Created by Mario on 29/12/2015.
 */
public class Client  {

    private String TAG = "Client";
    private NsdHelper nsdHelper;
    private NsdServiceInfo nsdServiceInfo;
    private Socket socketServidor;
    private Context context;
    private int contextID;
    private String id;


    public Client (Context context, int id){
        this.context=context;
        this.contextID = id;
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
        //Habria que refactorizar esto...
        nsdHelper = new NsdHelper(context,"Brisca");
        nsdHelper.initializeNsd();
        nsdHelper.discoverServices();
        ArrayList<String> lista = nsdHelper.getPartidas();
        Log.i(TAG,"Tamaño "+lista.size());
        return lista;
    }

    public void conectar(String serviceName)  {
        //Habria que refactorizar esto...
        nsdHelper.setMServiceName(serviceName);
        nsdHelper.resuleve();
        nsdHelper.stopDiscovery();
        nsdServiceInfo=nsdHelper.getChosenServiceInfo();
        if(nsdServiceInfo!=null) {
            GetSocketServidorTarea tareaGetSocket = new GetSocketServidorTarea();
            tareaGetSocket.execute(nsdServiceInfo);
        }else {
            ClientActivity ca  = (ClientActivity)context;
            ca.notificaFalloServicio();
        }
    }

    /**
     * Obtine la direccion IP y puerto del servidor mediante la tecnologia NSD. Corre el riesgo de entrar
     * en bucle infinito si el nombre de servicio no se encuentra.
     * @param context
     * @param serviceName
     */
    public NsdServiceInfo resuelveNsd(Context context, String serviceName){
        nsdHelper=new NsdHelper(context,serviceName);
        nsdHelper.initializeNsd();
        nsdHelper.discoverServices();
        nsdHelper.resuleve();
        nsdHelper.stopDiscovery();

        //Si en menos de 5 segundos no lo ha encontrado devuelve null
        return nsdHelper.getChosenServiceInfo();
    }

    public void setSocketServidor(Socket socketServidor){
        //Guarda el socket del servidor
        this.socketServidor=socketServidor;

        //Una vez obtenido el socket espera a que el servidor le asigne un identificador
        GetIdentificadorTarea tareaGetId= new GetIdentificadorTarea();
        tareaGetId.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,socketServidor);
    }

    public void getMensaje(){
        ReciveMensajeTarea reciveMensaje= new ReciveMensajeTarea();
        reciveMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,socketServidor);
    }

    public void procesaMensaje(String mensaje){
        //El mensaje recibido es tipo: <<turno>>::<<mensaje>>
        if(mensaje==null || mensaje.split("::")[0].equals("apaga")){
            try {
                socketServidor.close();
                if(contextID==1){
                    ClientActivity c = (ClientActivity)context;
                    c.notificaApaga();
                }
            } catch (IOException e) {
                Log.e(TAG,"Error al cerrar el socket servidor",e);
            }
        }else {
            String [] m = mensaje.split("::");
            String id = Integer.toString(Integer.valueOf(m[0]));
            String datos = m[1];
            if (id.equals(this.id)) {
                //Notifica a la vista del mensaje si ademas es su turno muestra los botones para mandar mensaje
                if (contextID == 0) {
                    ServerActivity sa = (ServerActivity) context;
                    sa.publicaMensaje(datos);
                    sa.muestraBotones();
                } else {
                    ClientActivity ca = (ClientActivity) context;
                    ca.publicaMensaje(datos);
                    ca.muestraBotones();
                }
            } else {
                //Si no solo muestra el mensaje y espera la recepcion de nuevos
                if (contextID == 0) {
                    ServerActivity sa = (ServerActivity) context;
                    sa.publicaMensaje(datos);
                } else {
                    ClientActivity ca = (ClientActivity) context;
                    ca.publicaMensaje(datos);
                }
                getMensaje();
            }
        }
    }

    public void enviaMensaje(String datos){

        Mensaje mensaje = new Mensaje(socketServidor,datos);
        EnviaMensajeTarea enviaMensaje = new EnviaMensajeTarea();
        enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);

        //Espera la recepcion del mensaje nuevo
        getMensaje();
    }

    public void setIdentificador(String id){
        //Una vez que ya tiene el identificador esta listo para usarse
        this.id=id;

        //Le dice a la vista que ya tiene al servidor
        if(contextID==1) {
            ClientActivity ca = (ClientActivity) context;
            ca.notificaServidorEncontrado();
        }
        //Y se queda esperando una respuesta del servidor
        getMensaje();
    }
    public void updateContext(Context context){
        this.context=context;
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
     * Tarea que obtiene el identificador de cliente que le ha asignado el servidor
     */
    public class GetIdentificadorTarea extends AsyncTask<Socket, Void, String> {

        String TAG = "GetIdentificadorTarea";

        @Override
        protected String doInBackground(Socket... params) {
            Socket socket = params[0];
            try {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String datos = lector.readLine();
                Log.i(TAG, "Añadido identificador "+datos);
                return datos;
            } catch (IOException e) {
                Log.e(TAG, "ERROR: Al recibir el mensaje", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String datos){
            setIdentificador(datos);
        }
    }

    /**
     * Tarea que se encarga de coger los mensajes que llegan desde el servidor
     */
    public class ReciveMensajeTarea extends AsyncTask<Socket,Void,String>{
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
            procesaMensaje(datos);
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
            String datos = params[0].getMensaje();
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
}
