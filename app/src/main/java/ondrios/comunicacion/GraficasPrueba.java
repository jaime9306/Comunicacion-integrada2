package ondrios.comunicacion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.*;




import java.text.DecimalFormat;
import java.util.Arrays;

public class GraficasPrueba extends AppCompatActivity {

    private XYPlot mySimpleXYPlot;
    private PieChart myPie;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficas);

        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot2);
        Number[] series1Numbers = {65, 70, 50, 65, 62,60};
        crearGraficaXY(series1Numbers);

        myPie = (PieChart) findViewById(R.id.PieChart2);
        int ganados=10;
        int perdidos=25;
        crearGraficaPIE(ganados, perdidos);


    }

    private void crearGraficaPIE(int ganados, int perdidos) {
        myPie.getBackgroundPaint().setColor(Color.WHITE);

        Segment seg1 = new Segment("Ganados: "+ganados, ganados);
        Segment seg2 = new Segment("Perdidos: "+perdidos, perdidos);

        myPie.getBackgroundPaint().setColor(Color.green(7));
        myPie.addSeries(seg2, new SegmentFormatter(Color.rgb(0, 100, 0), Color.BLACK, Color.BLACK, Color.BLACK));
        myPie.addSeries(seg1, new SegmentFormatter(Color.rgb(150, 190, 150), Color.BLACK,Color.BLACK, Color.BLACK));


        PieRenderer pieRenderer = myPie.getRenderer(PieRenderer.class);
        pieRenderer.setDonutSize((float) 0 / 100,   PieRenderer.DonutMode.PERCENT);
    }

    public void crearGraficaXY(Number[] arrayNumeros){
        // Añadimos Línea Número UNO:
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(arrayNumeros),  // Array de datos
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                " "); // Nombre de la primera serie
        PointLabelFormatter p = new PointLabelFormatter(2);

        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // Color de la línea
                Color.rgb(0, 100, 0),                   // Color del punto
                Color.rgb(150, 190, 150),p);



        mySimpleXYPlot.addSeries(series1, series1Format);

    }


}