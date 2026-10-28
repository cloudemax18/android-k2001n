package com.cloudemax18.k2001nelmhelper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final int REQ = 701;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter adapter;
    private TextView status, output;
    private BluetoothSocket socket;
    private Thread reader;
    private final StringBuilder log = new StringBuilder();

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                append("Emparejamiento: " + safeName(d) + " → " + bondLabel(state));
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                append("Estado Bluetooth cambiado.");
            }
        }
    };

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        adapter = BluetoothAdapter.getDefaultAdapter();
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22, 22, 22, 22);
        root.setBackgroundColor(0xFFF4F6F8);

        TextView title = new TextView(this);
        title.setText("K2001N · ELM327");
        title.setTextSize(25);
        title.setTextColor(0xFF172B4D);
        title.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView intro = new TextView(this);
        intro.setText("Asistente de emparejamiento Bluetooth. No modifica el firmware del radio.");
        intro.setTextSize(15);
        intro.setTextColor(0xFF344563);
        intro.setPadding(0, 12, 0, 16);
        root.addView(intro);

        status = new TextView(this);
        status.setTextSize(15);
        status.setTextColor(0xFF172B4D);
        root.addView(status);

        addButton(root, "1 · Revisar permisos y Bluetooth", v -> startSetup());
        addButton(root, "2 · Ver dispositivos emparejados", v -> showBonded());
        addButton(root, "3 · Abrir ajustes Bluetooth del radio", v -> openBluetoothSettings());
        addButton(root, "4 · Probar conexión ELM327 (SPP)", v -> chooseAndConnect());
        addButton(root, "Enviar comando AT a ELM327", v -> sendAt());
        addButton(root, "Desconectar", v -> disconnect());

        output = new TextView(this);
        output.setTextSize(13);
        output.setTextColor(0xFF172B4D);
        output.setPadding(0, 14, 0, 14);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(output);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else registerReceiver(receiver, filter);
        updateStatus();
        append("Aplicación iniciada.");
    }

    private void addButton(LinearLayout root, String text, View.OnClickListener click) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setOnClickListener(click);
        root.addView(b, new LinearLayout.LayoutParams(-1, -2));
    }

    private boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= 31) {
            return checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        }
        if (Build.VERSION.SDK_INT >= 23)
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    private void startSetup() {
        if (adapter == null) { append("Este dispositivo no expone un adaptador Bluetooth estándar a Android."); return; }
        if (Build.VERSION.SDK_INT >= 31 && !hasBluetoothPermissions()) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, REQ);
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 31 && !hasBluetoothPermissions()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ);
            return;
        }
        try {
            if (!adapter.isEnabled()) {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                append("Solicitado encender Bluetooth.");
            } else append("Bluetooth del framework Android aparece encendido.");
            updateStatus();
        } catch (SecurityException e) { append("Permiso Bluetooth denegado: " + e.getMessage()); }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQ) {
            boolean ok = true;
            for (int r : results) if (r != PackageManager.PERMISSION_GRANTED) ok = false;
            append(ok ? "Permisos concedidos." : "Faltan permisos. Revisa Ajustes de aplicaciones.");
            updateStatus();
        }
    }

    private void updateStatus() {
        if (adapter == null) status.setText("Bluetooth estándar: no disponible");
        else {
            try { status.setText("Bluetooth estándar: " + (adapter.isEnabled() ? "encendido" : "apagado")); }
            catch (SecurityException e) { status.setText("Bluetooth: falta conceder permisos"); }
        }
    }

    private void showBonded() {
        if (!ensureReady()) return;
        try {
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            if (devices.isEmpty()) { append("Android no muestra dispositivos emparejados. El radio puede usar una pila Bluetooth propietaria."); return; }
            for (BluetoothDevice d : devices) append(safeName(d) + " · " + safeAddress(d) + " · " + bondLabel(d.getBondState()));
        } catch (SecurityException e) { append("Sin permiso para leer emparejamientos."); }
    }

    private void openBluetoothSettings() {
        try {
            startActivity(new Intent("android.settings.BLUETOOTH_SETTINGS"));
        } catch (Exception e) {
            try { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); }
            catch (Exception ex) { append("No se pudieron abrir los ajustes: " + ex.getMessage()); }
        }
    }

    private void chooseAndConnect() {
        if (!ensureReady()) return;
        try {
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            if (devices.isEmpty()) {
                append("Primero empareja el OBDII desde los ajustes del radio; luego vuelve aquí. PIN habitual de adaptadores simples: 0000 o 1234, según fabricante.");
                openBluetoothSettings();
                return;
            }
            final BluetoothDevice[] list = devices.toArray(new BluetoothDevice[0]);
            String[] labels = new String[list.length];
            for (int i = 0; i < list.length; i++) labels[i] = safeName(list[i]) + " (" + safeAddress(list[i]) + ")";
            new AlertDialog.Builder(this).setTitle("Selecciona el adaptador OBD")
                .setItems(labels, (dialog, which) -> connect(list[which])).setNegativeButton("Cancelar", null).show();
        } catch (SecurityException e) { append("Permiso para leer dispositivos denegado."); }
    }

    private void connect(BluetoothDevice device) {
        disconnect();
        new Thread(() -> {
            try {
                append("Conectando por Bluetooth SPP a " + safeName(device) + "…");
                BluetoothSocket s = device.createRfcommSocketToServiceRecord(SPP_UUID);
                adapter.cancelDiscovery();
                s.connect();
                socket = s;
                append("SPP conectado. Enviando ATZ…");
                OutputStream os = s.getOutputStream();
                os.write("ATZ\r".getBytes("US-ASCII"));
                os.flush();
                startReader(s);
            } catch (Exception e) {
                append("Falló SPP: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                append("Si el radio desconecta al emparejar, una APK normal no puede sustituir el servicio Bluetooth del firmware.");
                try { if (socket != null) socket.close(); } catch (Exception ignored) {}
                socket = null;
            }
        }).start();
    }

    private void startReader(BluetoothSocket s) {
        reader = new Thread(() -> {
            try {
                InputStream in = s.getInputStream();
                byte[] buffer = new byte[256];
                while (!Thread.currentThread().isInterrupted()) {
                    int n = in.read(buffer);
                    if (n < 0) break;
                    String data = new String(buffer, 0, n, "US-ASCII");
                    append("ELM327: " + data.replace("\r", " ⏎ ").replace("\n", ""));
                }
            } catch (IOException e) { append("Lectura finalizada: " + e.getMessage()); }
        });
        reader.start();
    }

    private void sendAt() {
        BluetoothSocket s = socket;
        if (s == null || !s.isConnected()) { append("No hay conexión SPP activa."); return; }
        new Thread(() -> {
            try {
                s.getOutputStream().write("ATI\r".getBytes("US-ASCII"));
                s.getOutputStream().flush();
                append("Enviado ATI al ELM327.");
            } catch (Exception e) { append("Error enviando AT: " + e.getMessage()); }
        }).start();
    }

    private boolean ensureReady() {
        if (adapter == null) { append("Adaptador Bluetooth estándar no disponible."); return false; }
        if (!hasBluetoothPermissions()) { startSetup(); return false; }
        try {
            if (!adapter.isEnabled()) { append("Activa Bluetooth primero."); startSetup(); return false; }
        } catch (SecurityException e) { append("Falta permiso Bluetooth."); return false; }
        return true;
    }

    private void disconnect() {
        try { if (reader != null) reader.interrupt(); } catch (Exception ignored) {}
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        socket = null;
    }

    private String safeName(BluetoothDevice d) {
        try { String n = d == null ? null : d.getName(); return n == null ? "Dispositivo" : n; }
        catch (Exception e) { return "Dispositivo"; }
    }
    private String safeAddress(BluetoothDevice d) {
        try { return d == null ? "MAC desconocida" : d.getAddress(); } catch (Exception e) { return "MAC desconocida"; }
    }
    private String bondLabel(int state) {
        if (state == BluetoothDevice.BOND_BONDED) return "emparejado";
        if (state == BluetoothDevice.BOND_BONDING) return "emparejando";
        if (state == BluetoothDevice.BOND_NONE) return "no emparejado";
        return "estado " + state;
    }
    private void append(String message) {
        runOnUiThread(() -> {
            log.append("• ").append(message).append("\n");
            if (log.length() > 12000) log.delete(0, log.length() - 10000);
            if (output != null) output.setText(log.toString());
        });
    }

    @Override protected void onDestroy() {
        disconnect();
        try { unregisterReceiver(receiver); } catch (Exception ignored) {}
        super.onDestroy();
    }
}
