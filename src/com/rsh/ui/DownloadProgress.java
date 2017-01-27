package com.rsh.ui;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import pw.tdekk.util.Crawler;

import java.awt.*;

/**
 * Created by TimD on 1/26/2017.
 */
public class DownloadProgress extends WebPanel {
    private WebProgressBar progressBar;

    public DownloadProgress() {
        progressBar = new WebProgressBar(0,100);
        progressBar.setProgressTopColor(Color.ORANGE);
        progressBar.setProgressBottomColor(Color.RED);
        add(progressBar);
        new Thread(() -> {
            int value;
            while ((value = Crawler.getDownloadPercent()) < 100) {
                progressBar.setValue(value);
                progressBar.invalidate();
            }
        }).start();
    }
}
