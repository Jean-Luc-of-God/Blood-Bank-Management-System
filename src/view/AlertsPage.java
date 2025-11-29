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

        ImagePanel heroImagePanel = new ImagePanel("alerts_image.jpg");
        heroImagePanel.setPreferredSize(new Dimension(100, 200));
        mainPanel.add(heroImagePanel, BorderLayout.NORTH);

        // Updated Columns: Shows 'Blood Type' now!
        tableModel = new DefaultTableModel(new String[]{"Alert ID", "Blood Type", "Alert Type", "Date", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        alertsTable = new JTable(tableModel);
        alertsTable.setRowHeight(30);
        alertsTable.getTableHeader().setBackground(DARK_GREY);
        alertsTable.getTableHeader().setForeground(Color.WHITE);

        // Add Color Renderer
        alertsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String type = (String) table.getModel().getValueAt(row, 2); // Alert Type column
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
            // Using the new bloodTypeString here
            tableModel.addRow(new Object[]{a.getAlertId(), a.getBloodTypeString(), a.getAlertType(), a.getDateGenerated(), a.getStatus()});
        }
    }

    private JButton createBigButton(String t, Color bg) { JButton b=new JButton(t); b.setFont(new Font("SansSerif",Font.BOLD,12)); b.setBackground(bg); b.setForeground(Color.WHITE); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,45)); b.setAlignmentX(Component.CENTER_ALIGNMENT); return b; }

    private class ImagePanel extends JPanel {
        private Image img;
        public ImagePanel(String p) { try { File f=new File(p); if(f.exists()) img=ImageIO.read(f); } catch(Exception e){} }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                // Smart Fit Logic
                double aspect = (double) getWidth() / getHeight();
                double imgAspect = (double) img.getWidth(null) / img.getHeight(null);
                int w, h, x, y;
                if (aspect > imgAspect) { w=getWidth(); h=(int)(getWidth()/imgAspect); x=0; y=0; }
                else { h=getHeight(); w=(int)(getHeight()*imgAspect); x=(getWidth()-w)/2; y=0; }
                g.drawImage(img, x, y, w, h, this);
            } else {
                g.setColor(new Color(240, 240, 245)); g.fillRect(0,0,getWidth(),getHeight());
                g.setColor(Color.GRAY); g.drawString("Add 'alerts_image.jpg'", getWidth()/2-70, getHeight()/2);
            }
        }
    }
}