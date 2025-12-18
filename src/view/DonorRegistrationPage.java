package view;

import model.Donor;
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
    private JSpinner dateSpinner;
    private JButton saveButton, updateButton, deleteButton, donateButton, backButton;
    private JTable donorTable;
    private DefaultTableModel tableModel;

    public DonorRegistrationPage() {
        setTitle("Donor Registration & Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(1200, 80)); // Slightly taller for better spacing
        headerPanel.setBorder(new EmptyBorder(0, 30, 0, 30));

        // --- INDUSTRY STANDARD BACK BUTTON ---
        backButton = new JButton(" DASHBOARD");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        backButton.setForeground(PRIMARY_RED);
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setIcon(new BackIcon(14, PRIMARY_RED));
        // Match the Logout button style exactly
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Centering Wrapper
        JPanel btnWrapper = new JPanel(new GridBagLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.add(backButton);
        headerPanel.add(btnWrapper, BorderLayout.WEST); // Place on LEFT

        // Title (Centered)
        JLabel titleLabel = new JLabel("Donor Registration", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Spacer for balance
        JPanel spacer = new JPanel(); spacer.setOpaque(false); spacer.setPreferredSize(new Dimension(120, 10));
        headerPanel.add(spacer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // SIDEBAR
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(350, 700));
        sidebarPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        sidebarPanel.add(createLabel("Full Name"));
        nameField = createTextField();
        sidebarPanel.add(new IconInputPanel(new UserIcon(), nameField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Contact (10 Digits)"));
        contactField = createTextField();
        sidebarPanel.add(new IconInputPanel(new PhoneIcon(), contactField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Blood Type"));
        bloodTypeComboBox = new JComboBox<>(new String[]{"--Select--", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodTypeComboBox.setFont(INPUT_FONT);
        bloodTypeComboBox.setBackground(Color.WHITE);
        sidebarPanel.add(new IconInputPanel(new DropIcon(), bloodTypeComboBox));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Registration Date"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.add(new IconInputPanel(new CalendarIcon(), dateSpinner));

        sidebarPanel.add(Box.createVerticalGlue());

        saveButton = createBigButton("REGISTER DONOR", PRIMARY_RED);
        sidebarPanel.add(saveButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        updateButton = createBigButton("UPDATE SELECTED", new Color(255, 140, 0));
        sidebarPanel.add(updateButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        donateButton = createBigButton("RECORD DONATION (+1)", new Color(46, 139, 87));
        sidebarPanel.add(donateButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        deleteButton = createBigButton("DELETE SELECTED", DARK_GREY);
        sidebarPanel.add(deleteButton);
        add(sidebarPanel, BorderLayout.WEST);

        // MAIN
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);
        ImagePanel heroImagePanel = new ImagePanel("donor_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 250));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(LIGHT_BG);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel tableTitle = new JLabel("Registered Donors Directory");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableTitle.setForeground(Color.DARK_GRAY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Blood Type", "Date"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        donorTable = new JTable(tableModel);
        donorTable.setRowHeight(30);
        donorTable.getTableHeader().setBackground(DARK_GREY);
        donorTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    // Getters
    public JTextField getNameField() { return nameField; }
    public JTextField getContactField() { return contactField; }
    public JComboBox<String> getBloodTypeComboBox() { return bloodTypeComboBox; }
    public java.util.Date getSelectedDate() { return (java.util.Date) dateSpinner.getValue(); }
    public void setSelectedDate(java.util.Date d) { dateSpinner.setValue(d); }
    public String getDonorName() { return nameField.getText(); }
    public String getDonorContact() { return contactField.getText(); }
    public String getSelectedBloodType() { return (String) bloodTypeComboBox.getSelectedItem(); }
    public JButton getSaveButton() { return saveButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getDonateButton() { return donateButton; }
    public JButton getBackButton() { return backButton; }
    public JTable getDonorTable() { return donorTable; }
    public void showMessage(String m) { JOptionPane.showMessageDialog(this, m); }
    public void clearForm() { nameField.setText(""); contactField.setText(""); bloodTypeComboBox.setSelectedIndex(0); dateSpinner.setValue(new java.util.Date()); }
    public void refreshTable(List<Donor> donors) { tableModel.setRowCount(0); for (Donor d : donors) tableModel.addRow(new Object[]{d.getDonorId(), d.getName(), d.getContact(), d.getBloodType(), d.getDateRegistered()}); }

    private JLabel createLabel(String t) { JLabel l=new JLabel(t); l.setFont(new Font("SansSerif",Font.BOLD,12)); l.setForeground(Color.GRAY); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l; }
    private JTextField createTextField() { JTextField t=new JTextField(); t.setFont(INPUT_FONT); t.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); return t; }
    private JButton createBigButton(String t, Color bg) { JButton b=new JButton(t); b.setFont(new Font("SansSerif",Font.BOLD,14)); b.setBackground(bg); b.setForeground(Color.WHITE); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); b.setAlignmentX(Component.CENTER_ALIGNMENT); return b; }

    private class ImagePanel extends JPanel {
        private Image img; public ImagePanel(String p) { try { File f=new File(p); if(f.exists()) img=ImageIO.read(f); } catch(Exception e){} }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); if(img!=null) { int w=getWidth(), h=getHeight(), iw=img.getWidth(null), ih=img.getHeight(null); if(iw>0&&ih>0) { double r=(double)iw/ih, pr=(double)w/h; int dw, dh, x, y; if(pr>r) { dh=h; dw=(int)(h*r); x=(w-dw)/2; y=0; } else { dw=w; dh=(int)(w/r); x=0; y=(h-dh)/2; } g.drawImage(img,x,y,dw,dh,this); } } else { g.setColor(new Color(240,240,245)); g.fillRect(0,0,getWidth(),getHeight()); g.setColor(Color.GRAY); g.drawString("Add 'donor_image.jpg'", getWidth()/2-80, getHeight()/2); } }
    }
    private class IconInputPanel extends JPanel { public IconInputPanel(Icon i, JComponent c) { setLayout(new BorderLayout()); setBackground(Color.WHITE); setBorder(BorderFactory.createLineBorder(new Color(200,200,200),1)); setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); JLabel l=new JLabel(i); l.setOpaque(true); l.setBackground(ICON_BG); l.setPreferredSize(new Dimension(40,40)); l.setHorizontalAlignment(SwingConstants.CENTER); add(l,BorderLayout.WEST); add(c,BorderLayout.CENTER); } }
    private static class UserIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillOval(x+6,y+4,8,8); g.fillArc(x+4,y+12,12,8,0,180); } }
    private static class PhoneIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillRect(x+7,y+4,6,12); } }
    private static class DropIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillOval(x+5,y+9,10,10); int[] px={x+5,x+15,x+10}; int[] py={y+14,y+14,y+4}; g.fillPolygon(px,py,3); } }
    private static class CalendarIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.drawRect(x+3,y+5,14,12); g.fillRect(x+3,y+5,14,3); } }

    // NEW IMPROVED BACK ICON
    private static class BackIcon implements Icon {
        int s; Color c; public BackIcon(int s, Color c){this.s=s; this.c=c;} public int getIconWidth(){return s;} public int getIconHeight(){return s;}
        public void paintIcon(Component cp,Graphics g,int x,int y){
            Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x,y); g2.setColor(c); g2.setStroke(new BasicStroke(2));
            // Draw a proper arrow
            g2.drawLine(s, s/2, 0, s/2); // Horizontal line
            g2.drawLine(0, s/2, s/2, 0); // Upper wing
            g2.drawLine(0, s/2, s/2, s); // Lower wing
            g2.dispose();
        }
    }
}