package ondrios.comunicacion.Conexion.Servidor;

import android.os.Parcel;
import android.os.Parcelable;

import ondrios.comunicacion.Conexion.Cliente.Client;

public class MyParcelableServer implements Parcelable {
        private int mData;

    private Server servidor;
    private Client cliente;

    private String m;

    public Server getServidor() {
        return servidor;
    }

    public void setServidor(Server servidor) {
        this.servidor = servidor;
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

    public int getmData() {
        return mData;
    }

    public void setmData(int mData) {
        this.mData = mData;
    }





    public MyParcelableServer(){}


        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(mData);
        }

        public static final Creator<MyParcelableServer> CREATOR
                = new Creator<MyParcelableServer>() {
            public MyParcelableServer createFromParcel(Parcel in) {
                return new MyParcelableServer(in);
            }

            public MyParcelableServer[] newArray(int size) {
                return new MyParcelableServer[size];
            }
        };

        private MyParcelableServer(Parcel in) {
            mData = in.readInt();
        }
    }