package view;

import model.Alert;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class AlertsPage extends JFrame {

    private final Color PRIMARY_RED = new Color(190, 20, 20);
    private final Color DARK_GREY = new Color(50, 50, 50);
    private final Color LIGHT_BG = new Color(245, 248, 250);
    private JButton refreshButton, deleteButton, backButton;
    private JTable alertsTable;
    private DefaultTableModel tableModel;

    public AlertsPage() {
        setTitle("System Alerts");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_RED);
        header.setPreferredSize(new Dimension(1000, 80));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

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
        header.add(btnWrapper, BorderLayout.WEST);

        JLabel title = new JLabel("Expiry Alerts", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        JPanel spacer = new JPanel(); spacer.setOpaque(false); spacer.setPreferredSize(new Dimension(135,10));
        header.add(spacer, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 700));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        sidebar.setBackground(Color.WHITE);

        refreshButton = createBigButton("SCAN FOR ALERTS", new Color(46, 139, 87));
        sidebar.add(refreshButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        deleteButton = createBigButton("DISMISS ALERT", DARK_GREY);
        sidebar.add(deleteButton);
        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        ImagePanel heroImagePanel = new ImagePanel("alerts_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 200));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Blood Unit", "Blood Type", "Alert Type", "Date", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        alertsTable = new JTable(tableModel);
        alertsTable.setRowHeight(30);
        alertsTable.getTableHeader().setBackground(DARK_GREY);
        alertsTable.getTableHeader().setForeground(Color.WHITE);

        alertsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFoc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isSel, hasFoc, r, c);
                String type = (String) t.getModel().getValueAt(r, 3);
                if (!isSel) {
                    if ("Expired".equals(type)) comp.setBackground(new Color(255, 200, 200));
                    else if ("Near Expiry".equals(type)) comp.setBackground(new Color(255, 255, 200));
                    else comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        });

        mainPanel.add(new JScrollPane(alertsTable), BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    public JButton getRefreshButton() { return refreshButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getBackButton() { return backButton; }
    public JTable getAlertsTable() { return alertsTable; }
    public void showMessage(String m) { JOptionPane.showMessageDialog(this, m); }
    public void refreshTable(List<Alert> list) { tableModel.setRowCount(0); for(Alert a:list) tableModel.addRow(new Object[]{a.getAlertId(), a.getBloodId(), a.getBloodTypeDetails(), a.getAlertType(), a.getDateGenerated(), a.getStatus()}); }

    private JButton createBigButton(String t, Color bg) { JButton b=new JButton(t); b.setFont(new Font("SansSerif",Font.BOLD,12)); b.setBackground(bg); b.setForeground(Color.WHITE); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,45)); b.setAlignmentX(Component.CENTER_ALIGNMENT); return b; }

    private class ImagePanel extends JPanel {
        private Image img; public ImagePanel(String p) { try { File f=new File(p); if(f.exists()) img=ImageIO.read(f); } catch(Exception e){} }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); if(img!=null) { int w=getWidth(), h=getHeight(), iw=img.getWidth(null), ih=img.getHeight(null); if(iw>0&&ih>0) { double r=(double)iw/ih, pr=(double)w/h; int dw, dh, x, y; if(pr>r) { dh=h; dw=(int)(h*r); x=(w-dw)/2; y=0; } else { dw=w; dh=(int)(w/r); x=0; y=(h-dh)/2; } g.drawImage(img,x,y,dw,dh,this); } } else { g.setColor(new Color(240,240,245)); g.fillRect(0,0,getWidth(),getHeight()); g.setColor(Color.GRAY); g.drawString("Add 'alerts_image.jpg'", getWidth()/2-70, getHeight()/2); } }
    }

    private static class BackIcon implements Icon { int s; Color c; public BackIcon(int s, Color c){this.s=s; this.c=c;} public int getIconWidth(){return s;} public int getIconHeight(){return s;} public void paintIcon(Component cp,Graphics g,int x,int y){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); g2.translate(x,y); g2.setColor(c); g2.setStroke(new BasicStroke(2)); g2.drawLine(s, s/2, 0, s/2); g2.drawLine(0, s/2, s/2, 0); g2.drawLine(0, s/2, s/2, s); g2.dispose(); } }
}