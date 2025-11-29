package view;

import model.Alert;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * View class for the Alerts Page.
 * "PRO" DASHBOARD DESIGN VERSION
 */
public class AlertsPage extends JFrame {

    // --- Theme Colors ---
    private final Color PRIMARY_RED = new Color(190, 20, 20);
    private final Color DARK_GREY = new Color(50, 50, 50);
    private final Color LIGHT_BG = new Color(245, 248, 250);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);

    // --- Components ---
    private JButton refreshButton;
    private JButton deleteButton; // To dismiss alerts
    private JTable alertsTable;
    private DefaultTableModel tableModel;

    public AlertsPage() {
        setTitle("System Alerts & Notifications");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel titleLabel = new JLabel(" Expiry Alerts");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(new BellIcon(30));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. --- LEFT SIDEBAR (Actions) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(250, 700));
        sidebarPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Buttons
        refreshButton = createBigButton("REFRESH / SCAN", new Color(46, 139, 87));
        sidebarPanel.add(refreshButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        deleteButton = createBigButton("DISMISS ALERT", DARK_GREY);
        sidebarPanel.add(deleteButton);

        sidebarPanel.add(Box.createVerticalGlue());

        // Info Text
        JTextArea infoText = new JTextArea("System automatically scans for:\n- Units expired today\n- Units expiring in 7 days\n\nRun 'Refresh' to force a scan.");
        infoText.setWrapStyleWord(true);
        infoText.setLineWrap(true);
        infoText.setEditable(false);
        infoText.setFont(new Font("SansSerif", Font.ITALIC, 12));
        infoText.setForeground(Color.GRAY);
        sidebarPanel.add(infoText);

        add(sidebarPanel, BorderLayout.WEST);

        // 3. --- MAIN AREA ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        // Image
        ImagePanel heroImagePanel = new ImagePanel("alerts_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 200));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        // Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(LIGHT_BG);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"Alert ID", "Blood Unit ID", "Alert Type", "Date Generated", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        alertsTable = new JTable(tableModel);

        // Table Style & Custom Renderer for Colors
        alertsTable.setRowHeight(30);
        alertsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        alertsTable.setShowGrid(false);
        alertsTable.setIntercellSpacing(new Dimension(0, 0));

        // Custom Renderer to color rows!
        alertsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String type = (String) table.getModel().getValueAt(row, 2); // Column 2 is Alert Type

                if (isSelected) {
                    c.setBackground(new Color(220, 220, 255));
                } else {
                    if ("Expired".equals(type)) {
                        c.setBackground(new Color(255, 200, 200)); // Light Red for Expired
                    } else if ("Near Expiry".equals(type)) {
                        c.setBackground(new Color(255, 255, 200)); // Light Yellow for Near Expiry
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JTableHeader header = alertsTable.getTableHeader();
        header.setBackground(DARK_GREY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 35));

        JScrollPane scrollPane = new JScrollPane(alertsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // --- Helper Methods ---

    private JButton createBigButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    // --- Getters ---
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JTable getAlertsTable() { return alertsTable; }

    public void showMessage(String message) { JOptionPane.showMessageDialog(this, message); }

    public void refreshTable(List<Alert> alerts) {
        tableModel.setRowCount(0);
        for (Alert a : alerts) {
            tableModel.addRow(new Object[]{a.getAlertId(), a.getBloodId(), a.getAlertType(), a.getDateGenerated(), a.getStatus()});
        }
    }

    // --- Icons & Image Class ---

    private class ImagePanel extends JPanel {
        private Image img;
        public ImagePanel(String imagePath) {
            try { File f = new File(imagePath); if (f.exists()) img = ImageIO.read(f); else img = null; } catch (IOException e) { img = null; }
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            else { g.setColor(new Color(230,230,230)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }

    private static class BellIcon implements Icon {
        private int s; public BellIcon(int size) { this.s = size; }
        public int getIconWidth() { return s; } public int getIconHeight() { return s; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y); g2.setColor(Color.WHITE);
            g2.fillArc(5, 5, s-10, s-10, 0, 180); // Bell top
            g2.fillRect(5, s/2, s-10, s/3); // Bell body
            g2.fillOval(s/2-3, s-4, 6, 4); // Clapper
            g2.dispose();
        }
    }
}