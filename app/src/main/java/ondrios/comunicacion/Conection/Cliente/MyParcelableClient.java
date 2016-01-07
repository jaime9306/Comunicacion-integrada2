package ondrios.comunicacion.Conection.Cliente;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MyParcelableClient implements Parcelable {
        private int mData;



    private Client cliente;
    private String m;
    private ArrayList<String> lista;
    private String servicioSeleccionado;
    private int primeravez;

    public int getmData() {
        return mData;
    }

    public void setmData(int mData) {
        this.mData = mData;
    }



    public Client getCliente() {
        return cliente;
    }

    public void setCliente(Client cliente) {
        this.cliente = cliente;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public ArrayList<String> getLista() {
        return lista;
    }

    public void setLista(ArrayList<String> lista) {
        this.lista = lista;
    }

    public String getServicioSeleccionado() {
        return servicioSeleccionado;
    }

    public void setServicioSeleccionado(String servicioSeleccionado) {
        this.servicioSeleccionado = servicioSeleccionado;
    }

    public int getPrimeravez() {
        return primeravez;
    }

    public void setPrimeravez(int primeravez) {
        this.primeravez = primeravez;
    }




    public MyParcelableClient(){}


        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(mData);
        }

        public static final Creator<MyParcelableClient> CREATOR
                = new Creator<MyParcelableClient>() {
            public MyParcelableClient createFromParcel(Parcel in) {
                return new MyParcelableClient(in);
            }

            public MyParcelableClient[] newArray(int size) {
                return new MyParcelableClient[size];
            }
        };

        private MyParcelableClient(Parcel in) {
            mData = in.readInt();
        }
    }