package fr.unice.si3.ihm.ihm_enseigne_manuela;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.unice.si3.ihm.ihm_enseigne_manuela.database.ShopDBHelper;
import fr.unice.si3.ihm.ihm_enseigne_manuela.model.Shop;

public class MapShopActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Shop> shopList;
    private ListView drawerListView;
    private DrawerLayout drawerLayout;
    private Geocoder geocoder;
    private ShopArrayAdapter shopArrayAdapter;
    private List<Marker> mMarkerList;
    private ShopDBHelper shopDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_shop);
        mMarkerList = new ArrayList<>();
        geocoder = new Geocoder(getBaseContext());
        shopDBHelper = new ShopDBHelper(getBaseContext());
        try {
            shopDBHelper.createDataBase();
            shopDBHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final List<Shop> fullListShop = shopDBHelper.getShopList();
        shopList = new ArrayList<>(fullListShop);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_list);
        shopArrayAdapter = new ShopArrayAdapter(this, R.layout.activity_map_shop, shopList);
        drawerListView.setAdapter(shopArrayAdapter);
        drawerListView.setOnItemClickListener(new DrawerShopClickListener());
        final Button searchButton = (Button) findViewById(R.id.searchButton);
        final EditText searchText = (EditText) findViewById(R.id.searchText);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch(searchText.getText().toString().toLowerCase());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                searchText.setText("");
            }
        });
        Button clearButton = (Button) findViewById(R.id.cancel);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopList = new ArrayList<>(fullListShop);
                refreshShopDisplay(shopList);
                addShopsOnMap(shopList);
            }
        });
        Button drawerButton = (Button) findViewById(R.id.drawer_button);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(findViewById(R.id.left_drawer));
            }
        });
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    performSearch(searchText.getText().toString().toLowerCase());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    searchText.setText("");
                    return true;
                }
                return false;
            }
        });
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                performSort();
            }
        });
    }

    public void performSearch(String searchContent) {
        List<Shop> newShopList = findShopsWithAddress(searchContent);
        refreshShopDisplay(newShopList);
        drawerLayout.openDrawer(findViewById(R.id.left_drawer));
    }

    public void performSort() {
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        RadioButton checkedRadioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
        int id = checkedRadioButton.getId();
        List<Shop> newShopList = new ArrayList<>(shopList);
        if (id == R.id.revenue) {
            Collections.sort(newShopList, new Comparator<Shop>() {
                @Override
                public int compare(Shop shop, Shop t1) {
                    return Integer.compare(t1.getRevenue(), shop.getRevenue());
                }
            });
            refreshShopDisplay(newShopList);
        } else if (id == R.id.numberOfEmployees) {
            Collections.sort(newShopList, new Comparator<Shop>() {
                @Override
                public int compare(Shop shop, Shop t1) {
                    return Integer.compare(t1.getNumberOfEmployees(), shop.getNumberOfEmployees());
                }
            });
        } else {
            Collections.sort(newShopList, new Comparator<Shop>() {
                @Override
                public int compare(Shop shop, Shop t1) {
                    return shop.getCity().compareTo(t1.getCity());
                }
            });
        }
        refreshShopDisplay(newShopList);
    }

    public void refreshShopDisplay(List<Shop> newShopList) {
        addShopsOnMap(newShopList);
        AsyncTask<Object, Void, List<Shop>> asyncTask = new AsyncTask<Object, Void, List<Shop>>() {
            @Override
            protected List<Shop> doInBackground(Object... shops) {
                List<Shop> listShop = new ArrayList<>();
                for (Object object : shops) {
                    listShop.add((Shop) object);
                }
                return listShop;
            }

            protected void onPostExecute(List<Shop> listShop) {
                shopArrayAdapter.clear();
                shopArrayAdapter.addAll(listShop);
            }
        };
        asyncTask.execute(newShopList.toArray());
        Address address = null;
        try {
            address = geocoder.getFromLocationName("France", 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(address.getLatitude(), address.getLongitude()))
                .zoom(6)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public List<Shop> findShopsWithAddress(String address) {
        List<Shop> shopMatchingList = new ArrayList<>();
        for (Shop shop : shopList) {
            if (shop.getFullAddress().toLowerCase().contains(address)) {
                shopMatchingList.add(shop);
            }
        }
        return shopMatchingList;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Geocoder geocoder = new Geocoder(getBaseContext());
        try {
            Address address = geocoder.getFromLocationName("France", 1).get(0);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(address.getLatitude(), address.getLongitude()))
                    .zoom(6)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getBaseContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getBaseContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getBaseContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        addShopsOnMap(shopList);
    }

    public void addShopsOnMap(List<Shop> shopsToAddToMap) {
        mMap.clear();
        for (Shop shop : shopsToAddToMap) {
            try {
                Address currentShopAddress = geocoder.getFromLocationName(shop.getCity() + " " + shop.getAddress(), 1).get(0);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(currentShopAddress.getLatitude(),
                                currentShopAddress.getLongitude()))
                        .title(shop.getName())
                        .snippet(shop.getAddress()
                                + "\n" + shop.getPhoneNumber()));
                mMarkerList.add(marker);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DrawerShopClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            Marker marker = mMarkerList.get(position);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
            marker.showInfoWindow();
        }
    }
}
