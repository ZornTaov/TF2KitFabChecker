// 
// Decompiled by Procyon v0.5.36
// 

package org.zornco.tf2kitfabchecker;

import javax.swing.LayoutStyle;
import javax.swing.GroupLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.OverlayLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import java.awt.Component;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;

public class KitPanel extends JPanel
{
    GridBagConstraints gbc;
    int partRow;
    int canMake;
    public JPanel iconPanel;
    public JLabel kitIcon;
    public JLabel nameLabel;
    private JPanel partList;
    public JLabel weaponIcon;
    
    public KitPanel() {
        this.gbc = new GridBagConstraints();
        this.partRow = 0;
        this.canMake = 0;
        this.initComponents();
        this.setVisible(true);
    }
    
    public void addPart(final String part, final int have, final int need) {
        final JLabel label = new JLabel(part + ": " + have + "/" + need);
        label.setOpaque(true);
        this.canMake += ((have >= need) ? 1 : -100);
        label.setBackground((have >= need) ? new Color(200, 255, 200) : new Color(255, 200, 200));
        try {
            final File file = new File(TF2KitFabChecker.IMG_PATHS.get(part));
            final Icon ic = new ResizeableIcon(file, 32, 32);
            label.setIcon(ic);
        }
        catch (IOException ex) {
            Logger.getLogger(KitPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.gbc.gridy = this.partRow;
        ++this.partRow;
        this.partList.add(label, this.gbc);
    }
    
    public void setIcon(final String weapon, final String type) {
        try {
            File file = new File(TF2KitFabChecker.IMG_PATHS.get(weapon));
            Icon ic = new ResizeableIcon(file, 100, 100);
            this.weaponIcon.setIcon(ic);
            file = new File(TF2KitFabChecker.IMG_PATHS.get(type));
            ic = new ResizeableIcon(file, 100, 100);
            this.kitIcon.setIcon(ic);
        }
        catch (IOException ex) {
            Logger.getLogger(KitPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initComponents() {
        this.iconPanel = new JPanel();
        this.weaponIcon = new JLabel();
        this.kitIcon = new JLabel();
        this.nameLabel = new JLabel();
        this.partList = new JPanel();
        this.setBorder(BorderFactory.createBevelBorder(0));
        this.iconPanel.setLayout(new OverlayLayout(this.iconPanel));
        this.iconPanel.add(this.weaponIcon);
        this.iconPanel.add(this.kitIcon);
        this.nameLabel.setFont(new Font("Tahoma", 0, 18));
        this.nameLabel.setText("NAMENAMENAME");
        this.partList.setOpaque(false);
        this.partList.setLayout(new GridLayout(0, 1, 0, -5));
        final GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.iconPanel, -2, 100, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.nameLabel, -1, 211, 32767).addComponent(this.partList, -1, -1, 32767))));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.nameLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.partList, -1, 42, 32767)).addComponent(this.iconPanel, -1, -1, 32767));
    }
}
