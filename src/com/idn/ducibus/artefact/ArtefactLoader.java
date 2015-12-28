package com.idn.ducibus.artefact;

import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;
import com.idn.ducibus.MainActivity;
import com.idn.ducibus.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ArtefactLoader {
    private MainActivity activity;

    public ArtefactLoader(MainActivity activity) {
        this.activity = activity;
    }

    public void loadArtefact(ArtefactDescriptor artefact) {
        ImageView imageView = (ImageView) activity.findViewById(R.id.imageView);

        try {
            InputStream imageIS = activity.getAssets().open("data/" + artefact.getMuseumId() + "/" + artefact.getArtefactId() + "/image.jpg");
            imageView.setImageBitmap(BitmapFactory.decodeStream(imageIS));

            InputStream descriptionIS = activity.getAssets().open("data/" + artefact.getMuseumId() + "/" + artefact.getArtefactId() + "/description.txt");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(descriptionIS))) {
                String title = br.readLine();
                String description = "";
                String line = "";
                activity.setTitle(title);
                while ((line = br.readLine()) != null) {
                    description += line + "\n";
                }
                ((TextView) activity.findViewById(R.id.artefactName)).setText(title);
                ((TextView) activity.findViewById(R.id.artefactDescription)).setText(description);
            } catch (Exception ex) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
