package com.example.root.lawa;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class AnalysticFragment extends Fragment {
    private  BarChart chart;
    Socket mSocket;
    String token_key;
    JSONObject obj;
    RadioGroup radioGroup;
    RadioButton checkByMonth;
    RadioButton checkByCurrent;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        mSocket = SocketIO.getSocket();
        token_key = this.getArguments().getString("token_key");
        mSocket.on("respondHistoryState", processShowChartByRealTime);
        mSocket.on("respondHistoryByMonth", processShowChartByMonth);

        return inflater.inflate(R.layout.fragment_analystic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        obj = new JSONObject();
        try{
            obj.put("token_key", token_key);
        }
        catch (Exception e) {

        }
        setChartToView();
        radioGroup = (RadioGroup) getView().findViewById(R.id.group_radio_analystics);
        checkByMonth = (RadioButton) getView().findViewById(R.id.radiobtn_month);
        checkByCurrent = (RadioButton) getView().findViewById(R.id.radiobtn_current);
        getDataByNow();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkByMonth.isChecked()) {
                    getDataByMonth();
                    Log.e("click", obj.toString());
                }
                if(checkByCurrent.isChecked()) {
                    getDataByNow();
                    Log.e("click", "getDataByNow()");
                }
                Log.e("click", "click done");
            }
        });
        getActivity().setTitle("Analystics");

    }
    public void setChartToView() {
        chart = new BarChart(getActivity().getApplicationContext());
        FrameLayout content = (FrameLayout) getActivity().findViewById(R.id.chart_zone);
        content.addView(chart); //<-- Instead of setContentView(aboutPage)

    }
    public void fillDataToChart(ArrayList<String> labelList, ArrayList<BarEntry> data) {
        BarDataSet dataset = new BarDataSet(data, "Mực nước tính theo m");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData dataInclude = new BarData(labelList, dataset);
        chart.setData(dataInclude);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }
    private Emitter.Listener processShowChartByRealTime = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
                Log.e("Data", args[0].toString());
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ArrayList<String> labels = new ArrayList<String>();
                        ArrayList<BarEntry> entries = new ArrayList<>();

                        try {
                            JSONArray jArray = new JSONArray(args[0].toString());
                            for (int i = 0; i < 6; i++) {
                                JSONObject jObject = jArray.getJSONObject(i);
                                JSONObject jData = jObject.getJSONObject("data");
                                entries.add(new BarEntry(jData.getInt("mucnuoc"), i));
                                labels.add((i + 1)*5 + " phút trước");
                            }
                        } catch (Exception e) {

                        }
                        fillDataToChart(labels, entries);
                        chart.setDescription("Các giá trị");
                    }
                });
            }
        }
    };
    private Emitter.Listener processShowChartByMonth = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ArrayList<String> labels = new ArrayList<String>();
                        if (args[0] != null)
                        {
                            Log.e("Data from chart", args[0].toString());
                            try {
                                JSONArray jArray = new JSONArray(args[0].toString());
                                ArrayList<BarEntry> entries = new ArrayList<>();
                                for (int i = 0; i < jArray.length(); i++) {
                                    if (jArray.optInt(i, 0) != 0) {
                                        entries.add(new BarEntry(jArray.optInt(i), i));
                                        labels.add((i+1) + " tháng trước");
                                    }
                                }

                                fillDataToChart(labels, entries);
                                chart.setDescription("Các giá trị trung bình");

                            } catch (Exception e) {
                                Log.e("Loi", e.getMessage());
                            }

                        }
                    }
                });
            }
        }
    };
    public void getDataByMonth() {
        mSocket.emit("requestHistoryByMonth", obj);
    }
    public void getDataByNow() {
        mSocket.emit("requestHistoryState", obj);
    }
}
