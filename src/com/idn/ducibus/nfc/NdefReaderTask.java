package com.idn.ducibus.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import com.idn.ducibus.artefact.ArtefactDescriptor;

import java.io.*;
import java.util.Arrays;
import java.util.Observable;

public class NdefReaderTask extends AsyncTask<Tag, Void, String> {
    final private NfcReader nfcReader;

    NdefReaderTask(NfcReader nfcReader) {
        this.nfcReader = nfcReader;
    }

    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e("Ducibus", "Unsupported Encoding", e);
                }
            }
        }
        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            if (result.startsWith("DCB_")) {
                result = result.substring(4);
                String museumId = result.split("_")[0];
                String artefactId = result.split("_")[1];

                ArtefactDescriptor artefactDescriptor = new ArtefactDescriptor(museumId, artefactId);
                nfcReader.update(null,artefactDescriptor);
            }
        }
    }
}