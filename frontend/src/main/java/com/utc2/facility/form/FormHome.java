package com.utc2.facility.form;

import com.utc2.facility.component.Card;
import com.utc2.facility.model.ModelCard;
import com.utc2.facility.service.RoomService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class FormHome extends javax.swing.JPanel {
    public FormHome() {
        initComponents();
        setOpaque(false);
        initData();
    }

    private void initData() {
        RoomService roomService = new RoomService();
        try {
            List<ModelCard> rooms = roomService.fetchRooms();
            displayRooms(rooms);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayRooms(List<ModelCard> rooms) {
        showCard1.removeAll();
        showCard2.removeAll();
        showCard3.removeAll();

        showCard1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        showCard2.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        showCard3.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        for (ModelCard room : rooms) {
            Card card = new Card();
            card.setData(room);

            String typeRoomName = room.getRoomTypeName();

            switch (typeRoomName) {
                case "Phòng học":
                    showCard1.add(card);
                    break;
                case "Phòng học 1":
                    showCard2.add(card);
                    break;
                case "Phòng học 2":
                    showCard3.add(card);
                    break;
                default:
                    System.out.println("Loại phòng không xác định: " + typeRoomName);
                    break;
            }
        }

        if (showCard1.getComponentCount() == 0) showCard1.add(new JLabel("Không có phòng"));
        if (showCard2.getComponentCount() == 0) showCard2.add(new JLabel("Không có phòng"));
        if (showCard3.getComponentCount() == 0) showCard3.add(new JLabel("Không có phòng"));

        // Cập nhật UI sau khi thêm các phòng
        showCard1.revalidate();
        showCard1.repaint();
        showCard2.revalidate();
        showCard2.repaint();
        showCard3.revalidate();
        showCard3.repaint();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        lbRoomType3 = new javax.swing.JLabel();
        lbFacilitiesType = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lbRoomType1 = new javax.swing.JLabel();
        lbRoomType2 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        showCard1 = new javax.swing.JPanel();
        showCard2 = new javax.swing.JPanel();
        showCard3 = new javax.swing.JPanel();

        lbRoomType3.setText("Room Meeting");

        lbFacilitiesType.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        lbFacilitiesType.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFacilitiesType.setText("ROOM");

        lbRoomType1.setText("Room Study");

        lbRoomType2.setText("Room Teacher");

        javax.swing.GroupLayout showCard1Layout = new javax.swing.GroupLayout(showCard1);
        showCard1.setLayout(showCard1Layout);
        showCard1Layout.setHorizontalGroup(
            showCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        showCard1Layout.setVerticalGroup(
            showCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 108, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout showCard2Layout = new javax.swing.GroupLayout(showCard2);
        showCard2.setLayout(showCard2Layout);
        showCard2Layout.setHorizontalGroup(
            showCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        showCard2Layout.setVerticalGroup(
            showCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 108, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout showCard3Layout = new javax.swing.GroupLayout(showCard3);
        showCard3.setLayout(showCard3Layout);
        showCard3Layout.setHorizontalGroup(
            showCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        showCard3Layout.setVerticalGroup(
            showCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(354, Short.MAX_VALUE)
                .addComponent(lbFacilitiesType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(333, 333, 333))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addComponent(lbRoomType1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbRoomType3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbRoomType2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showCard3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showCard2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showCard1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(lbFacilitiesType, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbRoomType1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showCard1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbRoomType3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showCard2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbRoomType2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showCard3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lbFacilitiesType;
    private javax.swing.JLabel lbRoomType1;
    private javax.swing.JLabel lbRoomType2;
    private javax.swing.JLabel lbRoomType3;
    private javax.swing.JPanel showCard1;
    private javax.swing.JPanel showCard2;
    private javax.swing.JPanel showCard3;
    // End of variables declaration//GEN-END:variables
}
