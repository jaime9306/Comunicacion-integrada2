package ondrios.comunicacion.Conexion.Servidor;

import android.content.Context;
import android.os.AsyncTask;
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

import ondrios.comunicacion.Conexion.Mensaje;
import ondrios.comunicacion.Conexion.NsdHelper;
import ondrios.comunicacion.Juego.MotorJuego;
import ondrios.comunicacion.ServerActivity;

/**
 * Created by Mario on 29/12/2015.
 */
public class Server {

    private String TAG = "Servidor";

    private final MotorJuego motor;

    private NsdRegister register;
    private ServerSocket serverSocket;
    private int port;

    private int nclientes;
    private ArrayList<Socket> clientes;
    private int turno;

    private boolean apagado = false;

    private Context context;

    /**
     * Clase servidor. Se encarga de manejar las conexiones, llevar la lgica del juego y
     * mandar a cada cliente los datos oportuno.
     * @param context Contexto de la actividad donde se crea el servidor.
     * @param serviceName Nombre del servicio con el que se quiere registrar al servidor.
     * @param nclientes Numero de clientes que va a tener el servidor.
     */
    public Server(Context context, String serviceName, int nclientes) {
        //Guarda el contexto y en numero de clientes
        this.context   = context;
        this.nclientes = nclientes;
        //Crea el socket del servidor
        try {
            this.serverSocket = new ServerSocket(0);
            this.port = serverSocket.getLocalPort();
        }catch (IOException e) {
            Log.e(TAG, "Error al crear el ServerSocket: ", e);
            e.printStackTrace();
        }

        //Registra el servicio
        register=new NsdRegister(this,serviceName);
        register.registraServicio();
        Log.i(TAG, "Servcio registrado " + serviceName);

        //Crea una lista de clientes que se van a conectar al servidor
        clientes=new ArrayList<>();

        //Crea el motor que va a llevar el juego
        this.motor = new MotorJuego(this,nclientes,8);

        //Espera tantas peticiones como numero de usuarios se le pasen al constructor.
        for (int i =0; i<nclientes;i++) {
            RecibeClienteTarea recibe = new RecibeClienteTarea();
            recibe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        }
    }

    public void setCliente(Socket socketCliente) {
        clientes.add(socketCliente);
        Log.e(TAG, "Cliente añadido: " + socketCliente.getInetAddress().getHostAddress());

        //El sevidor manda un identificador al cliente que se ha conectado
        Mensaje mensajeIdentificador = new Mensaje(socketCliente,Integer.toString(clientes.indexOf(socketCliente)),"identificador");
        enviaMensaje(mensajeIdentificador);

        //Añade el jugador conectado al juego (El nombre es demomento el identificador)
        motor.añadeJugador(Integer.toString(clientes.indexOf(socketCliente)));

        if (clientes.size() == nclientes) {
            //Elimina el servicio de la red. Ya no es necesario que este al empezar la partida
            register.quitaServicio();

            //El motor inicia el juego
            motor.inicia();

            //Selecciona que cliente es el turno
            this.turno = 0;

            //Selecciona que cliente es el turno
            this.turno = motor.getJugadorMano();

            ServerActivity sa = (ServerActivity) context;
            sa.notificaClientesCompletados();

            String c = "";
            for (int i = 0; i<clientes.size();i++){
                c=c+clientes.get(i).getInetAddress().getHostAddress()+" ";
            }
            Log.e(TAG, "Clientes: " + c);

            //Empieza a tirar el jugador que es mano (Se podria enviar un mensaje a los demas para notificar quien empieza.
            Mensaje mensajeTira = new Mensaje(clientes.get(turno),"null","tira");
            enviaMensaje(mensajeTira);
        }

    }

    public void enviaMensaje(Mensaje mensaje){
        EnviaMensajeTarea enviaMensaje = new EnviaMensajeTarea();
        RecibeMensajeTarea tareaRecibe = new RecibeMensajeTarea();
        switch (mensaje.getProtocolo()){
            case "identificador":
                //Otorga un identificador al cliente
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                break;
            case "mensaje":
                //Mensaje directo al cliene
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                break;
            case "repite":
                //El formato del mensaje de este protocolo es <<turno>>::<<mensaje>>
                String datosEmpieza = turno+"::"+mensaje.getMensaje();
                mensaje.setMensaje(datosEmpieza);
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                //Se queda esperando la respuesta del cliente del que es turno.
                if(mensaje.getSocket().equals(clientes.get(turno))) {
                    tareaRecibe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,clientes.get(turno));
                }
                break;
            case "cartas": //Envia al cliente las cartas que le han toccado
                //Formato del mensaje <<carta1>>:<<carta2>>:<<carta3>>
                String datos = mensaje.getMensaje();
                String [] d = datos.split("::");
                int cliente = Integer.valueOf(d[0]);
                Mensaje mensajeCartas = new Mensaje(clientes.get(cliente),d[1],"cartas");
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensajeCartas);
                break;
            case "pinte": //Envia a todos los clientes el pinte
                for (int i = 0;i<nclientes;i++){
                    mensaje.setSocket(clientes.get(i));
                    enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                }
                break;
            case "tira": //Notifica al cliente que le toca el turno de tirar
                //El cuerpo del mensaje es un string que pone "null"
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
            case "apaga":
                //Envia la señal de apagado al cliente
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                break;
        }
    }

    public void recibeMensaje(String datos){
        turno++;
        if (turno==nclientes){turno=0;}
        //Hace eco del mensaje a todos los clientes añadiendo ademas quien es el siguiente en hablar
        String [] d;
        if(datos!=null){
            d = datos.split("&");
        }else{
            d = new String[1];
            d[0]="OK_apaga";
        }
        switch (d[0]){
            case "mensaje":
                Mensaje mensaje;
                for (int i=0;i<nclientes;i++){
                    mensaje=new Mensaje(clientes.get(i),d[1],"repite");
                    enviaMensaje(mensaje);
                }
                break;
            case "OK_apaga":
                try {
                    for (int i=0;i<clientes.size();i++){
                        clientes.get(i).close();
                    }
                    serverSocket.close();
                    Log.e(TAG, "cerrado");
                } catch (IOException ioe) {
                    Log.e(TAG, "Error al intentar cerrar el socket del servidor");
                }
                break;
        }
    }

    public void apagar() {
        apagado=true;
        for (int i=0;i<clientes.size();i++) {
            Mensaje mensaje = new Mensaje(clientes.get(i),"null","apaga");
            enviaMensaje(mensaje);
        }
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
            String protocolo   = params[0].getProtocolo();
            String datos = protocolo + "&" + mensaje;
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

    /*
        GETTERS Y SETTERS
     */


    public Context getContext(){
        return context;
    }

    public int getPort(){
        return port;
    }

    public String getAddress(){
        return serverSocket.getInetAddress().getHostAddress();
    }
}
