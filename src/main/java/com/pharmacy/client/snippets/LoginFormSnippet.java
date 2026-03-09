package com.pharmacy.client.snippets;

import com.pharmacy.client.ApiClient;
import com.pharmacy.entity.User;

import javax.swing.*;

/**
 * Swing event logic snippets for the Login Form.
 *
 * <p>
 * <b>Usage:</b> Paste this logic into your Login button's actionPerformed
 * handler in the UI builder.
 * </p>
 */
public class LoginFormSnippet {

    /**
     * Paste into your "Login" button's actionPerformed body.
     */
    public void btnLogin_actionPerformed(JTextField txtUsername, JPasswordField txtPassword, JFrame loginFrame) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()); // Plain text for demo

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Please enter both username and password.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disable login button to prevent multiple clicks
        // btnLogin.setEnabled(false);

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                // Call the ApiClient to perform authentication
                return ApiClient.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User loggedInUser = get();
                    if (loggedInUser != null) {
                        JOptionPane.showMessageDialog(loginFrame,
                                "Login successful! Welcome " + loggedInUser.getUsername());
                        // TODO: Open DashboardFrame and close loginFrame
                        // new DashboardFrame(loggedInUser).setVisible(true);
                        // loginFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.", "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(loginFrame, "Connection error: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    // btnLogin.setEnabled(true);
                }
            }
        }.execute();
    }
}
