package com.example.powder.monica;

import android.graphics.Typeface;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class PriorityBar {

    private TextView willNotText;

    private TextView couldText;

    private TextView shouldText;

    private TextView mustText;

    private SeekBar seekBar;

    private String chosenPriority = "WillNot ";

    public PriorityBar(View view) {
        willNotText = view.findViewById(R.id.willNotText);
        couldText = view.findViewById(R.id.couldText);
        shouldText = view.findViewById(R.id.shouldText);
        mustText = view.findViewById(R.id.mustText);
        seekBar = view.findViewById(R.id.priorityBar);

        willNotText.setTypeface(null, Typeface.BOLD);
        willNotText.setTextSize(16);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPriority(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setPriority(int progress) {
        switch (progress) {
            case 0:
                setChosenPriority(willNotText, couldText, shouldText, mustText);
                chosenPriority = "WillNot ";
                break;

            case 1:
                setChosenPriority(couldText, willNotText, shouldText, mustText);
                chosenPriority = "Could ";
                break;

            case 2:
                setChosenPriority(shouldText, mustText, couldText, willNotText);
                chosenPriority = "Should ";
                break;

            case 3:
                setChosenPriority(mustText, shouldText, willNotText, couldText);
                chosenPriority = "Must ";
                break;
            default:
                break;
        }
    }

    public String getChosenPriority() {
        return chosenPriority;
    }

    private void setChosenPriority(TextView chosenPriority, TextView a, TextView b, TextView c) {
        chosenPriority.setTypeface(null, Typeface.BOLD);
        chosenPriority.setTextSize(16);

        a.setTypeface(null, Typeface.NORMAL);
        a.setTextSize(14);
        b.setTypeface(null, Typeface.NORMAL);
        b.setTextSize(14);
        c.setTypeface(null, Typeface.NORMAL);
        c.setTextSize(14);

    }
}
