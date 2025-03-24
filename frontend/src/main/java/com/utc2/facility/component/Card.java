package com.utc2.facility.component;

import com.utc2.facility.model.ModelCard;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class Card extends javax.swing.JPanel {

    public Color getColorGradient() {
        return colorGradient;
    }

    public void setColorGradient(Color colorGradient) {
        this.colorGradient = colorGradient;
    }

    private Color colorGradient;

    public Card() {
        initComponents();
        setOpaque(false);
        setBackground(new Color(112, 69, 246));
        colorGradient = new Color(255, 255, 255);
    }

    public void setData(ModelCard data) {
        DecimalFormat df = new DecimalFormat("#,##0.##");
        lnName.setText(data.getName());
        lbDescription.setText(data.getDescription());
        lbCapacity.setText(df.format(data.getCapacity()));

        // Đặt màu chữ cho các label
        lbDescription.setForeground(Color.WHITE);
        lbCapacity.setForeground(Color.WHITE);

        // Load ảnh từ đường dẫn
        if (data.getImage() != null && !data.getImage().isEmpty()) {
            ImageIcon icon = new ImageIcon(getClass().getResource(data.getImage()));
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lbImage.setIcon(new ImageIcon(img));
        }

        // Cập nhật giao diện sau khi set dữ liệu
        revalidate();
        repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lnName = new javax.swing.JLabel();
        lbDescription = new javax.swing.JLabel();
        lbCapacity = new javax.swing.JLabel();
        lbImage = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lnName.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lnName.setForeground(new java.awt.Color(255, 255, 255));
        lnName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnName.setText("Title");

        lbDescription.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbDescription.setText("Description");

        lbCapacity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbCapacity.setText("Capacity");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lnName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lbImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lbDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
            .addComponent(lbCapacity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(lbImage, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lnName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbCapacity)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, getHeight(), getBackground(), getWidth(), 0, colorGradient);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbCapacity;
    private javax.swing.JLabel lbDescription;
    private javax.swing.JLabel lbImage;
    private javax.swing.JLabel lnName;
    // End of variables declaration//GEN-END:variables
}
