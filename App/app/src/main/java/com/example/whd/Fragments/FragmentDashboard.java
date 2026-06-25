package com.example.whd.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whd.Connection.ApiManager;
import com.example.whd.Connection.Models.AvgBreakResponse;
import com.example.whd.Connection.Models.ComparisonResponse;
import com.example.whd.Connection.Models.WeeklyReportResponse;
import com.example.whd.Connection.Models.WorkEntryResponse;
import com.example.whd.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDashboard extends Fragment {

    public static final int MARGIN_DASHBOARD = 5;
    private LinearLayout gui_LinearLayout_Dashboard;

    // Synchronisations-Variablen
    private int callsFinished = 0;
    private final int TOTAL_CALLS = 5;
    private String dataEntries = "", dataGoal = "", dataBreak = "";
    private List<WorkEntryResponse> chartData_Range = null;
    private List<WeeklyReportResponse> chartData_WeeklyReport = null;

    public FragmentDashboard() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        this.gui_LinearLayout_Dashboard = view.findViewById(R.id.gui_LinearLayout_Dashboard);

        startLoadingProcess();
        return view;
    }

    private void startLoadingProcess() {
        // 1. Einträge
        ApiManager.getInstance().getService().getAllEntries().enqueue(new Callback<List<WorkEntryResponse>>() {
            @Override public void onResponse(Call<List<WorkEntryResponse>> call, Response<List<WorkEntryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) dataEntries = "" + response.body().size();
                checkAllFinished();
            }
            @Override public void onFailure(Call<List<WorkEntryResponse>> call, Throwable t) { checkAllFinished(); }
        });

        // 2. Wochenziel
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        ApiManager.getInstance().getService().getComparison(sdf.format(new Date())).enqueue(new Callback<ComparisonResponse>() {
            @Override public void onResponse(Call<ComparisonResponse> call, Response<ComparisonResponse> response) {
                if (response.isSuccessful() && response.body() != null) dataGoal = response.body().getWorked() + "h / " + response.body().getTarget() + "h";
                checkAllFinished();
            }
            @Override public void onFailure(Call<ComparisonResponse> call, Throwable t) { checkAllFinished(); }
        });

        // 3. Pause
        ApiManager.getInstance().getService().getAvgBreak().enqueue(new Callback<AvgBreakResponse>() {
            @Override public void onResponse(Call<AvgBreakResponse> call, Response<AvgBreakResponse> response) {
                if (response.isSuccessful() && response.body() != null) dataBreak = response.body().getAvgBreakMinutes() + "min";
                checkAllFinished();
            }
            @Override public void onFailure(Call<AvgBreakResponse> call, Throwable t) { checkAllFinished(); }
        });

        // 4. Chart Daten
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String endDate = isoFormat.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -21);
        String startDate = isoFormat.format(cal.getTime());

        ApiManager.getInstance().getService().getEntriesByRange(startDate, endDate).enqueue(new Callback<List<WorkEntryResponse>>() {
            @Override public void onResponse(Call<List<WorkEntryResponse>> call, Response<List<WorkEntryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chartData_Range = response.body();
                    checkAllFinished();
                }
            }
            @Override public void onFailure(Call<List<WorkEntryResponse>> call, Throwable t) { checkAllFinished(); }
        });

        // 5. Chart Daten
        SimpleDateFormat isoFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal2 = Calendar.getInstance();
        String endDate2 = isoFormat2.format(cal2.getTime());
        cal2.add(Calendar.DAY_OF_YEAR, -49);
        String startDate2 = isoFormat2.format(cal2.getTime());

        chartData_WeeklyReport = new ArrayList<>();
        ApiManager.getInstance().getService().getWeeklyReport(startDate2, endDate2).enqueue(new Callback<List<WeeklyReportResponse>>() {
            @Override
            public void onResponse(Call<List<WeeklyReportResponse>> call, Response<List<WeeklyReportResponse>> response) {
                if (response.isSuccessful() && response.body() != null) chartData_WeeklyReport = response.body();
                checkAllFinished();
            }

            @Override
            public void onFailure(Call<List<WeeklyReportResponse>> call, Throwable t) {

            }
        });
    }

    private synchronized void checkAllFinished() {
        callsFinished++;
        if (callsFinished == TOTAL_CALLS) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::renderDashboard);
            }
        }
    }

    private void renderDashboard() {
        if (!isAdded() || gui_LinearLayout_Dashboard == null) return;
        gui_LinearLayout_Dashboard.removeAllViews();

        init_AdditionalTextViewIcon("Anzahl Einträge", dataEntries.isEmpty() ? "-" : dataEntries, R.drawable.icon_dashboard);
        init_AdditionalTextViewIcon("Wochenziel", dataGoal.isEmpty() ? "-" : dataGoal, R.drawable.icon_goal);
        init_AdditionalTextViewIcon("Durchschnitts-Pausendauer", dataBreak.isEmpty() ? "-" : dataBreak, R.drawable.icon_pause);

        if (chartData_Range != null) {
            setupChart_Range(chartData_Range);
        }

        if (chartData_WeeklyReport != null) {
            setupChart_WeeklyReport(chartData_WeeklyReport);
        }
    }

    private void init_AdditionalTextViewIcon(String description, String value, int rscID) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View iconElement = inflater.inflate(R.layout.element_dashboard_2_value_data, gui_LinearLayout_Dashboard, false);
        ((ImageView) iconElement.findViewById(R.id.gui_ImageView_Dashboard_Icon)).setImageResource(rscID);
        ((TextView) iconElement.findViewById(R.id.gui_TextView_Dashboard_ElementTitle)).setText(description);
        ((TextView) iconElement.findViewById(R.id.gui_TextView_Dashboard_ElementValue)).setText(value);
        gui_LinearLayout_Dashboard.addView(iconElement);
    }

    private void setupChart_WeeklyReport (List<WeeklyReportResponse> entries) {
        if (!isAdded() || getContext() == null) return;

        BarChart chart = new BarChart(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 800);
        params.setMargins(10, 20, 10, 10);

        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            barEntries.add(new BarEntry(i, (float) (entries.get(i).getWorkedHours() - entries.get(i).getTargetHours())));
            labels.add(entries.get(i).getWeek());
        }

        // Dataset Design
        BarDataSet dataSet = new BarDataSet(barEntries, "Wochenziele");
        dataSet.setColor(android.graphics.Color.parseColor("#0000FF"));
        dataSet.setValueTextColor(android.graphics.Color.DKGRAY);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);
        chart.setData(barData);

        // Achsen-Design
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        chart.getAxisRight().setEnabled(false); // Rechte Y-Achse ausblenden

        // Chart allgemeine Einstellungen
        chart.getDescription().setEnabled(false); // Text "Test Chart" entfernen
        chart.setFitBars(true);
        chart.animateY(1000);
        chart.setTouchEnabled(true); // Interaktion erlauben
        chart.setPinchZoom(true);
        chart.setDrawValueAboveBar(true); // Werte über den Balken zeichnen
        chart.setExtraTopOffset(20f);     // Platz für die Texte schaffen

        gui_LinearLayout_Dashboard.addView(chart, params);
        chart.invalidate();
    }

    private void setupChart_Range(List<WorkEntryResponse> entries) {
        if (!isAdded() || getContext() == null) return;

        BarChart chart = new BarChart(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 800);
        params.setMargins(10, 20, 10, 10);

        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Format für Eingabe (API) und Ausgabe (X-Achse)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM");

        for (int i = 0; i < entries.size(); i++) {
            try {
                Date start = inputFormat.parse(entries.get(i).getStartTime());
                Date end = inputFormat.parse(entries.get(i).getEndTime());

                float hours = (float) (end.getTime() - start.getTime()) / (3600000f);
                float actualWork = hours - (entries.get(i).getBreakMinutes() / 60f);

                barEntries.add(new BarEntry((float) i, actualWork));
                labels.add(outputFormat.format(start));
            } catch (Exception e) { e.printStackTrace(); }
        }

        // Dataset Design
        BarDataSet dataSet = new BarDataSet(barEntries, "Letzte 21 Arbeitstage");
        dataSet.setColor(android.graphics.Color.parseColor("#0000FF"));
        dataSet.setValueTextColor(android.graphics.Color.DKGRAY);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);
        chart.setData(barData);

        // Achsen-Design
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        chart.getAxisRight().setEnabled(false); // Rechte Y-Achse ausblenden
        chart.getAxisLeft().setAxisMinimum(0f); // Bei 0 beginnen

        // Chart allgemeine Einstellungen
        chart.getDescription().setEnabled(false); // Text "Test Chart" entfernen
        chart.setFitBars(true);
        chart.animateY(1000);
        chart.setTouchEnabled(true); // Interaktion erlauben
        chart.setPinchZoom(true);

        gui_LinearLayout_Dashboard.addView(chart, params);
        chart.invalidate();
    }
}