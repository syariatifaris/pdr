package com.seoultechappsoftlab.wireloc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.seoultechappsoftlab.wireloc.activity.adapter.FingerprintManagerListViewAdapter;
import com.seoultechappsoftlab.wireloc.activity.dialogs.ViewFingerprintDialog;
import com.seoultechappsoftlab.wireloc.controllers.FingerprintManagerController;

/**
 * Fingerprint Manager Activity
 *
 * @author farissyariati
 */
public class FingerprintManagerActivity extends Activity {

    //Region Private Variable

    private FingerprintManagerController controller;
    private FingerprintManagerListViewAdapter adapter;
    private ListView listViewFingerprints;

    //End Region Private Variable

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_manager);

        this.controller = new FingerprintManagerController(this);

        this.listViewFingerprints = (ListView) this.findViewById(R.id.lvFingerprints);
        this.adapter = new FingerprintManagerListViewAdapter(this);

        this.adapter.setData(this.controller.getRegisteredFingeprint());
        this.listViewFingerprints.setAdapter(this.adapter);
        this.setListViewOnClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, R.string.fingerprint_status_enable);
        menu.add(0, 2, 0, R.string.fingerprint_status_disable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                if (this.controller.actionEnableFingerprints(this.adapter.getDataCollection())) {
                    this.clearListView();
                }
                break;
            case 2:
                if (this.controller.actionDisableFingerprints(this.adapter.getDataCollection())) {
                    this.clearListView();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void setListViewOnClickListener() {
        this.listViewFingerprints.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pointX = adapter.getData(position).getPointX();
                int pointY = adapter.getData(position).getPointY();
                ViewFingerprintDialog psd = new ViewFingerprintDialog(parent.getContext(), pointX, pointY);
                psd.setTitle(getString(R.string.dialog_fingerprint_data_title));
                psd.show();
            }
        });
    }

    private void clearListView(){
        this.controller.refreshFingerprintData();
        this.adapter = new FingerprintManagerListViewAdapter(this);
        this.adapter.setData(this.controller.getRegisteredFingeprint());
        this.listViewFingerprints.setAdapter(this.adapter);
    }
}
