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
        Integer size = new Integer(sizeSelectedItems.intValue());
        Double sizekB = sizeSelectedItems / 1000;
        progressBar.setProgress(size / 100000);
        percentageProgress.setText(String.format("%.2fkB", sizekB));
    }
}
