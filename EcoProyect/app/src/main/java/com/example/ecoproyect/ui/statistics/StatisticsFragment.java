package com.example.ecoproyect.ui.statistics;

import static android.content.Context.MODE_PRIVATE;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.AllConst.AllConst;
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.Statistics;
import com.example.ecoproyect.databinding.FragmentStatisticsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private SharedPreferences sharedPreferences;
    StatisticsModel statisticsModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         statisticsModel =
                new ViewModelProvider(this).get(StatisticsModel.class);

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = getContext().getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);

        //final TextView textView = binding.textDashboard;
        //textView.setText(dashboardViewModel.getText().getValue().toString());
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Statistics statistics = ((MyApplication) getActivity().getApplication()).getStatistics();
        Statistics statisticsMonth = ((MyApplication) getActivity().getApplication()).getStatisticsMonth();

        if (statistics != null ||    statisticsMonth != null){
            if (statisticsMonth != null){
                crearGraficoBarras(binding.barChart, statisticsMonth);
            } else {
                binding.barChart.setVisibility(View.GONE);
                binding.comenzar.setText("No hay viajes este mes.");
            }
            crearGraficoBarras(binding.barChart2, statistics);
            binding.viajesTotales.setText("Total viajes completados: " + statistics.getViajesTotales());
        } else {
            binding.comenzar.setText("Comienza a hacer viajes y te mostraremos tus estadísticas!");
            binding.txt2.setVisibility(View.GONE);
            binding.barChart.setVisibility(View.GONE);
            binding.barChart2.setVisibility(View.GONE);
        }


        return root;
    }

    private void crearGraficoBarras(BarChart barChart, Statistics statistics){
        // Obtén una referencia al BarChart desde el XML
        int viajesAndando = Integer.valueOf(statistics.getViajesAndando());
        int viajesBici = Integer.valueOf(statistics.getViajesBici());
        int viajesBiciElec = Integer.valueOf(statistics.getViajesBiciEléctrica());
        int viajesPatinete = Integer.valueOf(statistics.getViajesPatineteEléctrico());
        int viajesBus = Integer.valueOf(statistics.getViajesBus());

        // Configuración del eje X (Modos de transporte)
        List<String> modosTransporte = new ArrayList<>();
        modosTransporte.add("A pie");
        modosTransporte.add("Bicicleta");
        modosTransporte.add("Bicicleta eléc.");
        modosTransporte.add("Patinete eléc.");
        modosTransporte.add("Bus");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(modosTransporte));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

        // Configuración del eje Y (Cantidad de viajes)
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        // Configuración de los datos y colores de las barras
        List<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(0f, viajesAndando)); // Cantidad de viajes en bicicleta
        entries.add(new BarEntry(1f, viajesBici)); // Cantidad de viajes a pie
        entries.add(new BarEntry(2f, viajesBiciElec)); // Cantidad de viajes en bicicleta eléctrica
        entries.add(new BarEntry(3f, viajesPatinete)); // Cantidad de viajes en autobús
        entries.add(new BarEntry(4f, viajesBus)); // Cantidad de viajes en patinete eléctrico

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(10, 169, 15));
        colors.add(Color.rgb(20, 200, 5));
        colors.add(Color.rgb(35, 102, 8));
        colors.add(Color.rgb(3, 51, 4));
        colors.add(Color.rgb(50, 234, 26));


        BarDataSet dataSet = new BarDataSet(entries, "Cantidad de Viajes");
        dataSet.setColors(colors);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        // Configuración del BarChart
        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }



    private class MyXAxisValueFormatter extends ValueFormatter {

        private final List<String> values;

        MyXAxisValueFormatter(List<String> values) {
            this.values = values;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < values.size()) {
                return values.get(index);
            }
            return "";
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}