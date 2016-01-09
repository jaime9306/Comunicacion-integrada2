package ondrios.comunicacion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.*;




import java.text.DecimalFormat;
import java.util.Arrays;

public class GraficasPrueba extends AppCompatActivity {

    private XYPlot mySimpleXYPlot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficas);


        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot2);

        Number[] series1Numbers = {1,65,2, 70,3, 50,4, 65,5, 62,6,60};

        // Añadimos Línea Número UNO:
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),  // Array de datos
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
                " "); // Nombre de la primera serie
        PointLabelFormatter p = new PointLabelFormatter(2);


        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // Color de la línea
                Color.rgb(0, 100, 0),                   // Color del punto
                Color.rgb(150, 190, 150),p);

        mySimpleXYPlot.addSeries(series1, series1Format);


    }
}