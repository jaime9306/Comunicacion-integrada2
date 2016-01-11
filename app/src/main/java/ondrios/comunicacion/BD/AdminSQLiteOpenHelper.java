package ondrios.comunicacion.BD;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Adrián on 07/01/2016.
 * Clase administradora de la base de datos, crea la tabla de la base de datos.
 */
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
    //creacion de la base de datos en este caso una sola tabla.
        db.execSQL("create table partidas(id integer primary key autoincrement , fecha long, puntPropia integer ,puntEquipo integer , duracion integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
