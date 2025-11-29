package view;

import model.BloodUnit;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import java.util.Date;

public class BloodStockPage extends JFrame {

    private final Color PRIMARY_RED = new Color(190, 20, 20);
    private final Color DARK_GREY = new Color(50, 50, 50);
    private final Color LIGHT_BG = new Color(245, 248, 250);
    private final Color ICON_BG = new Color(255, 230, 230);
    private final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private JComboBox<String> bloodTypeComboBox;
    private JTextField quantityField;
    // NEW: Spinners for Dates
    private JSpinner donationDateSpinner;
    private JSpinner expiryDateSpinner;
    private JTextField donorIdField;

    private JButton saveButton, updateButton, deleteButton;
    private JTable stockTable;
    private DefaultTableModel tableModel;

    public BloodStockPage() {
        setTitle("Blood Stock Inventory");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        headerPanel.setBorder(new EmptyBorder(0, 30, 0, 30));
        JLabel titleLabel = new JLabel(" Blood Stock Inventory");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(350, 700));
        sidebarPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        sidebarPanel.add(createLabel("Blood Type"));
        bloodTypeComboBox = new JComboBox<>(new String[]{"--Select--", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodTypeComboBox.setFont(INPUT_FONT);
        bloodTypeComboBox.setBackground(Color.WHITE);
        sidebarPanel.add(new IconInputPanel(new DropIcon(), bloodTypeComboBox));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Quantity (Units)"));
        quantityField = createTextField();
        sidebarPanel.add(new IconInputPanel(new SigmaIcon(), quantityField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // DONATION DATE SPINNER
        sidebarPanel.add(createLabel("Donation Date"));
        donationDateSpinner = new JSpinner(new SpinnerDateModel());
        donationDateSpinner.setEditor(new JSpinner.DateEditor(donationDateSpinner, "yyyy-MM-dd"));
        donationDateSpinner.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.add(new IconInputPanel(new CalendarIcon(), donationDateSpinner));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // EXPIRY DATE SPINNER
        sidebarPanel.add(createLabel("Expiry Date"));
        expiryDateSpinner = new JSpinner(new SpinnerDateModel());
        expiryDateSpinner.setEditor(new JSpinner.DateEditor(expiryDateSpinner, "yyyy-MM-dd"));
        expiryDateSpinner.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.add(new IconInputPanel(new CalendarIcon(), expiryDateSpinner));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Donor ID (Optional)"));
        donorIdField = createTextField();
        sidebarPanel.add(new IconInputPanel(new UserIcon(), donorIdField));

        sidebarPanel.add(Box.createVerticalGlue());

        saveButton = createBigButton("ADD STOCK", PRIMARY_RED);
        sidebarPanel.add(saveButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        updateButton = createBigButton("UPDATE", new Color(255, 140, 0));
        sidebarPanel.add(updateButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        deleteButton = createBigButton("DELETE", DARK_GREY);
        sidebarPanel.add(deleteButton);
        add(sidebarPanel, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        ImagePanel heroImagePanel = new ImagePanel("side_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 250));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        String[] columnNames = {"Blood ID", "Blood Type", "Quantity", "Donation Date", "Expiry Date", "Donor ID"};
        tableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        stockTable = new JTable(tableModel);
        stockTable.setRowHeight(30);
        stockTable.getTableHeader().setBackground(DARK_GREY);
        stockTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    // --- UPDATED GETTERS FOR SPINNERS ---
    public JComboBox<String> getBloodTypeComboBox() { return bloodTypeComboBox; }
    public JTextField getQuantityField() { return quantityField; }
    public JTextField getDonorIdField() { return donorIdField; }

    // Get Date Objects directly
    public java.util.Date getDonationDate() { return (java.util.Date) donationDateSpinner.getValue(); }
    public java.util.Date getExpiryDate() { return (java.util.Date) expiryDateSpinner.getValue(); }

    // Setters for populating form
    public void setDonationDate(java.util.Date d) { donationDateSpinner.setValue(d); }
    public void setExpiryDate(java.util.Date d) { expiryDateSpinner.setValue(d); }

    public String getSelectedBloodType() { return (String) bloodTypeComboBox.getSelectedItem(); }
    public String getQuantity() { return quantityField.getText(); }
    public String getDonorId() { return donorIdField.getText(); }

    public JButton getSaveButton() { return saveButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JTable getStockTable() { return stockTable; }

    public void showMessage(String m) { JOptionPane.showMessageDialog(this, m); }
    public void clearForm() {
        bloodTypeComboBox.setSelectedIndex(0);
        quantityField.setText("");
        donationDateSpinner.setValue(new Date());
        expiryDateSpinner.setValue(new Date());
        donorIdField.setText("");
    }
    public void refreshTable(List<BloodUnit> units) { tableModel.setRowCount(0); for (BloodUnit u : units) tableModel.addRow(new Object[]{u.getBloodId(), u.getBloodType(), u.getQuantity(), u.getDonationDate(), u.getExpiryDate(), u.getDonorId()}); }

    private JLabel createLabel(String t) { JLabel l=new JLabel(t); l.setFont(new Font("SansSerif",Font.BOLD,12)); l.setForeground(Color.GRAY); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l; }
    private JTextField createTextField() { JTextField t=new JTextField(); t.setFont(INPUT_FONT); t.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); return t; }
    private JButton createBigButton(String t, Color bg) { JButton b=new JButton(t); b.setFont(new Font("SansSerif",Font.BOLD,14)); b.setBackground(bg); b.setForeground(Color.WHITE); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,45)); b.setAlignmentX(Component.CENTER_ALIGNMENT); return b; }

    private class ImagePanel extends JPanel {
        private Image img;
        public ImagePanel(String p) { try { File f=new File(p); if(f.exists()) img=ImageIO.read(f); } catch(Exception e){} }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                int panelW = getWidth(); int panelH = getHeight(); int imgW = img.getWidth(null); int imgH = img.getHeight(null);
                if (imgW > 0 && imgH > 0) {
                    double imgAspect = (double) imgW / imgH; double panelAspect = (double) panelW / panelH;
                    int drawW, drawH, x, y;
                    if (panelAspect > imgAspect) { drawH = panelH; drawW = (int) (panelH * imgAspect); y = 0; x = (panelW - drawW) / 2; }
                    else { drawW = panelW; drawH = (int) (panelW / imgAspect); x = 0; y = (panelH - drawH) / 2; }
                    g.drawImage(img, x, y, drawW, drawH, this);
                }
            } else { g.setColor(new Color(240, 240, 245)); g.fillRect(0,0,getWidth(),getHeight()); g.setColor(Color.GRAY); g.drawString("Add 'side_image.jpg' to project root", getWidth()/2-100, getHeight()/2); }
        }
    }

    private class IconInputPanel extends JPanel { public IconInputPanel(Icon i, JComponent c) { setLayout(new BorderLayout()); setBackground(Color.WHITE); setBorder(BorderFactory.createLineBorder(new Color(200,200,200))); setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); JLabel l=new JLabel(i); l.setOpaque(true); l.setBackground(ICON_BG); l.setPreferredSize(new Dimension(40,40)); l.setHorizontalAlignment(SwingConstants.CENTER); add(l,BorderLayout.WEST); add(c,BorderLayout.CENTER); } }
    private static class DropIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillOval(x+5,y+9,10,10); int[] px={x+5,x+15,x+10}; int[] py={y+14,y+14,y+4}; g.fillPolygon(px,py,3); } }
    private static class CalendarIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.drawRect(x+3,y+5,14,12); g.fillRect(x+3,y+5,14,3); } }
    private static class UserIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillOval(x+6,y+4,8,8); g.fillArc(x+4,y+12,12,8,0,180); } }
    private static class SigmaIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.drawPolyline(new int[]{x+15,x+5,x+15,x+5,x+15}, new int[]{y+5,y+5,y+10,y+15,y+15}, 5); } }
}