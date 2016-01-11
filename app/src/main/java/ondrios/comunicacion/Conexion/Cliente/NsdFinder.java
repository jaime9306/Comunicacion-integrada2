package ondrios.comunicacion.Conexion.Cliente;

import android.net.nsd.NsdServiceInfo;

import java.util.ArrayList;

import ondrios.comunicacion.Conexion.NsdHelper;

/**
 * Clase que se encarga de buscar los servicios y resolver la dirreccion
 * y puerto del servicio.
 */
public class NsdFinder {

    private String TAG = "NsdFinder";

    private NsdHelper nsdHelper;
    private Client client;

    /**
     * Constructor del buscador.
     * @param client Necesaro para obtener el contexto
     */
    public NsdFinder(Client client){
        this.client = client;
        nsdHelper = new NsdHelper(client.getContext(),"Brisca");
        nsdHelper.initializeNsd();
    }

    /**
     * Busca los servicios que se encuentran disponibles.
     */
    public void iniciaBusqueda(){
        nsdHelper.discoverServices();
    }

    /**
     * Lista de servicios disponibles
     * @return Devuelve una lista de servicios disponibles a los que poder conectarse.
     */
    public ArrayList<String> getServiciosDisponibles(){
        return nsdHelper.getPartidas();
    }

    /**
     * Resuelve la direccion ip y el puerto del servicio, encapsulado en
     * la clase NsdServiceInfo.
     * @param servicename Nombre del servicio que tiene que resolver.
     * @return Direccion ip y puerto encapsulado en la clase NsdServiceInfo.
     */
    public NsdServiceInfo resuelve(String servicename){
        nsdHelper.setMServiceName(servicename);
        nsdHelper.resuleve();
        nsdHelper.stopDiscovery();
        return nsdHelper.getChosenServiceInfo(); //Si en menos de 5 segundos no lo ha encontado devielve null
    }
}
