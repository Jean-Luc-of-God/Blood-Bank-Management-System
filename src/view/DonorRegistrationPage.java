package view;

import model.Donor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class DonorRegistrationPage extends JFrame {

    private final Color PRIMARY_RED = new Color(190, 20, 20);
    private final Color DARK_GREY = new Color(50, 50, 50);
    private final Color LIGHT_BG = new Color(245, 248, 250);
    private final Color ICON_BG = new Color(255, 230, 230);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private JTextField nameField;
    private JTextField contactField;
    private JComboBox<String> bloodTypeComboBox;

    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;
    // NEW BUTTON
    private JButton donateButton;

    private JTable donorTable;
    private DefaultTableModel tableModel;

    public DonorRegistrationPage() {
        setTitle("Donor Registration & Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        JLabel titleLabel = new JLabel(" Donor Registration");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(new HeaderIcon(40));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. --- SIDEBAR FORM ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(350, 700));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        sidebarPanel.add(createLabel("Full Name"));
        nameField = createTextField();
        sidebarPanel.add(new IconInputPanel(new UserIcon(), nameField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Contact (Phone/Email)"));
        contactField = createTextField();
        sidebarPanel.add(new IconInputPanel(new PhoneIcon(), contactField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Blood Type"));
        String[] bloodTypes = {"--Select--", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodTypeComboBox = new JComboBox<>(bloodTypes);
        bloodTypeComboBox.setFont(INPUT_FONT);
        bloodTypeComboBox.setBackground(Color.WHITE);
        sidebarPanel.add(new IconInputPanel(new DropIcon(), bloodTypeComboBox));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(Box.createVerticalGlue());

        // -- Buttons --
        saveButton = createBigButton("REGISTER DONOR", PRIMARY_RED);
        sidebarPanel.add(saveButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        updateButton = createBigButton("UPDATE SELECTED", new Color(255, 140, 0));
        sidebarPanel.add(updateButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // NEW DONATION BUTTON
        donateButton = createBigButton("RECORD DONATION (+1 Unit)", new Color(46, 139, 87)); // Green
        sidebarPanel.add(donateButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        deleteButton = createBigButton("DELETE SELECTED", DARK_GREY);
        sidebarPanel.add(deleteButton);

        add(sidebarPanel, BorderLayout.WEST);

        // 3. --- MAIN AREA ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        ImagePanel heroImagePanel = new ImagePanel("donor_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 250));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(LIGHT_BG);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tableTitle = new JLabel("Registered Donors Directory");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableTitle.setForeground(Color.DARK_GRAY);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columnNames = {"Donor ID", "Name", "Contact", "Blood Type", "Date Registered"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        donorTable = new JTable(tableModel);

        donorTable.setRowHeight(30);
        donorTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        donorTable.setShowGrid(false);
        donorTable.setIntercellSpacing(new Dimension(0, 0));
        donorTable.setSelectionBackground(new Color(255, 220, 220));
        donorTable.setSelectionForeground(Color.BLACK);

        JTableHeader header = donorTable.getTableHeader();
        header.setBackground(DARK_GREY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < donorTable.getColumnCount(); i++) {
            donorTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // --- GETTERS ---
    public String getDonorName() { return nameField.getText(); }
    public String getDonorContact() { return contactField.getText(); }
    public String getSelectedBloodType() { return (String) bloodTypeComboBox.getSelectedItem(); }
    public JButton getSaveButton() { return saveButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getDonateButton() { return donateButton; } // NEW GETTER
    public JTable getDonorTable() { return donorTable; }

    public JTextField getNameField() { return nameField; }
    public JTextField getContactField() { return contactField; }
    public JComboBox<String> getBloodTypeComboBox() { return bloodTypeComboBox; }

    public void showMessage(String message) { JOptionPane.showMessageDialog(this, message); }

    public void clearForm() {
        nameField.setText("");
        contactField.setText("");
        bloodTypeComboBox.setSelectedIndex(0);
    }

    public void refreshTable(List<Donor> donors) {
        tableModel.setRowCount(0);
        for (Donor d : donors) {
            tableModel.addRow(new Object[]{d.getDonorId(), d.getName(), d.getContact(), d.getBloodType(), d.getDateRegistered()});
        }
    }

    // --- Helper Methods ---
    private JLabel createLabel(String text) { JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.BOLD, 12)); l.setForeground(Color.GRAY); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l; }
    private JTextField createTextField() { JTextField tf = new JTextField(); tf.setFont(INPUT_FONT); tf.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); return tf; }
    private JButton createBigButton(String text, Color bg) { JButton b = new JButton(text); b.setFont(new Font("SansSerif", Font.BOLD, 14)); b.setBackground(bg); b.setForeground(Color.WHITE); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); b.setAlignmentX(Component.CENTER_ALIGNMENT); return b; }

    private class ImagePanel extends JPanel {
        private Image img;
        public ImagePanel(String imagePath) {
            try { File f = new File(imagePath); if (f.exists()) img = ImageIO.read(f); else img = null; } catch (IOException e) { img = null; }
        }
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
            } else {
                g.setColor(new Color(240, 240, 245)); g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.GRAY); g.drawString("Add 'donor_image.jpg' to project root", getWidth()/2 - 100, getHeight()/2);
            }
        }
    }

    private class IconInputPanel extends JPanel { public IconInputPanel(Icon i, JComponent c) { setLayout(new BorderLayout()); setBackground(Color.WHITE); setBorder(BorderFactory.createLineBorder(new Color(200,200,200),1)); setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); JLabel l=new JLabel(i); l.setOpaque(true); l.setBackground(ICON_BG); l.setPreferredSize(new Dimension(40,40)); l.setHorizontalAlignment(SwingConstants.CENTER); add(l,BorderLayout.WEST); add(c,BorderLayout.CENTER); } }
    private static class HeaderIcon implements Icon { int s; public HeaderIcon(int s){this.s=s;} public int getIconWidth(){return s;} public int getIconHeight(){return s;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(Color.WHITE); g.drawOval(x,y,s,s); g.fillOval(x+10,y+10,s-20,s-20); } }
    private static class UserIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillOval(x+6,y+4,8,8); g.fillArc(x+4,y+12,12,8,0,180); } }
    private static class PhoneIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillRect(x+7,y+4,6,12); } }
    private static class DropIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillOval(x+5,y+9,10,10); int[] px={x+5,x+15,x+10}; int[] py={y+14,y+14,y+4}; g.fillPolygon(px,py,3); } }
}