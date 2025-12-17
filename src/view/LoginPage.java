package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class LoginPage extends JFrame {

    // Colors
    private final Color PRIMARY_RED = new Color(190, 20, 20);
    private final Color DARK_RED = new Color(139, 0, 0);
    private final Color TEXT_GRAY = new Color(100, 100, 100);
    private final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 28);
    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);

    // Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton actionButton;
    private JButton toggleButton;
    private JLabel titleLabel;

    // State
    private boolean isLoginMode = true;

    public LoginPage() {
        setTitle("Blood Bank System - Secure Access");

        // 1. Set the Default "Restored" Size
        setSize(1000, 700);

        // 2. Set the MINIMUM Size (Prevents shrinking too small to see)
        setMinimumSize(new Dimension(900, 650));

        // 3. Start Maximized
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new GridLayout(1, 2)); // Split Screen

        // --- LEFT SIDE: The "Brand" Panel ---
        JPanel brandPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient Background
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_RED, getWidth(), getHeight(), DARK_RED);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw Big Logo circles
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillOval(-50, -50, 300, 300);
                g2.fillOval(getWidth()-200, getHeight()-200, 400, 400);

                // Draw Blood Drop (Centered)
                int cx = getWidth()/2;
                int cy = getHeight()/2 - 20;
                g2.setColor(Color.WHITE);

                GeneralPath drop = new GeneralPath();
                drop.moveTo(cx, cy - 50);
                drop.curveTo(cx + 50, cy, cx + 50, cy + 70, cx, cy + 70);
                drop.curveTo(cx - 50, cy + 70, cx - 50, cy, cx, cy - 50);
                drop.closePath();
                g2.fill(drop);

                // Text
                g2.setFont(new Font("SansSerif", Font.BOLD, 32));
                String s1 = "BLOOD BANK";
                String s2 = "MANAGEMENT";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s1, cx - fm.stringWidth(s1)/2, cy + 120);
                g2.drawString(s2, cx - fm.stringWidth(s2)/2, cy + 160);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
                String s3 = "Save Lives. Manage Stock.";
                fm = g2.getFontMetrics();
                g2.drawString(s3, cx - fm.stringWidth(s3)/2, cy + 200);
            }
        };
        add(brandPanel);

        // --- RIGHT SIDE: The "Form" Panel ---
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout()); // Centers the box

        // Fixed-width content box
        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setBackground(Color.WHITE);
        contentBox.setPreferredSize(new Dimension(400, 520)); // Ensure this fits in Min Size

        // Title
        titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_RED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentBox.add(titleLabel);

        JLabel subtitle = new JLabel("Please enter your details");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentBox.add(subtitle);

        contentBox.add(Box.createRigidArea(new Dimension(0, 40)));

        // Inputs
        contentBox.add(createLabel("Username"));
        usernameField = createField();
        contentBox.add(usernameField);
        contentBox.add(Box.createRigidArea(new Dimension(0, 20)));

        contentBox.add(createLabel("Password"));
        passwordField = new JPasswordField();
        styleField(passwordField);
        contentBox.add(passwordField);
        contentBox.add(Box.createRigidArea(new Dimension(0, 20)));

        contentBox.add(createLabel("Role"));
        roleComboBox = new JComboBox<>(new String[]{"Admin", "Nurse"});
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        contentBox.add(roleComboBox);

        contentBox.add(Box.createRigidArea(new Dimension(0, 40)));

        // Button
        actionButton = new JButton("LOGIN");
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        actionButton.setBackground(PRIMARY_RED);
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        actionButton.setBorderPainted(false);
        actionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentBox.add(actionButton);

        contentBox.add(Box.createRigidArea(new Dimension(0, 20)));

        // Link
        toggleButton = new JButton("New User? Create an Account");
        toggleButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        toggleButton.setForeground(PRIMARY_RED);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleButton.addActionListener(e -> toggleMode());
        contentBox.add(toggleButton);

        formPanel.add(contentBox);
        add(formPanel);
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            titleLabel.setText("Welcome Back");
            actionButton.setText("LOGIN");
            toggleButton.setText("New User? Create an Account");
        } else {
            titleLabel.setText("Create Account");
            actionButton.setText("REGISTER");
            toggleButton.setText("Already have an account? Login");
        }
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createField() {
        JTextField t = new JTextField();
        styleField(t);
        return t;
    }

    private void styleField(JTextField t) {
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getSelectedRole() { return (String) roleComboBox.getSelectedItem(); }
    public boolean isLoginMode() { return isLoginMode; }
    public JButton getActionButton() { return actionButton; }
    public void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }
    public void clearFields() { usernameField.setText(""); passwordField.setText(""); }
}