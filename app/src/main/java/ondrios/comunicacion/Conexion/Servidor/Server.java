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
import ondrios.comunicacion.Juego.Equipo;
import ondrios.comunicacion.Juego.Jugador;
import ondrios.comunicacion.Juego.MotorJuego;
import ondrios.comunicacion.Juego.Partida;
import ondrios.comunicacion.ServerActivity;

import static java.lang.Thread.sleep;

/**
 * Created by Mario on 29/12/2015.
 */
public class Server {

    private String TAG = "Servidor";

    private MotorJuego motor;
    private Partida partida;
    private Equipo equipo0;
    private Equipo equipo1;

    private NsdRegister register;
    private ServerSocket serverSocket;
    private int port;

    private int nclientes;
    private ArrayList<Socket> clientes;
    private int turno;

    private boolean apagado = false;

    private Context context;

    /**
     * Clase servidor. Se encarga de manejar las conexiones, llevar la logica del juego y
     * mandar a cada cliente los datos oportunos.
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

        if (clientes.size() == nclientes) {
            //Elimina el servicio de la red. Ya no es necesario que este al empezar la partida
            register.quitaServicio();

            //Crea los equipos dependiendo de si son de 2 o 4 jugadores (identificadores provisionales -> Socket.toString())
            if(clientes.size() == 2){
                //Crea Jugadores
                //Jugador jugador0 = new Jugador(clientes.get(0).toString());
                //Jugador jugador1 = new Jugador(clientes.get(1).toString());
                Jugador jugador0= new Jugador("0");
                Jugador jugador1= new Jugador("1");

                //Crea Equipos
                equipo0 = new Equipo(jugador0);
                equipo1 = new Equipo(jugador1);
            } else{
                if(clientes.size() == 4){
                    //Crea Jugadores
                    Jugador jugador0 = new Jugador(clientes.get(0).toString());
                    Jugador jugador1 = new Jugador(clientes.get(1).toString());
                    Jugador jugador2 = new Jugador(clientes.get(2).toString());
                    Jugador jugador3 = new Jugador(clientes.get(3).toString());

                    //Crea Equpos
                    equipo0 = new Equipo(jugador0, jugador2);
                    equipo1 = new Equipo(jugador1, jugador3);
                }
            }

            //Crea la partida a 8 juegos
            partida = new Partida(equipo0, equipo1, 8);

            motor = new MotorJuego(this, partida);

            //El motor inicia el juego
            motor.inicia();
            turno=motor.getTurno();
            //ESTO NO ENTIENDO MUY BIEN QUE HACE A SI QUE NO LO TOCO
            ServerActivity sa = (ServerActivity) context;
            sa.notificaClientesCompletados();

            String c = "";
            for (int i = 0; i<clientes.size();i++){
                c=c+clientes.get(i).getInetAddress().getHostAddress()+" ";
            }
            Log.i(TAG, "Clientes: " + c);

            //Empieza a tirar el jugador que es mano (Se podria enviar un mensaje a los demas para notificar quien empieza.
            Mensaje mensajeTira = new Mensaje(clientes.get(turno),Integer.toString(turno),"tira");
            enviaMensaje(mensajeTira);
        }

    }

    /**
     * Envia los mensajes a su corespondiente destinatario del mensaje y actua en funcion del protocolo
     * del mensaje.
     * @param mensaje Mensaje provisto de socket, cuerpo de mensaje y protocolo.
     */
    public void enviaMensaje(Mensaje mensaje){

        switch (mensaje.getProtocolo()){
            case "identificador":
                //duerme();
                //Otorga un identificador al cliente
                EnviaMensajeTarea enviaIdentificador = new EnviaMensajeTarea();
                enviaIdentificador.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                break;

            case "mensaje":
                duerme();
                //Mensaje directo al cliene
                EnviaMensajeTarea enviaMensaje = new EnviaMensajeTarea();
                enviaMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                break;

            case "repite":
                duerme();
                //El formato del mensaje de este protocolo es <<turno>>::<<mensaje>>
                String datosEmpieza = turno+"::"+mensaje.getMensaje();
                mensaje.setMensaje(datosEmpieza);
                EnviaMensajeTarea enviaRepite = new EnviaMensajeTarea();
                enviaRepite.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                //Se queda esperando la respuesta del cliente del que es turno.
                if(mensaje.getSocket().equals(clientes.get(turno))) {
                    RecibeMensajeTarea tareaRecibe = new RecibeMensajeTarea();
                    tareaRecibe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,clientes.get(turno));
                }
                break;

            case "cartas": //Envia al cliente las cartas y pinteque le han toccado
                duerme();
                //Formato del mensaje <<carta1>>:<<carta2>>:<<carta3>>::<<pinte>>
                String datos = mensaje.getMensaje();
                String [] d = datos.split("::");
                int cliente = Integer.valueOf(d[0]);
                Mensaje mensajeCartas = new Mensaje(clientes.get(cliente),d[1]+"::"+d[2],"cartas");

                EnviaMensajeTarea enviaCartas = new EnviaMensajeTarea();
                enviaCartas.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensajeCartas);
                break;

