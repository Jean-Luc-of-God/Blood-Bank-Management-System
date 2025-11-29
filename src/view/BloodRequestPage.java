package view;

import model.BloodRequest;

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
 * View class for the Blood Request Page.
 * "PRO" DASHBOARD DESIGN VERSION
 * Features: Sidebar Form, Hero Image, and Status Tracking in Table.
 */
public class BloodRequestPage extends JFrame {

    // --- Theme Colors ---
    private final Color PRIMARY_RED = new Color(190, 20, 20);
    private final Color DARK_GREY = new Color(50, 50, 50);
    private final Color LIGHT_BG = new Color(245, 248, 250);
    private final Color ICON_BG = new Color(255, 230, 230);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);
    private final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // --- Components ---
    private JComboBox<String> bloodTypeComboBox;
    private JTextField quantityField;
    private JTextField requestDateField;

    private JButton submitButton;
    private JButton fulfillButton; // Special button to mark request as "Done"
    private JButton deleteButton;

    private JTable requestTable;
    private DefaultTableModel tableModel;

    public BloodRequestPage() {
        setTitle("Hospital Blood Requests");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. --- TOP HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        headerPanel.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel titleLabel = new JLabel(" Blood Request Portal");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(new HeaderIcon(40));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. --- LEFT SIDEBAR (FORM) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(350, 700));
        sidebarPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // -- Form Fields --
        sidebarPanel.add(createLabel("Requested Blood Type"));
        String[] bloodTypes = {"--Select--", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodTypeComboBox = new JComboBox<>(bloodTypes);
        bloodTypeComboBox.setFont(INPUT_FONT);
        bloodTypeComboBox.setBackground(Color.WHITE);
        sidebarPanel.add(new IconInputPanel(new DropIcon(), bloodTypeComboBox));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Quantity Needed (Units)"));
        quantityField = createTextField();
        sidebarPanel.add(new IconInputPanel(new SigmaIcon(), quantityField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Date Required (YYYY-MM-DD)"));
        requestDateField = createTextField();
        sidebarPanel.add(new IconInputPanel(new CalendarIcon(), requestDateField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(Box.createVerticalGlue());

        // -- Buttons --
        submitButton = createBigButton("SUBMIT REQUEST", PRIMARY_RED);
        sidebarPanel.add(submitButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        fulfillButton = createBigButton("MARK FULFILLED", new Color(46, 139, 87)); // Green
        sidebarPanel.add(fulfillButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        deleteButton = createBigButton("DELETE REQUEST", DARK_GREY);
        sidebarPanel.add(deleteButton);

        add(sidebarPanel, BorderLayout.WEST);

        // 3. --- RIGHT MAIN AREA (IMAGE + TABLE) ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        // A. HERO IMAGE PANEL
        // Looks for 'request_image.jpg' in project root
        ImagePanel heroImagePanel = new ImagePanel("request_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 250));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        // B. TABLE PANEL
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(LIGHT_BG);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel tableTitle = new JLabel("Pending & Active Requests");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableTitle.setForeground(Color.DARK_GRAY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columnNames = {"Req ID", "Blood Type", "Quantity", "Date Needed", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        requestTable = new JTable(tableModel);

        // Table Style
        requestTable.setRowHeight(30);
        requestTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        requestTable.setShowGrid(false);
        requestTable.setIntercellSpacing(new Dimension(0, 0));
        requestTable.setSelectionBackground(new Color(255, 220, 220));
        requestTable.setSelectionForeground(Color.BLACK);

        JTableHeader header = requestTable.getTableHeader();
        header.setBackground(DARK_GREY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < requestTable.getColumnCount(); i++) {
            requestTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // --- Helper Methods ---

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(Color.GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(INPUT_FONT);
        tf.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return tf;
    }

    private JButton createBigButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    // --- Getters ---
    public String getSelectedBloodType() { return (String) bloodTypeComboBox.getSelectedItem(); }
    public JComboBox<String> getBloodTypeComboBox() { return bloodTypeComboBox; }
    public String getQuantity() { return quantityField.getText(); }
    public String getRequestDate() { return requestDateField.getText(); }

    public JButton getSubmitButton() { return submitButton; }
    public JButton getFulfillButton() { return fulfillButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JTable getRequestTable() { return requestTable; }

    public void showMessage(String message) { JOptionPane.showMessageDialog(this, message); }
    public void clearForm() {
        bloodTypeComboBox.setSelectedIndex(0);
        quantityField.setText("");
        requestDateField.setText("");
    }
    public void refreshTable(List<BloodRequest> requests) {
        tableModel.setRowCount(0);
        for (BloodRequest req : requests) {
            String status = req.isFulfilled() ? "FULFILLED" : "PENDING";
            tableModel.addRow(new Object[]{
                    req.getRequestId(), req.getBloodType(), req.getQuantity(),
                    req.getRequestDate(), status
            });
        }
    }

    // ==========================================================
    //  IMAGE & ICON CLASSES (Re-used for consistency)
    // ==========================================================

    private class ImagePanel extends JPanel {
        private Image img;
        public ImagePanel(String imagePath) {
            try {
                File f = new File(imagePath);
                // Debug print removed for cleanliness, add back if image issues persist
                if (f.exists()) img = ImageIO.read(f);
                else img = null;
            } catch (IOException e) { img = null; }
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            else {
                g.setColor(new Color(230, 230, 230));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.GRAY);
                g.setFont(new Font("SansSerif", Font.BOLD, 16));
                g.drawString("Add 'request_image.jpg' to project folder", 50, getHeight()/2);
            }
        }
    }

    private class IconInputPanel extends JPanel {
        public IconInputPanel(Icon icon, JComponent inputField) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setPreferredSize(new Dimension(300, 40));
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setOpaque(true);
            iconLabel.setBackground(ICON_BG);
            iconLabel.setPreferredSize(new Dimension(40, 40));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(iconLabel, BorderLayout.WEST);
            add(inputField, BorderLayout.CENTER);
        }
    }

    // --- ICONS ---
    private static class HeaderIcon implements Icon {
        private int s; public HeaderIcon(int size) { this.s = size; }
        public int getIconWidth() { return s; } public int getIconHeight() { return s; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y); g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(2, 5, s-4, s-10, 5, 5); // Clipboard shape
            g2.drawLine(8, 12, s-8, 12); g2.drawLine(8, 18, s-8, 18); g2.dispose();
        }
    }
    private static class DropIcon implements Icon {
        public int getIconWidth() { return 20; } public int getIconHeight() { return 20; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y); g2.setColor(new Color(190, 20, 20)); g2.fillOval(5, 9, 10, 10);
            g2.fillPolygon(new int[]{5, 15, 10}, new int[]{14, 14, 4}, 3); g2.dispose();
        }
    }
    private static class CalendarIcon implements Icon {
        public int getIconWidth() { return 20; } public int getIconHeight() { return 20; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.translate(x, y); g2.setColor(new Color(190, 20, 20));
            g2.drawRect(3, 5, 14, 12); g2.fillRect(3, 5, 14, 3); g2.drawLine(6, 3, 6, 5); g2.drawLine(14, 3, 14, 5); g2.dispose();
        }
    }
    private static class SigmaIcon implements Icon {
        public int getIconWidth() { return 20; } public int getIconHeight() { return 20; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y); g2.setColor(new Color(190, 20, 20)); g2.setStroke(new BasicStroke(2));
            g2.drawPolyline(new int[]{15, 5, 15, 5, 15}, new int[]{5, 5, 10, 15, 15}, 5); g2.dispose();
        }
    }
}