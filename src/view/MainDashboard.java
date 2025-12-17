package view;

import controller.AlertController;
import controller.BloodRequestController;
import controller.BloodUnitController;
import controller.DonorController;
import dao.AlertDAO;
import dao.BloodRequestDAO;
import dao.BloodUnitDAO;
import dao.DonorDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDashboard extends JFrame {

    private final Color PRIMARY_RED = new Color(190, 20, 20);
    private final Color BG_COLOR = new Color(245, 248, 250);

    private final User currentUser;

    // Default constructor for testing (defaults to Admin)
    public MainDashboard() {
        this(new User(0, "Administrator", "", "Admin"));
    }

    public MainDashboard(User user) {
        this.currentUser = user;

        setTitle("Blood Bank Management System - Main Menu");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(1000, 100));
        headerPanel.setBorder(new EmptyBorder(0, 40, 0, 40));

        JLabel titleLabel = new JLabel("Blood Bank Management System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(new MainLogoIcon(50));

        // DYNAMIC WELCOME MESSAGE
        JLabel subtitleLabel = new JLabel("Welcome, " + user.getRole() + ": " + user.getUsername());
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);

        // Logout Button
        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setBackground(new Color(255, 255, 255, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new app.App().main(null); // Restart app
        });
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. DASHBOARD GRID
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setBackground(BG_COLOR);
        gridPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        gridPanel.add(createDashboardCard("Donor Management", "Register & Manage Donors", new UserIcon(), e -> openDonorModule()));
        gridPanel.add(createDashboardCard("Blood Stock", "Manage Inventory", new DropIcon(), e -> openStockModule()));
        gridPanel.add(createDashboardCard("Blood Requests", "Process Hospital Requests", new ClipboardIcon(), e -> openRequestModule()));

        // ROLE BASED ACCESS CONTROL (RBAC)
        // If user is a 'Nurse', disable the Alerts module (Example rule)
        if ("Admin".equalsIgnoreCase(user.getRole())) {
            gridPanel.add(createDashboardCard("Expiry Alerts", "View Warnings & Status", new BellIcon(), e -> openAlertModule()));
        } else {
            // Add a disabled/locked card for Nurses
            gridPanel.add(createLockedCard("Expiry Alerts", "Restricted Access", new BellIcon()));
        }

        add(gridPanel, BorderLayout.CENTER);

        // 3. FOOTER
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.add(new JLabel("© 2025 Blood Bank System | Developed by MANISHIMWE Kwizera Jean Luc"));
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- NAVIGATION ---
    private void openDonorModule() {
        DonorDAO dao = new DonorDAO();
        DonorRegistrationPage view = new DonorRegistrationPage();
        new DonorController(view, dao);
        view.setVisible(true);
    }
    private void openStockModule() {
        BloodUnitDAO dao = new BloodUnitDAO();
        BloodStockPage view = new BloodStockPage();
        new BloodUnitController(view, dao);
        view.setVisible(true);
    }
    private void openRequestModule() {
        BloodRequestDAO dao = new BloodRequestDAO();
        BloodRequestPage view = new BloodRequestPage();
        new BloodRequestController(view, dao);
        view.setVisible(true);
    }
    private void openAlertModule() {
        AlertDAO dao = new AlertDAO();
        AlertsPage view = new AlertsPage();
        new AlertController(view, dao);
        view.setVisible(true);
    }

    // --- CARDS ---
    private JPanel createDashboardCard(String title, String sub, Icon icon, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,230,230),1), new EmptyBorder(20,20,20,20)));

        JLabel iconLabel = new JLabel(icon);
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel t = new JLabel(title); t.setFont(new Font("SansSerif", Font.BOLD, 18)); t.setForeground(new Color(50,50,50));
        JLabel s = new JLabel(sub); s.setFont(new Font("SansSerif", Font.PLAIN, 12)); s.setForeground(Color.GRAY);
        textPanel.add(t); textPanel.add(s);
        card.add(textPanel, BorderLayout.CENTER);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null)); }
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(250, 240, 240)); card.setBorder(BorderFactory.createLineBorder(PRIMARY_RED, 2)); }
            public void mouseExited(MouseEvent e) { card.setBackground(Color.WHITE); card.setBorder(BorderFactory.createLineBorder(new Color(230,230,230), 1)); }
        });
        return card;
    }

    private JPanel createLockedCard(String title, String sub, Icon icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245,245,245)); // Grey out
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setEnabled(false); // Grey out icon
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(new Color(245,245,245));
        textPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel t = new JLabel(title + " (Locked)"); t.setFont(new Font("SansSerif", Font.BOLD, 18)); t.setForeground(Color.GRAY);
        JLabel s = new JLabel(sub); s.setFont(new Font("SansSerif", Font.ITALIC, 12)); s.setForeground(Color.GRAY);
        textPanel.add(t); textPanel.add(s);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // --- ICONS (Same as before) ---
    private static class MainLogoIcon implements Icon { int s; public MainLogoIcon(int s) {this.s=s;} public int getIconWidth(){return s;} public int getIconHeight(){return s;} public void paintIcon(Component c,Graphics g,int x,int y){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); g2.translate(x,y); g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3)); g2.drawOval(5,5,s-10,s-10); g2.fillOval(s/2-8, s/2-6, 16, 16); int[] px={s/2-8,s/2+8,s/2}; int[] py={s/2+2,s/2+2,10}; g2.fillPolygon(px,py,3); g2.dispose(); } }
    private static class UserIcon implements Icon { public int getIconWidth(){return 50;} public int getIconHeight(){return 50;} public void paintIcon(Component c,Graphics g,int x,int y){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); g2.translate(x,y); g2.setColor(new Color(190,20,20)); g2.fillOval(15,5,20,20); g2.fillArc(5,28,40,22,0,180); g2.dispose(); } }
    private static class DropIcon implements Icon { public int getIconWidth(){return 50;} public int getIconHeight(){return 50;} public void paintIcon(Component c,Graphics g,int x,int y){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); g2.translate(x,y); g2.setColor(new Color(190,20,20)); g2.fillOval(12,22,26,26); int[] px={12,38,25}; int[] py={34,34,8}; g2.fillPolygon(px,py,3); g2.dispose(); } }
    private static class ClipboardIcon implements Icon { public int getIconWidth(){return 50;} public int getIconHeight(){return 50;} public void paintIcon(Component c,Graphics g,int x,int y){ Graphics2D g2=(Graphics2D)g.create(); g2.translate(x,y); g2.setColor(new Color(190,20,20)); g2.setStroke(new BasicStroke(3)); g2.drawRect(10,12,30,30); g2.fillRect(18,5,14,10); g2.dispose(); } }
    private static class BellIcon implements Icon { public int getIconWidth(){return 50;} public int getIconHeight(){return 50;} public void paintIcon(Component c,Graphics g,int x,int y){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); g2.translate(x,y); g2.setColor(new Color(190,20,20)); g2.fillArc(10,10,30,30,0,180); g2.fillRect(10,25,30,10); g2.fillOval(22,38,6,6); g2.dispose(); } }
}