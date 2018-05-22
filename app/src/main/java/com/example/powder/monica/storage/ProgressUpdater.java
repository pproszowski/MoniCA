package com.example.powder.monica.storage;

import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressUpdater {

    private ProgressBar progressBar;
    private TextView percentageProgress;

    public ProgressUpdater(ProgressBar progressBar, TextView percentageProgress) {
        this.progressBar = progressBar;
        this.percentageProgress = percentageProgress;
    }

    public void updateProgress(Double sizeSelectedItems) {
        Double sizeConverted;
        Integer size = new Integer(sizeSelectedItems.intValue());
        if(size <= 1048576){
            sizeConverted = sizeSelectedItems / 1024;
            percentageProgress.setText(String.format("%.2fkB / 10MB", sizeConverted));
        }
        else {
            sizeConverted = sizeSelectedItems / 1048576;
            percentageProgress.setText(String.format("%.2fMB / 10MB", sizeConverted));
        }
        progressBar.setProgress(size / 100000);

    }
}
