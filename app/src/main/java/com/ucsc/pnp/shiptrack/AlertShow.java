package com.ucsc.pnp.shiptrack;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class AlertShow extends AppCompatActivity {
    GetAlert getAlert;
    GoogleMap googleMap;
    Marker petpointer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        startService(new Intent(this, NetService.class));

        IntentFilter filter = new IntentFilter("com.ucsc.pnp.ais.CUSTOM");

        getAlert = new GetAlert();
        registerReceiver(getAlert, filter);

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getAlert);
    }


    class GetAlert extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonmsg=intent.getStringExtra("data");
            Toast.makeText(getApplicationContext(),"Alert in", Toast.LENGTH_LONG).show();
            try {
                JSONObject jsonObject=new JSONObject(jsonmsg);
                JSONObject obj = new JSONObject(jsonmsg);
                showAlert(obj.getString("sname"),new LatLng(obj.getLong("lat"),obj.getLong("lon")));

            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
    }
    public void showAlert(final String Petname,final LatLng loc){
        AlertDialog alertDialog = new AlertDialog.Builder(AlertShow.this).create();
        alertDialog.setTitle("Pet Alert");
        alertDialog.setMessage("Your ship is out of range :- ShipName ::-- "+Petname);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showLocation(Petname,loc);
                    }
                });
        alertDialog.show();
    }

    public void showLocation(String petname,LatLng newpsition){


        if(googleMap!=null) {
            petpointer = googleMap.addMarker(new MarkerOptions().position(newpsition).title(petname));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newpsition.latitude, newpsition.longitude), 12.0f));
        }
    }


}
