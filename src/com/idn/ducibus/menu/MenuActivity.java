package com.idn.ducibus.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.idn.ducibus.R;
import com.idn.ducibus.artefact.ArtefactActivity;
import com.idn.ducibus.artefact.ArtefactDescriptor;
import com.idn.ducibus.artefact.ArtefactLoader;
import com.idn.ducibus.nfc.NfcReader;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class MenuActivity extends Activity implements Observer {
    private NfcReader nfcReader;
    private ArtefactLoader artefactLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        setTitle("Ducibus");

        nfcReader = new NfcReader(this);
        artefactLoader = new ArtefactLoader(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcReader.setupForegroundDispatch(this);
    }

    @Override
    protected void onPause() {
        nfcReader.stopForegroundDispatch(this);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String[] list = new String[0];
        try {
            list = MenuActivity.this.getAssets().list("data/Schlossmuseum/1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.length > 0) {
            for (String s : list) {
                System.out.println(s);
            }
        }
        nfcReader.handleIntent(intent);
    }

    @Override
    public void update(Observable observable, Object obj) {
        ArtefactDescriptor artefactDescriptor = (ArtefactDescriptor) obj;
        artefactLoader.loadArtefact(artefactDescriptor);
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, ArtefactActivity.class);
        startActivity(intent);
    }
}
