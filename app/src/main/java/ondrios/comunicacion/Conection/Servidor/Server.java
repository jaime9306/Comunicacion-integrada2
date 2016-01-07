package ondrios.comunicacion.Conection.Servidor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import ondrios.comunicacion.Conection.Mensaje;
import ondrios.comunicacion.Conection.NsdHelper;
import ondrios.comunicacion.ServerActivity;

/**
 * Created by Mario on 29/12/2015.
 */
public class Server {

    private String TAG = "Servidor";

    private NsdHelper nsdHelper;
    private ServerSocket mServerSocket;
    private int port;

    private int nclientes;
    private Socket socketCliente;
    private ArrayList<Socket> clientes;
    private int turno;

    private boolean apagado = false;

    private Context context;


    public Server(Context context, String serviceName, int nclientes) {

        this.context=context;
        this.nclientes=nclientes;

        //Inicia el servidor con el nombre del servicio y crea el socket al que se conectan los usuarios.
        iniciaServidor(context, serviceName);

        //Crea una lista de clientes que se van a conectar al servidor
        clientes=new ArrayList<>();

        //
        for (int i =0; i<nclientes;i++) {
            RecibeClienteTarea recibe = new RecibeClienteTarea();
            recibe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mServerSocket);
        }

    }

    public int getPort(){
        return port;
    }

    public String getAddress(){
        return mServerSocket.getInetAddress().getHostAddress();
    }

    public InetAddress getInetAddress(){
        return  mServerSocket.getInetAddress();
    }

    public void iniciaServidor(Context context, String serviceName){
        //Crea el socket de comunicacion
        try {
            mServerSocket = new ServerSocket(0);
            port = mServerSocket.getLocalPort();
        }catch (IOException e) {
            Log.e(TAG, "Error al crear el ServerSocket: ", e);
            e.printStackTrace();
        }

        //Registra el servicio en el puerto del socket
        nsdHelper=new NsdHelper(context,serviceName);
        nsdHelper.initializeRegistrationListener();
        nsdHelper.registerService(port);
        Log.e(TAG, "Servidor registrado. Nombre del servicio: " + serviceName);
    }

    public void setCliente(Socket socketCliente) {
        clientes.add(socketCliente);
        Log.e(TAG, "Cliente añadido: " + socketCliente.getInetAddress().getHostAddress());

        //El sevidor manda un identificador al cliente que se ha conectado
        SetClienteIdTarea setIdTarea = new SetClienteIdTarea();
        setIdTarea.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socketCliente);

        if (clientes.size() == nclientes) {
            this.turno = 1;
            ServerActivity sa = (ServerActivity) context;
            sa.notificaClientesCompletados();
            String c = "";
            for (int i = 0; i<clientes.size();i++){
                c=c+clientes.get(i).getInetAddress().getHostAddress()+" ";
            }
            Log.e(TAG,"Clientes: "+c);
        //Inicia las conversaciones
            for (int i=0;i<clientes.size();i++) {
                Mensaje mensaje = new Mensaje(clientes.get(i), "Empieza la conversacion el cliente " + turno+" "+clientes.get(turno).getInetAddress().getHostAddress());
                mensaje.setTurno(Integer.toString(turno));
                EnviaMensajeTarea enviaMensaje = new EnviaMensajeTarea();
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
            }
            RecibeMensajeTarea tareaRecibe = new RecibeMensajeTarea();
            tareaRecibe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,clientes.get(turno));
            if (turno==nclientes){turno=0;}
        }

    }

    public void enviaMensaje(Mensaje mensaje){
        //El mensaje es tipo: <<turno>>::<<mensaje>>

        EnviaMensajeTarea enviaMensaje = new EnviaMensajeTarea();
        enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);

        //Espera la recepcion del mensaje nuevo solo del socket que es su turno
        if(mensaje.getSocket().equals(clientes.get(turno))) {
            RecibeMensajeTarea tareaRecibe = new RecibeMensajeTarea();
            tareaRecibe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,clientes.get(turno));
        }

    }

    public void recibeMensaje(String datos){
        turno++;
        if (turno==nclientes){turno=0;}
        //Hace eco del mensaje a todos los clientes añadiendo ademas quien es el siguiente en hablar
        Mensaje mensaje;
        for (int i=0;i<nclientes;i++){
            mensaje=new Mensaje(clientes.get(i),datos);
            mensaje.setTurno(Integer.toString(turno));
            enviaMensaje(mensaje);
        }
    }

    public void apagar() {
        apagado=true;
        try {
            for (int i=0;i<clientes.size();i++) {
                Mensaje mensaje = new Mensaje(clientes.get(i),"Apaga");
                mensaje.setTurno("apaga");
                EnviaMensajeTarea enviaApagar = new EnviaMensajeTarea();
                enviaApagar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mensaje);
            }

            nsdHelper.unregisterService();
            for (int i=0;i<clientes.size();i++){
               clientes.get(i).close();
            }
            mServerSocket.close();

            Log.e(TAG, "cerrado");
        } catch (IOException ioe) {
            Log.e(TAG, "Error al intentar cerrar el socket del servidor");
        }
    }


    public void updateContext(Context context){
        this.context=context;
    }

    /* ***** TAREAS ****** */

    public class RecibeClienteTarea extends AsyncTask<ServerSocket,Void,Socket> {

        String TAG="RecibeClienteTarea(Servidor)";

        @Override
        protected Socket doInBackground(ServerSocket... params) {
            try {
                ServerSocket mServerSocket = params[0];
                Log.e(TAG,"Esparando conexion");
                Socket socketCliente = mServerSocket.accept();
                Log.e(TAG, "Cliente conectado");
                return socketCliente;
            } catch (IOException e) {
                Log.e(TAG,"ERROR: Error al conectar cliente",e);
            } catch (Exception e){
                Log.e(TAG,"ERROR: Error inesperado",e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Socket socket){
            setCliente(socket);
        }
    }

    public class SetClienteIdTarea extends AsyncTask<Socket,Void,Void> {

        String TAG="SetClienteIdTarea(Servidor)";

        @Override
        protected Void doInBackground(Socket... params) {
            Socket socket = params[0];
            String id = Integer.toString(clientes.indexOf(socket));
            Log.e(TAG, "Identificador del cliente: "+id);
            try {
                PrintWriter escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                escritor.println(id);
                escritor.flush();
                Log.e(TAG, "Identificador enviado");
            } catch (IOException e) {
                Log.e(TAG, "ERROR: Al enviar el mensaje", e);
            }
            return null;
        }
    }

    /**
     * Tarea que se encarga de coger los mensajes que llegan desde los clientes
     */
    public class RecibeMensajeTarea extends AsyncTask<Socket,Void,String>{

        String TAG = "ReciveMensajeTarea(Servidor)";

        @Override
        protected String doInBackground(Socket... params) {
            Socket socket = params[0];
            if (apagado){this.cancel(true);}
            try {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                return lector.readLine();
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
     * Tarea que se encarga de enviar los mensajes a los clientes
     */
    public class EnviaMensajeTarea extends AsyncTask<Mensaje,Void,Void>{

        String TAG = "EnviaMensajeTarea(Servidor)";

        @Override
        protected Void doInBackground(Mensaje... params) {
            Socket socket  = params[0].getSocket();
            String mensaje = params[0].getMensaje();
            String turno   = params[0].getTurno();
            String datos = turno + "::" + mensaje;
            if (apagado){this.cancel(true);}
            try {
                Log.e(TAG, "Enviando mensaje a " + socket.getInetAddress().getHostAddress());
                PrintWriter escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                escritor.println(datos);
                escritor.flush();

            } catch (IOException e) {
                Log.e(TAG, "ERROR: Al recibir el mensaje", e);
            }
            return null;
        }
    }


}
