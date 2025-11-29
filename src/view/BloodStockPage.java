package view;

import model.BloodUnit;

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
 * View class for the Blood Stock Page.
 * "PRO" DASHBOARD DESIGN VERSION
 * Features: Code-drawn icons, Sidebar Form, and HERO IMAGE support (with Debugging).
 */
public class BloodStockPage extends JFrame {

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
    private JTextField donationDateField;
    private JTextField expiryDateField;
    private JTextField donorIdField;

    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;

    private JTable stockTable;
    private DefaultTableModel tableModel;

    public BloodStockPage() {
        setTitle("Blood Stock Inventory");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. --- TOP HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        headerPanel.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel titleLabel = new JLabel(" Blood Stock Inventory");
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
        sidebarPanel.add(createLabel("Blood Type"));
        String[] bloodTypes = {"--Select--", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodTypeComboBox = new JComboBox<>(bloodTypes);
        bloodTypeComboBox.setFont(INPUT_FONT);
        bloodTypeComboBox.setBackground(Color.WHITE);
        sidebarPanel.add(new IconInputPanel(new DropIcon(), bloodTypeComboBox));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Quantity (Units)"));
        quantityField = createTextField();
        sidebarPanel.add(new IconInputPanel(new SigmaIcon(), quantityField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Donation Date (YYYY-MM-DD)"));
        donationDateField = createTextField();
        sidebarPanel.add(new IconInputPanel(new CalendarIcon(), donationDateField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Expiry Date (YYYY-MM-DD)"));
        expiryDateField = createTextField();
        sidebarPanel.add(new IconInputPanel(new CalendarIcon(), expiryDateField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Donor ID"));
        donorIdField = createTextField();
        sidebarPanel.add(new IconInputPanel(new UserIcon(), donorIdField));

        sidebarPanel.add(Box.createVerticalGlue());

        // -- Buttons --
        saveButton = createBigButton("ADD STOCK", PRIMARY_RED);
        sidebarPanel.add(saveButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        updateButton = createBigButton("UPDATE SELECTED", new Color(255, 140, 0));
        sidebarPanel.add(updateButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        deleteButton = createBigButton("DELETE SELECTED", DARK_GREY);
        sidebarPanel.add(deleteButton);

        add(sidebarPanel, BorderLayout.WEST);

        // 3. --- RIGHT MAIN AREA (IMAGE + TABLE) ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        // A. HERO IMAGE PANEL (Top of Main Area)
        // This panel tries to load 'side_image.jpg'. If missing, draws a placeholder.
        ImagePanel heroImagePanel = new ImagePanel("side_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 250)); // Height 250px
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        // B. TABLE PANEL (Center of Main Area)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(LIGHT_BG);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel tableTitle = new JLabel("Current Inventory Status");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableTitle.setForeground(Color.DARK_GRAY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columnNames = {"Blood ID", "Blood Type", "Quantity", "Donation Date", "Expiry Date", "Donor ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        stockTable = new JTable(tableModel);

        // Table Style
        stockTable.setRowHeight(30);
        stockTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        stockTable.setShowGrid(false);
        stockTable.setIntercellSpacing(new Dimension(0, 0));
        stockTable.setSelectionBackground(new Color(255, 220, 220));
        stockTable.setSelectionForeground(Color.BLACK);

        JTableHeader header = stockTable.getTableHeader();
        header.setBackground(DARK_GREY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < stockTable.getColumnCount(); i++) {
            stockTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // --- Helper Methods & Classes ---

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
    public String getDonationDate() { return donationDateField.getText(); }
    public String getExpiryDate() { return expiryDateField.getText(); }
    public String getDonorId() { return donorIdField.getText(); }
    public JButton getSaveButton() { return saveButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JTable getStockTable() { return stockTable; }

    public void showMessage(String message) { JOptionPane.showMessageDialog(this, message); }
    public void clearForm() {
        bloodTypeComboBox.setSelectedIndex(0);
        quantityField.setText("");
        donationDateField.setText("");
        expiryDateField.setText("");
        donorIdField.setText("");
    }
    public void refreshTable(List<BloodUnit> units) {
        tableModel.setRowCount(0);
        for (BloodUnit unit : units) {
            tableModel.addRow(new Object[]{unit.getBloodId(), unit.getBloodType(), unit.getQuantity(), unit.getDonationDate(), unit.getExpiryDate(), unit.getDonorId()});
        }
    }

    // ==========================================================
    //  IMAGE & ICON CLASSES
    // ==========================================================

    /**
     * A Custom Panel that displays an image.
     * DEBUG VERSION: Prints path info to console to help you find the file!
     */
    private class ImagePanel extends JPanel {
        private Image img;

        public ImagePanel(String imagePath) {
            try {
                File f = new File(imagePath);

                // DEBUGGING: Print where Java is looking
                System.out.println("----------------------------------------------");
                System.out.println("DEBUG: Looking for image at: " + f.getAbsolutePath());
                System.out.println("----------------------------------------------");

                if (f.exists()) {
                    img = ImageIO.read(f);
                } else {
                    img = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                img = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Placeholder if missing
                g.setColor(new Color(230, 230, 230));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.GRAY);
                g.setFont(new Font("SansSerif", Font.BOLD, 16));
                g.drawString("Image not found. Check Console for path!", 50, getHeight()/2);
            }
        }
    }

    // Custom Icon Panel Wrapper
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

    // --- CODE DRAWN ICONS ---
    private static class HeaderIcon implements Icon {
        private int s; public HeaderIcon(int size) { this.s = size; }
        public int getIconWidth() { return s; } public int getIconHeight() { return s; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y); g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2));
            g2.drawOval(2, 2, s-4, s-4); g2.fillOval(s/2-6, s/2-4, 12, 12);
            int[] px = {s/2-6, s/2+6, s/2}; int[] py = {s/2+2, s/2+2, 8}; g2.fillPolygon(px, py, 3); g2.dispose();
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
    private static class UserIcon implements Icon {
        public int getIconWidth() { return 20; } public int getIconHeight() { return 20; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y); g2.setColor(new Color(190, 20, 20)); g2.fillOval(6, 4, 8, 8); g2.fillArc(4, 12, 12, 8, 0, 180); g2.dispose();
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