package com.utc2.facility.view;

import com.utc2.facility.controller.LoginController;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView() {
        setTitle("Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new MigLayout("wrap 2", "[grow]10[grow]", "[]10[]10[]"));

        panel.add(new JLabel("Username:"), "cell 0 0");
        usernameField = new JTextField(20);
        panel.add(usernameField, "cell 1 0, growx");

        panel.add(new JLabel("Password:"), "cell 0 1");
        passwordField = new JPasswordField(20);
        panel.add(passwordField, "cell 1 1, growx");

        loginButton = new JButton("Login");
        panel.add(loginButton, "span, center");
        loginButton.addActionListener(e -> handleLogin());

        add(panel);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        LoginController loginController = new LoginController();
        String token = loginController.login(username, password);

        if (token != null) {
            System.out.println("Đăng nhập thành công! Token: " + token);
            this.dispose();

            MainUIView mainUI = new MainUIView();
            mainUI.setVisible(true);
        } else {
            System.out.println("Đăng nhập thất bại!");
            JOptionPane.showMessageDialog(this, "Đăng nhập thất bại!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}
