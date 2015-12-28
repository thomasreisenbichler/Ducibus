package com.idn.ducibus;

import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.*;
import java.util.Arrays;

public class NdefReaderTask extends AsyncTask<Tag, Void, String> {
    final private ImageView imageView;
    final private MainActivity mainActivity;

    NdefReaderTask(ImageView imageView, MainActivity mainActivity) {
        this.imageView = imageView;
        this.mainActivity = mainActivity;
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
                String museum = result.split("_")[0];
                String artefact = result.split("_")[1];
                try {
                    InputStream imageIS = mainActivity.getAssets().open("data/" + museum + "/" + artefact + "/image.jpg");
                    imageView.setImageBitmap(BitmapFactory.decodeStream(imageIS));

                    InputStream descriptionIS = mainActivity.getAssets().open("data/" + museum + "/" + artefact + "/description.txt");
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(descriptionIS))) {
                        String title = br.readLine();
                        String description = "";
                        String line = "";
                        mainActivity.setTitle(title);
                        while ((line = br.readLine()) != null) {
                            description += line + "\n";
                        }
                        ((TextView) mainActivity.findViewById(R.id.artefactName)).setText(title);
                        ((TextView) mainActivity.findViewById(R.id.artefactDescription)).setText(description);
                    } catch (Exception ex) {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}