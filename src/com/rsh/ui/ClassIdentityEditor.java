package com.rsh.ui;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import com.rsh.miu.ClassIdentity;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by TimD on 1/25/2017.
 */
public class ClassIdentityEditor extends WebPanel {
    WebTable fieldCounts;
    WebTextField superClass;
    WebTextField subClass;
    WebTextField explicitName;
    WebLabel result;
    WebButton find;

    public ClassIdentityEditor() {
        super();
        fieldCounts = new WebTable(10, 2);
        superClass = new WebTextField();
        subClass = new WebTextField();
        explicitName = new WebTextField();
        result = new WebLabel();
        find = new WebButton("Find");
        add(explicitName, BorderLayout.NORTH);
        add(superClass, BorderLayout.NORTH);
        add(subClass, BorderLayout.NORTH);
        add(fieldCounts, BorderLayout.CENTER);
        add(result, BorderLayout.SOUTH);
        add(find,BorderLayout.SOUTH);
    }
}
