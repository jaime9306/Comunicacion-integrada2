package ondrios.comunicacion.Conexion.Servidor;

import android.util.Log;

import ondrios.comunicacion.Conexion.NsdHelper;

/**
 * Clase que se encarga de la parte de registar los servicios y tambien quitarlos.
 *  Usa la tecnologia Network Service Discovery de Android
 */
class NsdRegister {

    private final String TAG = "NsdRegister";

    private NsdHelper nsdHelper;
    private final String servicename;
    private final Server server;

    /**
     * Constructor del registrador.
     * @param server Necesario para obtener el puerto y el contexto del servidor
     * @param servicename Nombre con el que se va a registrar el servicio.
     */
    public NsdRegister(Server server, String servicename){
        this.server      = server;
        this.servicename = servicename;
    }

    /**
     * Metodo que registra al servidor con el nombre de servicio dado en el constructor.
     */
    public void registraServicio(){
        //Registra el servicio en el puerto del socket
        nsdHelper = new NsdHelper(server.getContext(),servicename);
        nsdHelper.initializeRegistrationListener();
        nsdHelper.registerService(server.getPort());
        Log.e(TAG, "Servidor registrado. Nombre del servicio: " + servicename);
    }

    /**
     * Elimina el servidor de los servicios.
     */
    public void quitaServicio(){
        nsdHelper.unregisterService();
    }
}
