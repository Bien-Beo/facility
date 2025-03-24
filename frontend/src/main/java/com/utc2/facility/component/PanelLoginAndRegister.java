package com.utc2.facility.component;

import com.utc2.facility.controller.LoginController;
import com.utc2.facility.main.Main;
import com.utc2.facility.swing.Button;
import com.utc2.facility.swing.MyTextField;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class PanelLoginAndRegister extends javax.swing.JLayeredPane {
    
    public PanelLoginAndRegister() {
        initComponents();
        initLogin();
        initRegister();
        login.setVisible(true);
        register.setVisible(false);
    }

    private void initRegister() {
        register.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]25[]push"));
        JLabel label = new JLabel("Create Account");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(7, 164, 121));
        register.add(label);

        MyTextField txtUser = new MyTextField();
        txtUser.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/user.png")));
        txtUser.setHint("Username");
        register.add(txtUser, "w 60%");

        MyTextField txtPassword = new MyTextField();
        txtPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/pass.png")));
        txtPassword.setHint("Password");
        register.add(txtPassword, "w 60%");

        Button cmd = new Button();
        cmd.setFocusPainted(false);
        cmd.setBackground(new Color(7, 164, 121));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.setText("SIGN UP");
        register.add(cmd, "w 40%, h 40");
    }

    private MyTextField txtUser;
    private MyTextField txtPassword;
    private Button btnLogin;

    private void initLogin() {
        login.setLayout(new MigLayout(
                "wrap",
                "push[center]push",
                "push[]25[]10[]10[]25[]push"
                )
        );
        JLabel label = new JLabel("Sign In");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(7, 164, 121));
        login.add(label);

        txtUser = new MyTextField();
        txtUser.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/user.png")));
        txtUser.setHint("Username");
        login.add(txtUser, "w 60%");

        txtPassword = new MyTextField();
        txtPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/pass.png")));
        txtPassword.setHint("Password");
        login.add(txtPassword, "w 60%");

        JButton cmdForgot = new JButton("Forgot your password ?");
        cmdForgot.setForeground(new Color(100, 100, 100));
        cmdForgot.setFont(new Font("sansserif", 1, 12));
        cmdForgot.setContentAreaFilled(false);
        cmdForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdForgot.setBorder(null);
        login.add(cmdForgot);

        btnLogin = new Button();
        btnLogin.setFocusPainted(false);
        btnLogin.setBackground(new Color(7, 164, 121));
        btnLogin.setForeground(new Color(250, 250, 250));
        btnLogin.setText("SIGN IN");

        btnLogin.addActionListener(e -> handleLogin());

        login.add(btnLogin, "w 40%, h 40");
    }

    private void handleLogin() {
        String username = txtUser.getText();
        String password = txtPassword.getText();

        LoginController loginController = new LoginController();
        String token = loginController.login(username, password);

        if (token != null) {
            System.out.println("Đăng nhập thành công! Token: " + token);
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);

            SwingUtilities.getWindowAncestor(this).dispose();

            // Mở Main UI
            java.awt.EventQueue.invokeLater(() -> {
                Main.setAuthenticated(true);
                new Main().setVisible(true);
            });
        } else {
            System.out.println("Đăng nhập thất bại!");
            JOptionPane.showMessageDialog(this, "Đăng nhập thất bại! Kiểm tra lại tài khoản và mật khẩu.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showLogin(boolean show) {
        if (show) {
            login.setVisible(false);
            register.setVisible(true);
        } else {
            login.setVisible(true);
            register.setVisible(false);
        }
    }

    public void showRegister(boolean show) {
        if (show) {
            login.setVisible(true);
            register.setVisible(false);
        } else {
            login.setVisible(false);
            register.setVisible(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        login = new javax.swing.JPanel();
        register = new javax.swing.JPanel();

        setLayout(new java.awt.CardLayout());

        login.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout loginLayout = new javax.swing.GroupLayout(login);
        login.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(login, "card3");

        register.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout registerLayout = new javax.swing.GroupLayout(register);
        register.setLayout(registerLayout);
        registerLayout.setHorizontalGroup(
            registerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        registerLayout.setVerticalGroup(
            registerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(register, "card2");
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel login;
    private javax.swing.JPanel register;
    // End of variables declaration//GEN-END:variables
}
