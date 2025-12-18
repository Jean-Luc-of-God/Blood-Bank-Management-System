package view;

import model.BloodRequest;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import java.util.Date;

public class BloodRequestPage extends JFrame {

    private final Color PRIMARY_RED = new Color(190, 20, 20);
    private final Color DARK_GREY = new Color(50, 50, 50);
    private final Color LIGHT_BG = new Color(245, 248, 250);
    private final Color ICON_BG = new Color(255, 230, 230);
    private final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private JComboBox<String> bloodTypeComboBox;
    private JTextField quantityField;
    private JSpinner requestDateSpinner;
    private JButton submitButton, fulfillButton, deleteButton, backButton;
    private JTable requestTable;
    private DefaultTableModel tableModel;

    public BloodRequestPage() {
        setTitle("Hospital Blood Requests");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        headerPanel.setBorder(new EmptyBorder(0, 30, 0, 30));

        // BACK BUTTON
        backButton = new JButton(" DASHBOARD");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        backButton.setForeground(PRIMARY_RED);
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setIcon(new BackIcon(14, PRIMARY_RED));
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel btnWrapper = new JPanel(new GridBagLayout()); btnWrapper.setOpaque(false); btnWrapper.add(backButton);
        headerPanel.add(btnWrapper, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Blood Request Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel spacer = new JPanel(); spacer.setOpaque(false); spacer.setPreferredSize(new Dimension(135,10));
        headerPanel.add(spacer, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(300, 700));
        sidebarPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        sidebarPanel.add(createLabel("Blood Type"));
        bloodTypeComboBox = new JComboBox<>(new String[]{"--Select--", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodTypeComboBox.setFont(INPUT_FONT);
        bloodTypeComboBox.setBackground(Color.WHITE);
        sidebarPanel.add(new IconInputPanel(new DropIcon(), bloodTypeComboBox));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Quantity"));
        quantityField = createTextField();
        sidebarPanel.add(new IconInputPanel(new SigmaIcon(), quantityField));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebarPanel.add(createLabel("Date Needed"));
        requestDateSpinner = new JSpinner(new SpinnerDateModel());
        requestDateSpinner.setEditor(new JSpinner.DateEditor(requestDateSpinner, "yyyy-MM-dd"));
        requestDateSpinner.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.add(new IconInputPanel(new CalendarIcon(), requestDateSpinner));
        sidebarPanel.add(Box.createVerticalGlue());

        submitButton = createBigButton("SUBMIT REQUEST", PRIMARY_RED);
        sidebarPanel.add(submitButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        fulfillButton = createBigButton("MARK FULFILLED", new Color(46, 139, 87));
        sidebarPanel.add(fulfillButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        deleteButton = createBigButton("DELETE REQUEST", DARK_GREY);
        sidebarPanel.add(deleteButton);
        add(sidebarPanel, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        ImagePanel heroImagePanel = new ImagePanel("request_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 250));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Req ID", "Blood Type", "Quantity", "Date Needed", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        requestTable = new JTable(tableModel);
        requestTable.setRowHeight(30);
        requestTable.getTableHeader().setBackground(DARK_GREY);
        requestTable.getTableHeader().setForeground(Color.WHITE);
        mainPanel.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    public String getSelectedBloodType() { return (String) bloodTypeComboBox.getSelectedItem(); }
    public String getQuantity() { return quantityField.getText(); }
    public java.util.Date getRequestDate() { return (java.util.Date) requestDateSpinner.getValue(); }
    public void setRequestDate(java.util.Date d) { requestDateSpinner.setValue(d); }

    public JButton getSubmitButton() { return submitButton; }
    public JButton getFulfillButton() { return fulfillButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getBackButton() { return backButton; }
    public JTable getRequestTable() { return requestTable; }

    public void showMessage(String m) { JOptionPane.showMessageDialog(this, m); }
    public void clearForm() { quantityField.setText(""); requestDateSpinner.setValue(new Date()); }
    public void refreshTable(List<BloodRequest> list) { tableModel.setRowCount(0); for(BloodRequest r:list) tableModel.addRow(new Object[]{r.getRequestId(), r.getBloodType(), r.getQuantity(), r.getRequestDate(), r.isFulfilled()?"Fulfilled":"Pending"}); }

    private JLabel createLabel(String t) { JLabel l=new JLabel(t); l.setFont(new Font("SansSerif",Font.BOLD,12)); l.setForeground(Color.GRAY); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l; }
    private JTextField createTextField() { JTextField t=new JTextField(); t.setFont(INPUT_FONT); t.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); return t; }
    private JButton createBigButton(String t, Color bg) { JButton b=new JButton(t); b.setFont(new Font("SansSerif",Font.BOLD,14)); b.setBackground(bg); b.setForeground(Color.WHITE); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,45)); b.setAlignmentX(Component.CENTER_ALIGNMENT); return b; }

    private class ImagePanel extends JPanel {
        private Image img; public ImagePanel(String p) { try { File f=new File(p); if(f.exists()) img=ImageIO.read(f); } catch(Exception e){} }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); if(img!=null) { int w=getWidth(), h=getHeight(), iw=img.getWidth(null), ih=img.getHeight(null); if(iw>0&&ih>0) { double r=(double)iw/ih, pr=(double)w/h; int dw, dh, x, y; if(pr>r) { dh=h; dw=(int)(h*r); x=(w-dw)/2; y=0; } else { dw=w; dh=(int)(w/r); x=0; y=(h-dh)/2; } g.drawImage(img,x,y,dw,dh,this); } } else { g.setColor(new Color(240,240,245)); g.fillRect(0,0,getWidth(),getHeight()); g.setColor(Color.GRAY); g.drawString("Add 'request_image.jpg'", getWidth()/2-80, getHeight()/2); } }
    }
    private class IconInputPanel extends JPanel { public IconInputPanel(Icon i, JComponent c) { setLayout(new BorderLayout()); setBackground(Color.WHITE); setBorder(BorderFactory.createLineBorder(new Color(200,200,200))); setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); JLabel l=new JLabel(i); l.setOpaque(true); l.setBackground(ICON_BG); l.setPreferredSize(new Dimension(40,40)); l.setHorizontalAlignment(SwingConstants.CENTER); add(l,BorderLayout.WEST); add(c,BorderLayout.CENTER); } }
    private static class DropIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.fillOval(x+5,y+9,10,10); int[] px={x+5,x+15,x+10}; int[] py={y+14,y+14,y+4}; g.fillPolygon(px,py,3); } }
    private static class CalendarIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.drawRect(x+3,y+5,14,12); g.fillRect(x+3,y+5,14,3); } }
    private static class SigmaIcon implements Icon { public int getIconWidth(){return 20;} public int getIconHeight(){return 20;} public void paintIcon(Component c,Graphics g,int x,int y){ g.setColor(new Color(190,20,20)); g.drawPolyline(new int[]{x+15,x+5,x+15,x+5,x+15}, new int[]{y+5,y+5,y+10,y+15,y+15}, 5); } }
    private static class BackIcon implements Icon { int s; Color c; public BackIcon(int s, Color c){this.s=s; this.c=c;} public int getIconWidth(){return s;} public int getIconHeight(){return s;} public void paintIcon(Component cp,Graphics g,int x,int y){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); g2.translate(x,y); g2.setColor(c); g2.setStroke(new BasicStroke(2)); g2.drawLine(s, s/2, 0, s/2); g2.drawLine(0, s/2, s/2, 0); g2.drawLine(0, s/2, s/2, s); g2.dispose(); } }
}