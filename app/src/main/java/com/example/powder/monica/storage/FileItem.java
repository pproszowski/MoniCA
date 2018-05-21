package com.example.powder.monica.storage;

import android.graphics.drawable.Icon;

import java.io.File;

public class FileItem {
    private String name;
    private double size;
    private boolean checked;

    public FileItem(String name, double size, boolean checked) {
        this.name = name;
        this.checked = checked;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FileItem && name.equals(((FileItem) obj).getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public Double getSize() {
        return size;
    }
}

