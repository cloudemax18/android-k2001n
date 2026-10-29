package com.cloudemax18.k2001nelmhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int LOCATION_REQUEST = 812;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        LinearLayout root = new LinearLayout(this);
        root.setGravity(Gravity.CENTER);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(0xFF171323);

        Button title = new Button(this);
        title.setText("Compañeros Pokémon");
        title.setTextSize(22);
        title.setAllCaps(false);
        title.setEnabled(false);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        Button gengar = new Button(this);
        gengar.setText("Mostrar Gengar");
        gengar.setTextSize(19);
        gengar.setAllCaps(false);
        gengar.setOnClickListener(v -> startPokemon("gengar"));
        LinearLayout.LayoutParams item = new LinearLayout.LayoutParams(-1, -2);
        item.topMargin = 18;
        root.addView(gengar, item);

        Button blastoise = new Button(this);
        blastoise.setText("Mostrar Blastoise");
        blastoise.setTextSize(19);
        blastoise.setAllCaps(false);
        blastoise.setOnClickListener(v -> startPokemon("blastoise"));
        LinearLayout.LayoutParams item2 = new LinearLayout.LayoutParams(-1, -2);
        item2.topMargin = 10;
        root.addView(blastoise, item2);
        setContentView(root);
    }

    private void startPokemon(String pokemon) {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            try {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivity(i);
                Toast.makeText(this, "Permite mostrar sobre otras aplicaciones y vuelve a pulsar el botón.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "No se pudieron abrir los permisos de superposición.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        try {
            Intent i = new Intent(this, GengarOverlayService.class);
            i.putExtra("pokemon", pokemon);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
            Toast.makeText(this, "Pokémon flotante iniciado. Arrástralo para moverlo; tócalo para cambiar tamaño.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo iniciar Gengar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == LOCATION_REQUEST) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido. Pulsa Mostrar Gengar otra vez.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "El GPS necesita permiso de ubicación para estimar la velocidad y reaccionar al movimiento.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
