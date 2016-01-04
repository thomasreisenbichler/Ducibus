package com.idn.ducibus.artefact;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.idn.ducibus.R;
import com.idn.ducibus.nfc.NfcReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

public class ArtefactActivity extends Activity implements Observer {
    private TextView artefactName;
    private ImageView imageView;
    private NfcReader nfcReader;
    private ArtefactLoader artefactLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artefactactivity);
        setTitle("Ducibus");
        nfcReader = new NfcReader(this);
        artefactLoader = new ArtefactLoader(this);

        artefactName = (TextView) findViewById(R.id.artefactName);
        imageView = (ImageView) findViewById(R.id.imageView);

        InputStream imageIS = null;
        try {
            imageIS = this.getAssets().open("data/nfc.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(BitmapFactory.decodeStream(imageIS));

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
            list = ArtefactActivity.this.getAssets().list("data/Schlossmuseum/1");
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
        ArtefactDescriptor artefactDescriptor = (ArtefactDescriptor)obj;
        artefactLoader.loadArtefact(artefactDescriptor);
    }
}
