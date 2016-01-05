package com.idn.ducibus.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class NfcReader extends Observable implements Observer {
    private static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter nfcAdapter;

    public <T extends Observer> NfcReader(T activity) {
        nfcAdapter = NfcAdapter.getDefaultAdapter((Context) activity);
        this.addObserver(activity);
    }

    public boolean hasNfcSupport() {
        return nfcAdapter != null;
    }

    public boolean hasNfcEnabled() {
        return nfcAdapter.isEnabled();
    }

    public void setupForegroundDispatch(final Activity activity) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public void stopForegroundDispatch(final Activity activity) {
        nfcAdapter.disableForegroundDispatch(activity);
    }

    public void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask(this).execute(tag);

            } else {
                Log.d("Ducibus", "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask(this).execute(tag);
                    break;
                }
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        this.setChanged();
        this.notifyObservers(o);
    }
}
