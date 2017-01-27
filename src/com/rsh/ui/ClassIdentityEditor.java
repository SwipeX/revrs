package com.rsh.ui;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import com.rsh.miu.ClassIdentity;
import com.rsh.util.Store;

import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author TimD
 */
public class ClassIdentityEditor extends WebPanel {

    public ClassIdentityEditor() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new WebLabel();
        explicitName = new WebTextField();
        superClass = new WebTextField();
        jLabel2 = new WebLabel();
        subClass = new WebTextField();
        jLabel3 = new WebLabel();
        fieldCountTable = new WebTable();
        tableScroll = new WebScrollPane(fieldCountTable);
        jLabel4 = new WebLabel();
        testButton = new WebButton();
        identityList = new WebList();
        listScroll = new WebScrollPane(identityList);

        jLabel1.setText("Explicit Name:");

        jLabel2.setText("Super Class:");

        jLabel3.setText("Sub Class:");

        fieldCountTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null}
                },
                new String[]{
                        "Descriptor", "Count"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        tableScroll.setViewportView(fieldCountTable);

        jLabel4.setText("Result:");

        testButton.setText("Test");

        identityList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = Store.getClassIdentities().keySet().stream().collect(Collectors.toList()).toArray(new String[]{});

            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });
        identityList.addListSelectionListener(this::identityListValueChanged);
        listScroll.setViewportView(identityList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(testButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(listScroll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(102, 102, 102)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(explicitName, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(superClass, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(subClass, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(141, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(explicitName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(superClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(subClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(listScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(testButton))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    private void identityListValueChanged(ListSelectionEvent evt) {
        DefaultTableModel model = (DefaultTableModel) fieldCountTable.getModel();
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            model.setValueAt("", i, 0);
            model.setValueAt("", i, 1);
        }
        ClassIdentity identity = Store.getClassIdentities().get(identityList.getSelectedValue());
        if (identity != null) {
            explicitName.setText(identity.getExplicitName());
            superClass.setText(identity.getSuperClass());
            subClass.setText(identity.getSubClass());
            Map<String, Integer> map = identity.getFieldCounts();
            int index = 0;
            for (Map.Entry entry : map.entrySet()) {
                model.setValueAt(entry.getKey(), index, 0);
                model.setValueAt(entry.getValue(), index, 1);
                index++;
            }
        }
    }

    // Variables declaration - do not modify
    private WebButton testButton;
    private WebLabel jLabel1;
    private WebLabel jLabel2;
    private WebLabel jLabel3;
    private WebLabel jLabel4;
    private WebList identityList;
    private WebScrollPane tableScroll;
    private WebScrollPane listScroll;
    private WebTable fieldCountTable;
    private WebTextField explicitName;
    private WebTextField superClass;
    private WebTextField subClass;
    // End of variables declaration
}
