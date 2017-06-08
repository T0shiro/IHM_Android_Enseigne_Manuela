package fr.unice.si3.ihm.ihm_enseigne_manuela;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.unice.si3.ihm.ihm_enseigne_manuela.database.ShopDBHelper;
import fr.unice.si3.ihm.ihm_enseigne_manuela.model.Shop;

public class ComparaisonActivity extends AppCompatActivity implements ShopPresentationFragment.OnListFragmentInteractionListener {

    private ShopDBHelper shopDBHelper;
    private List<Shop> shopList;
    private int shopRange1 = 1;
    private int shopRange2 = 1;
    private BarChart chartNbEmployee;
    private BarChart chartRevenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparaison);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragmentStats = new ShopPresentationFragment();
        transaction.add(R.id.fragment1, fragmentStats);
        transaction.commit();

        shopDBHelper = new ShopDBHelper(getBaseContext());
        try {
            shopDBHelper.createDataBase();
            shopDBHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        shopList = shopDBHelper.getShopList();

        chartNbEmployee = (BarChart) findViewById(R.id.chartShop1);
        BarData barData = new BarData(getAbscissValues(1), getDataSet(shopRange1, shopRange2, 1));
        setParametersChart(chartNbEmployee, barData);
        chartNbEmployee.getAxisRight().setEnabled(false);

        chartRevenue = (BarChart) findViewById(R.id.chartShop2);
        BarData barData2 = new BarData(getAbscissValues(2), getDataSet(shopRange1, shopRange2, 2));
        setParametersChart(chartRevenue, barData2);
        chartRevenue.getAxisLeft().setEnabled(false);
        chartNbEmployee.getLegend().setEnabled(false);
        chartRevenue.getLegend().setEnabled(false);
        int[] colors = chartRevenue.getLegend().getColors();
        CardView firstChoice = (CardView) findViewById(R.id.choice1);
        firstChoice.setBackgroundColor(colors[1]);
        CardView secondChoice = (CardView) findViewById(R.id.choice2);
        secondChoice.setBackgroundColor(colors[0]);


        Spinner spinner1 = (Spinner) findViewById(R.id.shop1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_element, shopDBHelper.getShopNameList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        Spinner spinner2 = (Spinner) findViewById(R.id.shop2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshShopData(i, 1, chartNbEmployee, chartRevenue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshShopData(i, 2, chartNbEmployee, chartRevenue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void setParametersChart(BarChart chart, BarData barData) {
        chart.getAxisLeft().setTextSize(30);
        chart.getAxisRight().setTextSize(30);
        chart.getLegend().setTextSize(30);
        chart.getXAxis().setTextSize(30);
        chart.getAxisLeft().setAxisMinValue(0);
        chart.getAxisRight().setAxisMinValue(0);
        chart.setData(barData);
        chart.setDescription("");
        chart.invalidate();
    }

    public void refreshShopData(int shopRange, int fragmentNb, BarChart chartNbEmployee, BarChart chartRevenue) {
        if (fragmentNb == 2) {
            shopRange2 = shopRange;
            initiateStatsShop(shopRange, fragmentNb);
            BarData barData = new BarData(getAbscissValues(1), getDataSet(shopRange1, shopRange, 1));
            barData.setValueTextSize(30);
            chartNbEmployee.setData(barData);
            BarData barData2 = new BarData(getAbscissValues(2), getDataSet(shopRange1, shopRange, 2));
            barData2.setValueTextSize(30);
            chartRevenue.setData(barData2);
        } else {
            shopRange1 = shopRange;
            initiateStatsShop(shopRange, fragmentNb);
            BarData barData = new BarData(getAbscissValues(1), getDataSet(shopRange2, shopRange, 1));
            barData.setValueTextSize(30);
            chartNbEmployee.setData(barData);
            BarData barData2 = new BarData(getAbscissValues(2), getDataSet(shopRange2, shopRange, 2));
            barData2.setValueTextSize(30);
            chartRevenue.setData(barData2);
        }
        chartNbEmployee.animateXY(2000, 2000);
        chartNbEmployee.invalidate();
        chartRevenue.animateXY(2000, 2000);
        chartRevenue.invalidate();
    }

    public void initiateStatsShop(int shopRange, int fragmentNb) {
        ArrayList<String> list = new ArrayList<>();
        Shop shop = shopList.get(shopRange);
        list.add(shop.getCity());
        list.add(shop.getRevenue() + "");
        list.add(shop.getNumberOfEmployees() + "");
        Fragment fragmentShop = ShopPresentationFragment.newInstance(list);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        int idFragment;
        if (fragmentNb == 1) {
            idFragment = R.id.fragment2;
        } else {
            idFragment = R.id.fragment3;
        }
        transaction.replace(idFragment, fragmentShop);
        transaction.commit();
    }

    private ArrayList<IBarDataSet> getDataSet(int shopRange1, int shopRange2, int n) {
        ArrayList<IBarDataSet> dataSets;
        Shop currentShop = shopList.get(shopRange1);
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        if (n == 1) {
            BarEntry v1e1 = new BarEntry(currentShop.getNumberOfEmployees(), 0);
            valueSet1.add(v1e1);
        } else {
            BarEntry v1e2 = new BarEntry(currentShop.getRevenue(), 0);
            valueSet1.add(v1e2);
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, currentShop.getCity());
        barDataSet1.setColor(Color.rgb(0, 155, 0));

        currentShop = shopList.get(shopRange2);
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        if (n == 1) {
            BarEntry v2e1 = new BarEntry(currentShop.getNumberOfEmployees(), 0);
            valueSet2.add(v2e1);
        } else {
            BarEntry v2e2 = new BarEntry(currentShop.getRevenue(), 0);
            valueSet2.add(v2e2);
        }

        BarDataSet barDataSet2 = new BarDataSet(valueSet2, currentShop.getCity());
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getAbscissValues(int n) {
        ArrayList<String> xAxis = new ArrayList<>();
        if (n == 1) {
            xAxis.add("Nombre d'employés");
        } else {
            xAxis.add("Chiffre d'affaires");
        }
        return xAxis;
    }

    @Override
    public void onListFragmentInteraction(String item) {

    }
}
