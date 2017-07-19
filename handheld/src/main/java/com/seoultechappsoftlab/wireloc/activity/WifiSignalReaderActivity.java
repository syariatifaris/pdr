package com.seoultechappsoftlab.wireloc.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seoultechappsoftlab.wireloc.entities.RSSIRecord;
import com.seoultechappsoftlab.wireloc.helpers.JsonExportHelper;
import com.seoultechappsoftlab.wireloc.utilities.BluetoothUtils;
import com.wisewells.iamzone.blelibrary.Beacon;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farissyariati on 7/13/15.
 */
public class WifiSignalReaderActivity extends Activity implements View.OnClickListener {
    private TextView textViewAccessPointName;
    private TextView textViewRSSIStrength;
    private TextView textViewBeaconInformation;
    private TextView textViewRecordCounter;
    private TextView textViewStatisticsInformation;

    private final Handler rssiHandler = new Handler();
    private final Handler recordHandler = new Handler();

    private static final String BEACON_MAC_ADDRESS = "C8:5F:A2:7A:8B:31";
    private static final int MAXIMUM_TOTAL_RECORDS = 200;

    //Region BLE
    private BluetoothUtils bluetoothUtils;

    private int currentBeaconRssi;
    private int currentWifiRssi;
    private List<RSSIRecord> rssiRecords = new ArrayList<RSSIRecord>();

