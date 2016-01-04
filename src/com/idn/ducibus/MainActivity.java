package com.idn.ducibus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.idn.ducibus.artefact.ArtefactActivity;
import com.idn.ducibus.artefact.ArtefactDescriptor;
import com.idn.ducibus.artefact.ArtefactLoader;
import com.idn.ducibus.menu.MenuActivity;
import com.idn.ducibus.nfc.NfcReader;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements Observer {
    private NfcReader nfcReader;
    private ArtefactLoader artefactLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);
        setTitle("Ducibus");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.museums, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        nfcReader = new NfcReader(this);
        artefactLoader = new ArtefactLoader(this);

        if (!nfcReader.hasNfcSupport()) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        }
        if (!nfcReader.hasNfcEnabled()) {
            Toast.makeText(this, "\"NFC is disabled.", Toast.LENGTH_LONG).show();
        }

        nfcReader.handleIntent(getIntent());
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
            list = MainActivity.this.getAssets().list("data/Schlossmuseum/1");
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
        Intent done = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(done);
    }
}
