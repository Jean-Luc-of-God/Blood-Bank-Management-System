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
    private JButton refreshButton, deleteButton;
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
        JLabel title = new JLabel("  Expiry Alerts");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
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

        // SMART IMAGE PANEL (SCALE TO FIT)
        ImagePanel heroImagePanel = new ImagePanel("alerts_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 200));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Blood Unit", "Blood Type", "Alert Type", "Date", "Status"}, 0);
        alertsTable = new JTable(tableModel);
        alertsTable.setRowHeight(30);
        alertsTable.getTableHeader().setBackground(DARK_GREY);
        alertsTable.getTableHeader().setForeground(Color.WHITE);

        alertsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String type = (String) table.getModel().getValueAt(row, 3);
                if (!isSelected) {
                    if ("Expired".equals(type)) c.setBackground(new Color(255, 200, 200));
                    else if ("Near Expiry".equals(type)) c.setBackground(new Color(255, 255, 200));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        mainPanel.add(new JScrollPane(alertsTable), BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    public JButton getRefreshButton() { return refreshButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JTable getAlertsTable() { return alertsTable; }
    public void showMessage(String m) { JOptionPane.showMessageDialog(this, m); }

    public void refreshTable(List<Alert> list) {
        tableModel.setRowCount(0);
        for(Alert a:list) {
            tableModel.addRow(new Object[]{a.getAlertId(), a.getBloodId(), a.getBloodTypeDetails(), a.getAlertType(), a.getDateGenerated(), a.getStatus()});
        }
    }

    private JButton createBigButton(String t, Color bg) { JButton b=new JButton(t); b.setFont(new Font("SansSerif",Font.BOLD,12)); b.setBackground(bg); b.setForeground(Color.WHITE); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,45)); b.setAlignmentX(Component.CENTER_ALIGNMENT); return b; }

    // --- SMART IMAGE PANEL (SCALE TO FIT) ---
    private class ImagePanel extends JPanel {
        private Image img;
        public ImagePanel(String p) { try { File f=new File(p); if(f.exists()) img=ImageIO.read(f); } catch(Exception e){} }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                // FIXED: Scale to Fit (No Zoom/Crop)
                int panelW = getWidth();
                int panelH = getHeight();
                int imgW = img.getWidth(null);
                int imgH = img.getHeight(null);

                if (imgW > 0 && imgH > 0) {
                    double imgAspect = (double) imgW / imgH;
                    double panelAspect = (double) panelW / panelH;
                    int drawW, drawH, x, y;

                    if (panelAspect > imgAspect) {
                        // Panel is flatter than image -> Fit to Height
                        drawH = panelH;
                        drawW = (int) (panelH * imgAspect);
                        y = 0;
                        x = (panelW - drawW) / 2; // Center horizontally
                    } else {
                        // Panel is taller than image -> Fit to Width
                        drawW = panelW;
                        drawH = (int) (panelW / imgAspect);
                        x = 0;
                        y = (panelH - drawH) / 2; // Center vertically
                    }
                    g.drawImage(img, x, y, drawW, drawH, this);
                }
            } else {
                g.setColor(new Color(240, 240, 245)); g.fillRect(0,0,getWidth(),getHeight());
                g.setColor(Color.GRAY); g.drawString("Add 'alerts_image.jpg'", getWidth()/2-70, getHeight()/2);
            }
        }
    }
}