    private boolean isRecording = false;
    private int sequence = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_wifi_signal_reader);
        this.textViewAccessPointName = (TextView) this.findViewById(R.id.tvWifiAPName);
        this.textViewRSSIStrength = (TextView) this.findViewById(R.id.tvRSSIStrength);
        this.textViewBeaconInformation = (TextView) this.findViewById(R.id.tvBeaconRSSIStrength);
        this.textViewRecordCounter = (TextView) this.findViewById(R.id.textViewSavedCounter);
        this.textViewStatisticsInformation = (TextView) this.findViewById(R.id.textViewStatisticsInformation);

        this.setCounterTextView();

        Button buttonStartRecording = (Button) this.findViewById(R.id.buttonStartRecordRssiValues);
        Button buttonStopRecording = (Button) this.findViewById(R.id.buttonStopRecordRssiValues);

        buttonStartRecording.setOnClickListener(this);
        buttonStopRecording.setOnClickListener(this);

        this.registerBroadcastListener();

        this.bluetoothUtils = new BluetoothUtils(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.actionActivateBluetoothReceiverIsNotActive();
        this.rssiHandler.post(this.listenNearbyBeacons);
        this.recordHandler.post(this.rssiRecordThread);
    }

    /**
     * On Pause On Application Pause Stop the Update RSSI
     */
    @Override
    protected void onPause() {
        super.onPause();
        this.actionActivateBluetoothReceiverIsNotActive();
        this.rssiHandler.removeCallbacks(this.listenNearbyBeacons);
        this.recordHandler.removeCallbacks(this.rssiRecordThread);
    }

    //Region Broadcast Receiver

    private BroadcastReceiver broadcastReceiverWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //int networkType = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_TYPE);

            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                displayWifiState();
            }
        }
    };

    private BroadcastReceiver broadcastReceiverRssi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double newRSSI = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
            currentWifiRssi = (int) newRSSI;
            setRSSIText(newRSSI);
        }
    };

    //End Region Broadcast Receiver

    private void setRSSIText(double newRSSIValue) {
        String textRSSI = this.getString(R.string.wifi_rssi_strength);
        textRSSI = textRSSI.replace("[rssi]", Double.toString(newRSSIValue));
        this.textViewRSSIStrength.setText(textRSSI);
    }

    private void displayWifiState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String accessPointInfo = this.getString(R.string.wifi_access_point);
        if (networkInfo.isConnected()) {
            accessPointInfo = accessPointInfo.replace("[ap]", wifiInfo.getSSID());
            accessPointInfo = accessPointInfo.replace("[sts]", "connected");
        } else {
            accessPointInfo = accessPointInfo.replace("[ap]", "No AP");
            accessPointInfo = accessPointInfo.replace("[sts]", "not connected");
        }
        this.textViewAccessPointName.setText(accessPointInfo);
    }

    private void registerBroadcastListener() {
        this.registerReceiver(this.broadcastReceiverWifi, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        this.registerReceiver(this.broadcastReceiverRssi, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    public void actionActivateBluetoothReceiverIsNotActive() {
        if (!this.bluetoothUtils.getReceiver().isActive()) {
            this.bluetoothUtils.getReceiver().activate();
        }
    }

    private Runnable listenNearbyBeacons = new Runnable() {
        @Override
        public void run() {
            List<Beacon> nearbyBeacons = bluetoothUtils.getTracker().getAllNearbyBeacons();
            updateBeaconInformation(nearbyBeacons);
            rssiHandler.postDelayed(this, 100);
        }
    };

    private Runnable rssiRecordThread = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                sequence++;
                rssiRecords.add(new RSSIRecord(sequence, currentWifiRssi, currentBeaconRssi));
                setCounterTextView();
                if(rssiRecords.size() == MAXIMUM_TOTAL_RECORDS ){
                    playNotification();
                }
            }
            recordHandler.postDelayed(this, 1000);
        }
    };

    private void playNotification(){
        try{
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone ringtone = RingtoneManager.getRingtone(this, notification);
            ringtone.play();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateBeaconInformation(List<Beacon> nearbyBeacons) {
        for (Beacon beacon : nearbyBeacons) {
            if (beacon.getMacAddress().equals(BEACON_MAC_ADDRESS)) {
                this.currentBeaconRssi = (int) beacon.getRssi();
                String information = this.getString(R.string.beacon_rssi_strength);
                information = information.replace("[bmac]", BEACON_MAC_ADDRESS);
                information = information.replace("[brssi]", Double.toString(beacon.getRssi()));
                this.textViewBeaconInformation.setText(information);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStartRecordRssiValues:
                this.isRecording = true;
                Toast.makeText(this, "Start Recording", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonStopRecordRssiValues:
                if (this.rssiRecords.size() > 0) {
                    JsonExportHelper<RSSIRecord> jsonExportHelper = new JsonExportHelper<RSSIRecord>();
                    if (jsonExportHelper.saveToJson(this.rssiRecords)) {
                        Toast.makeText(this, "File Saved Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed on Saving File", Toast.LENGTH_SHORT).show();
                    }
                }

                this.showDescriptiveStatistics();

                this.isRecording = false;
                this.sequence = 0;
                this.rssiRecords = new ArrayList<RSSIRecord>();
                this.setCounterTextView();

                Toast.makeText(this, "Stop Recording", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setCounterTextView() {
        String information = this.getString(R.string.tv_save_counter);
        information = information.replace("[r]", Integer.toString(this.sequence));
        this.textViewRecordCounter.setText(information);
    }

    private void showDescriptiveStatistics() {
        DescriptiveStatistics wifiStatistics = new DescriptiveStatistics();
        DescriptiveStatistics beaconStatistics = new DescriptiveStatistics();
        for (RSSIRecord record : this.rssiRecords) {
            wifiStatistics.addValue(record.getWifiRssi());
            beaconStatistics.addValue(record.getBeaconRssi());
        }

        double wifiMean = wifiStatistics.getMean();
        double wifiVariance = wifiStatistics.getVariance();
        double wifiStandardDeviation = wifiStatistics.getStandardDeviation();

        double beaconMean = beaconStatistics.getMean();
        double beaconVariance = beaconStatistics.getVariance();
        double beaconStandardDeviation = beaconStatistics.getStandardDeviation();

        String information = "\n\n\nWifi\nMean = " + wifiMean + "\nVariance: " + wifiVariance + "\nSTDEV: "+wifiStandardDeviation+"\n\n"
                + "Beacon\nMean: " + beaconMean + "\nBeacon Variance: " + beaconVariance +"\nSTDEV: "+beaconStandardDeviation+ "\n";

        this.textViewStatisticsInformation.setText(information);
    }
}
