package com.horrorsoft.abctimer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Alexey on 14.02.2016.
 * !!!
 */
public class GoogleMapsActivity extends FragmentActivity {
    private GoogleMap map = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapView)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                PolylineOptions line = new PolylineOptions();
                line.add(new LatLng(59.146593, 37.887522));
                line.add(new LatLng(59.125421, 37.931468));
                line.add(new LatLng(59.11121, 37.960135));
                line.add(new LatLng(59.107682, 38.083119));
                line.color(Color.BLUE);
                line.width(3);
                line.geodesic(true);
                map.addPolyline(line);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.125421, 37.931468), 14));
            }
        });
    }
}
