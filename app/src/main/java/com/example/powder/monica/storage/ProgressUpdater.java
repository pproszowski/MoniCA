package com.example.powder.monica.storage;

import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class ProgressUpdater {

    private ProgressBar progressBar;
    private TextView percentageProgress;

    public ProgressUpdater(ProgressBar progressBar, TextView percentageProgress) {
        this.progressBar = progressBar;
        this.percentageProgress = percentageProgress;
    }

    public void updateProgress(List<FileItem> fileItems) {
        Double sizeSelectedItems = getSizeOfSelectedFiles(fileItems);
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

    private Double getSizeOfSelectedFiles(List<FileItem> fileItems) {
        Double size = 0d;
        for (FileItem fileItem : fileItems) {
            if (fileItem.isChecked()) {
                size += fileItem.getSize();
            }
        }
        return size;
    }
}