            case "muestra_carta":
                duerme();
                //Envia la carta a todos los usuarios menos al del turno para que la  muestren <<jugador>>::<<carta>>
                String [] dmc = mensaje.getMensaje().split("::");
                int jugador = Integer.valueOf(dmc[0]);
                for (int i = 0; i<clientes.size();i++){
                    if (jugador!=i){
                        Mensaje mensajeMuestraCarta = new Mensaje(clientes.get(i),mensaje.getMensaje(),mensaje.getProtocolo());
                        EnviaMensajeTarea enviaMuestraCarta = new EnviaMensajeTarea();
                        enviaMuestraCarta.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensajeMuestraCarta);
                    }
                }
                break;

            case "tira": //Notifica al cliente que le toca el turno de tirar
                duerme();
                //El cuerpo del mensaje es un string que pone "null"
                for (int i=0;i<nclientes;i++){
                    Mensaje mensajeTira = new Mensaje(clientes.get(i),Integer.toString(turno),"tira");
                    EnviaMensajeTarea enviaTiraInicial = new EnviaMensajeTarea();
                    enviaTiraInicial.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensajeTira);
                }
                RecibeMensajeTarea tareaRecibeTiraIncial = new RecibeMensajeTarea();
                tareaRecibeTiraIncial.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje.getSocket());
                break;

            case "ganador_baza":
                duerme();
                for (int i = 0; i<clientes.size();i++){
                    Mensaje mensajeMuestraGanador = new Mensaje(clientes.get(i),mensaje.getMensaje(),mensaje.getProtocolo());
                    EnviaMensajeTarea enviaMuestraGanador = new EnviaMensajeTarea();
                    enviaMuestraGanador.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensajeMuestraGanador);
                }
                duerme(500);
                motor.roba();
                break;

            case "roba":
                duerme(300);
                //Otorga un identificador al cliente
                EnviaMensajeTarea enviaRoba = new EnviaMensajeTarea();
                enviaRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mensaje);

                if (clientes.indexOf(mensaje.getSocket())==turno){
                    RecibeMensajeTarea tareaRecibeRoba = new RecibeMensajeTarea();
                    tareaRecibeRoba.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,clientes.get(getTurno()));
                }
                break;

            case "roba_pinte":
                //Otorga un identificador al cliente
                EnviaMensajeTarea enviaRobaPinte = new EnviaMensajeTarea();
                enviaRobaPinte.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mensaje);

                if (clientes.indexOf(mensaje.getSocket())==turno){
                    RecibeMensajeTarea tareaRecibeRobaPinte = new RecibeMensajeTarea();
                    tareaRecibeRobaPinte.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,clientes.get(getTurno()));
                }
                break;

            case "apaga":
                //Envia la señal de apagado al cliente
                EnviaMensajeTarea enviaApaga = new EnviaMensajeTarea();
                enviaApaga.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mensaje);
                break;
        }
    }

    public void recibeMensaje(String datos){
        turno++;
        if (turno==nclientes){turno=0;}

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

            case "tira_carta":
                int jugador = turno-1;
                if(jugador==-1){
                    jugador=nclientes-1;
                }
                motor.tiraCarta(jugador,Integer.valueOf(d[1]));
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
                Log.e(TAG,"Esperando mensaje de "+socket.getInetAddress().getHostAddress());
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
                Log.e(TAG, "Enviando mensaje a " + socket.getInetAddress().getHostAddress()+" "+datos);
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

    public ArrayList<Socket> getClientes() {
        return clientes;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public int getTurno() {
        return turno;
    }

    private void duerme(){
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void duerme(int tiempo){
        try {
            sleep(tiempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
