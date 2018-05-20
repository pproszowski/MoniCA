package com.example.powder.monica.storage;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class ProgressUpdater {

    private ArrayList<String> checkedFileNames;
    private Set<FileItem> fileItems;
    private Double sizeSelectedItems;
    private ProgressBar progressBar;
    private TextView percentageProgress;
    private String path;

    public ProgressUpdater(ArrayList<String> checkedFileNames, Set<FileItem> fileItems, Double sizeSelectedItems, ProgressBar progressBar, TextView percentageProgress, String path) {
        this.checkedFileNames = checkedFileNames;
        this.fileItems = fileItems;
        this.sizeSelectedItems = sizeSelectedItems;
        this.progressBar = progressBar;
        this.percentageProgress = percentageProgress;
        this.path = path;
    }

    public void updateProgress() {
        checkedFileNames.clear();
        for (FileItem fileItem : fileItems) {
            if (fileItem.isChecked()) {
                checkedFileNames.add(fileItem.getName());
            }
        }
        sizeSelectedItems = 0.0;
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (checkedFileNames.contains(file.getName())) {
                sizeSelectedItems += file.length();
            }
//                            if (!"email.txt".equals(file.getName())) {
//                                size += file.length();
//                            }
        }

        Integer size = new Integer(sizeSelectedItems.intValue());
        Double sizekB = sizeSelectedItems/1000;
        progressBar.setProgress(size/100000);
        percentageProgress.setText(String.format("%.2fkB", sizekB));
    }
}
