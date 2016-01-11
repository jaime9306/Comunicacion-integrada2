package ondrios.comunicacion;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
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

public class Estadisticas extends AppCompatActivity implements View.OnClickListener{
    private TextView nombreUsuario;
    private TextView puntuacionMedia;
    private TextView numeroPartidasJugadas;
    private TextView duracionMedia;

    private XYPlot historialPuntuaciones;
    private XYPlot historialDuracion;
    private PieChart porcentajeResultados;
    private PieChart porcentajePuntos;

    private Button botonSript;
    private Button botonBorrar;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficas);

        nombreUsuario = (TextView)findViewById(R.id.Usuario);
        puntuacionMedia=(TextView)findViewById(R.id.Media);
        numeroPartidasJugadas=(TextView)findViewById(R.id.NumeroPartidas);
        duracionMedia=(TextView)findViewById(R.id.DuracionMedia);

        historialPuntuaciones = (XYPlot) findViewById(R.id.mySimpleXYPlot2);
        historialDuracion = (XYPlot) findViewById(R.id.mySimpleXYPlot3);
        porcentajeResultados = (PieChart) findViewById(R.id.PieChart2);
        porcentajePuntos = (PieChart) findViewById(R.id.PieChart3);

        botonBorrar = (Button) findViewById(R.id.boton_borrarDatos);
        botonSript = (Button) findViewById(R.id.boton_datosPrueba);

        botonBorrar.setOnClickListener(this);
        botonSript.setOnClickListener(this);

        ver();




    }


    private void crearGraficaPIE(int n1,String t1, int n2,String t2, int n3,String t3, PieChart grafica) {

        Segment seg1 = new Segment(t1+": "+n1, n1);
        Segment seg2 = new Segment(t2+": "+n2, n2);
        Segment seg3 = new Segment(t3+": "+n3, n3);

        grafica.getBackgroundPaint().setColor(Color.TRANSPARENT);
        grafica.addSeries(seg2, new SegmentFormatter(Color.rgb(0, 100, 0), Color.BLACK, Color.BLACK, Color.BLACK));
        grafica.addSeries(seg1, new SegmentFormatter(Color.rgb(150, 190, 150), Color.BLACK, Color.BLACK, Color.BLACK));
        grafica.addSeries(seg3, new SegmentFormatter(Color.rgb(20, 50, 20), Color.BLACK, Color.BLACK, Color.BLACK));

        PieRenderer pieRenderer = grafica.getRenderer(PieRenderer.class);
        pieRenderer.setDonutSize((float) 0 / 100, PieRenderer.DonutMode.PERCENT);
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

    public void crearGraficaXY(Number[] arrayNumeros,Number[] arrayNumerosM,XYPlot grafica){
        // Añadimos Línea Número UNO:
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(arrayNumeros),  // Array de datos
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Puntuacion"); // Nombre de la primera serie



        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(arrayNumerosM),  // Array de datos
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Media"); // Nombre de la primera serie

        PointLabelFormatter p = new PointLabelFormatter(2);

        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // Color de la línea
                Color.rgb(0, 100, 0),                   // Color del punto
                Color.rgb(150, 190, 150),p);

        LineAndPointFormatter series1Formatm = new LineAndPointFormatter(
                Color.rgb(200, 0, 0),                   // Color de la línea
                Color.TRANSPARENT,                   // Color del punto
                Color.TRANSPARENT,p);

        grafica.getLegendWidget().setVisible(false);
        grafica.addSeries(series1, series1Format);
        grafica.addSeries(series2, series1Formatm);

    }



    public void ver(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "brisca", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor cur= bd.rawQuery("select puntEquipo,duracion,puntPropia, puntEquipo from partidas", null);
        int total = cur.getCount();
        int media=0;
        int mediaDuraciones=0;
        ArrayList puntuaciones=new ArrayList();
        ArrayList duraciones=new ArrayList();
        int puntosPropios=0;
        int puntosEquipos=0;
        while (cur.moveToNext()){
            puntuaciones.add(cur.getInt(0));
            duraciones.add(cur.getInt(1));
            puntosPropios+=cur.getInt(2);
            puntosEquipos+=cur.getInt(3);
            media+=cur.getInt(0);
            mediaDuraciones+=cur.getInt(1);
        }

        if(total!=0) {
            media /= total;
            mediaDuraciones /=total;
        }
        puntuacionMedia.setText(puntuacionMedia.getText()+Integer.toString(media));
        duracionMedia.setText(duracionMedia.getText()+Integer.toString(mediaDuraciones));
        numeroPartidasJugadas.setText(numeroPartidasJugadas.getText()+Integer.toString(total));

        Number[] puntos=new Number[puntuaciones.size()];
        Number[] puntosM=new Number[puntuaciones.size()];
        Number[] numberDuraciones=new Number[duraciones.size()];
        Number[] numberDuracionesM=new Number[duraciones.size()];
        Iterator it = puntuaciones.iterator();
        int i=0;
        while(it.hasNext()){
            puntos[i]=(Number)it.next();
            puntosM[i]=media;
            i++;
        }
        int a=0;
        Iterator it2 = duraciones.iterator();
        while(it2.hasNext()){
            numberDuraciones[a]=(Number)it2.next();
            numberDuracionesM[a]=mediaDuraciones;
            a++;
        }



        Cursor cur2 = bd.rawQuery("select puntEquipo from partidas where puntEquipo>60", null);
        int ganados=cur2.getCount();
        Cursor cur3 = bd.rawQuery("select puntEquipo from partidas where puntEquipo=60", null);
        int empatados=cur3.getCount();
        int perdidos=total-ganados-empatados;

        crearGraficaXY(puntos,puntosM, historialPuntuaciones);
        crearGraficaXY(numberDuraciones,numberDuracionesM, historialDuracion);
        crearGraficaPIE(ganados, "Ganados", perdidos, "Perdidos", empatados, "Empatados", porcentajeResultados);

        if(!(puntosEquipos-puntosPropios<0)) {
            crearGraficaPIE(puntosPropios, "Propios", puntosEquipos - puntosPropios, "Ajenos", porcentajePuntos);
        }



        bd.close();
    }

    @Override
    protected void onResume() {
        // Recogemos las preferencias del sistema.
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        String text = pref.getString("nombreUsuario", "Usuario");
        nombreUsuario.setText(nombreUsuario.getText()+text);

        super.onResume();
    }
    public void onClick(View v) {
        /*Para cada boton de la actividad se inicia la actividad correspondiente a ese boton*/
        switch (v.getId()) {
            case R.id.boton_borrarDatos:
                borrarDatos();
                break;

            case R.id.boton_datosPrueba:
                script();
                break;

        }}

    private void borrarDatos() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "brisca", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("DELETE FROM partidas;");
        db.close();
        Intent intentBD = new Intent(Estadisticas.this,Estadisticas.class);
        startActivity(intentBD);
    }
    private void script() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "brisca", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        Long tiempo=System.currentTimeMillis();
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",60,60,60);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",66,66,60);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",70,70,60);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",70,70,160);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",70,70,60);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",75,75,160);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",30,60,60);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",45,75,60);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",70,70,160);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",80,80,160);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",40,40,60);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",45,45,50);");
        db.execSQL("insert into partidas (fecha,puntPropia,puntEquipo,duracion) values("+tiempo+",68,68,60);");
        db.close();

        Intent intentBD = new Intent(Estadisticas.this,Estadisticas.class);
        startActivity(intentBD);
    }


}