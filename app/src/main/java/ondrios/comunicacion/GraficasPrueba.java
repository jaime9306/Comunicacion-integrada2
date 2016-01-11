package ondrios.comunicacion;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.*;




import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import ondrios.comunicacion.BD.AdminSQLiteOpenHelper;

public class GraficasPrueba extends AppCompatActivity {
    TextView texto;
    private XYPlot mySimpleXYPlot;
    private PieChart myPie;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficas);

        ver();


        myPie = (PieChart) findViewById(R.id.PieChart2);
        int ganados=10;
        int perdidos=25;
        crearGraficaPIE(ganados,"Ganados", perdidos,"Perdidos",myPie);


    }


    private void crearGraficaPIE(int n1,String t1, int n2,String t2, PieChart grafica) {

        Segment seg1 = new Segment(t1+": "+n1, n1);
        Segment seg2 = new Segment(t2+": "+n2, n2);

        grafica.getBackgroundPaint().setColor(Color.TRANSPARENT);
        grafica.addSeries(seg2, new SegmentFormatter(Color.rgb(0, 100, 0), Color.BLACK, Color.BLACK, Color.BLACK));
        grafica.addSeries(seg1, new SegmentFormatter(Color.rgb(150, 190, 150), Color.BLACK,Color.BLACK, Color.BLACK));


        PieRenderer pieRenderer = grafica.getRenderer(PieRenderer.class);
        pieRenderer.setDonutSize((float) 0 / 100, PieRenderer.DonutMode.PERCENT);
    }

    public void crearGraficaXY(Number[] arrayNumeros,XYPlot grafica){
        // Añadimos Línea Número UNO:
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(arrayNumeros),  // Array de datos
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Puntuacion"); // Nombre de la primera serie
        PointLabelFormatter p = new PointLabelFormatter(2);

        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // Color de la línea
                Color.rgb(0, 100, 0),                   // Color del punto
                Color.rgb(150, 190, 150),p);

        grafica.getLegendWidget().setVisible(false);
        grafica.addSeries(series1, series1Format);
    }

    @Override
    protected void onResume() {
        // Recogemos las preferencias del sistema.
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        String text = pref.getString("nombreUsuario", "Usuario");
        texto = (TextView)findViewById(R.id.textPrueba);
        texto.setText(text);

        super.onResume();
    }

    public void ver(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "brisca", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor cur= bd.rawQuery("select puntEquipo from partidas", null);
        int total = cur.getCount();
        int media=0;
        ArrayList puntuaciones=new ArrayList();
        while (cur.moveToNext()){
            Log.i("PutaID", Integer.toString(cur.getInt(0)));


            puntuaciones.add(cur.getInt(0));
         media+=cur.getInt(0);
        }
        if(total!=0) {
            media /= total;
        }
        Number[] puntos=new Number[puntuaciones.size()];
        Iterator it = puntuaciones.iterator();
        int i=0;
        while(it.hasNext()){
            puntos[i]=(Number)it.next();
            Log.i("Puta",puntos[i].toString());
            i++;
        }

        bd.close();
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot2);
        crearGraficaXY(puntos, mySimpleXYPlot);

    }
}