package shopbancancau.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;

public class OrderHistoryView extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnDetail;
    private JButton btnFilter;
    private JButton btnBack;

    private JSpinner spinnerFromDate;
    private JSpinner spinnerToDate;

    private JLabel lblTotal; // HIỂN THỊ TỔNG DOANH THU
    private POSView parentView;

    public OrderHistoryView() {
        this.parentView = null; // For standalone use
        initComponents();
    }

    public OrderHistoryView(POSView parent) {
        this.parentView = parent;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Thống kê");
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(parentView != null ? JFrame.DO_NOTHING_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Container cho top panel và filter panel
        JPanel northContainer = new JPanel(new BorderLayout());
        
        // Window listener và nút Quay lại chỉ khi có parent
        if (parentView != null) {
            // Window listener: dispose and show parent POSView
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                    if (parentView != null) {
                        parentView.setVisible(true);
                        parentView.toFront();
                        parentView.requestFocus();
                    }
                }
            });
            
            // Nút Quay lại ở góc trên trái
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            topPanel.setBackground(new Color(245, 245, 245));
            topPanel.setOpaque(false);
            btnBack = new JButton("← Quay lại");
            btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnBack.setPreferredSize(new Dimension(120, 35));
            btnBack.setBackground(new Color(0, 120, 215)); // Màu xanh
            btnBack.setForeground(Color.WHITE);
            btnBack.setFocusPainted(false);
            btnBack.setBorderPainted(false);
            btnBack.addActionListener(e -> {
                dispose();
                if (parentView != null) {
                    parentView.setVisible(true);
                    parentView.toFront();
                    parentView.requestFocus();
                }
            });
            topPanel.add(btnBack);
            northContainer.add(topPanel, BorderLayout.NORTH);
        }

        // ===== FILTER PANEL =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new Color(245, 245, 245));

        // Label và JSpinner cho "Từ ngày"
        JLabel lblFromDate = new JLabel("Từ ngày:");
        lblFromDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(lblFromDate);
        
        // Tạo SpinnerDateModel với format dd/MM/yyyy
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(Calendar.HOUR_OF_DAY, 0);
        calFrom.set(Calendar.MINUTE, 0);
        calFrom.set(Calendar.SECOND, 0);
        calFrom.set(Calendar.MILLISECOND, 0);
        
        SpinnerDateModel modelFrom = new SpinnerDateModel();
        modelFrom.setValue(calFrom.getTime());
        spinnerFromDate = new JSpinner(modelFrom);
        
        // Format hiển thị dd/MM/yyyy
        JSpinner.DateEditor editorFrom = new JSpinner.DateEditor(spinnerFromDate, "dd/MM/yyyy");
        spinnerFromDate.setEditor(editorFrom);
        spinnerFromDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerFromDate.setPreferredSize(new Dimension(150, 30));
        spinnerFromDate.setBackground(Color.WHITE);
        filterPanel.add(spinnerFromDate);

        // Label và JSpinner cho "Đến ngày"
        JLabel lblToDate = new JLabel("Đến ngày:");
        lblToDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(lblToDate);
        
        Calendar calTo = Calendar.getInstance();
        calTo.set(Calendar.HOUR_OF_DAY, 0);
        calTo.set(Calendar.MINUTE, 0);
        calTo.set(Calendar.SECOND, 0);
        calTo.set(Calendar.MILLISECOND, 0);
        calTo.add(Calendar.DAY_OF_MONTH, 1); // Mặc định = ngày mai
        
        SpinnerDateModel modelTo = new SpinnerDateModel();
        modelTo.setValue(calTo.getTime());
        spinnerToDate = new JSpinner(modelTo);
        
        // Format hiển thị dd/MM/yyyy
        JSpinner.DateEditor editorTo = new JSpinner.DateEditor(spinnerToDate, "dd/MM/yyyy");
        spinnerToDate.setEditor(editorTo);
        spinnerToDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerToDate.setPreferredSize(new Dimension(150, 30));
        spinnerToDate.setBackground(Color.WHITE);
        filterPanel.add(spinnerToDate);
        
        // Listener: Tự động điều chỉnh spinnerTo khi spinnerFrom thay đổi (sau khi cả hai đã được tạo)
        spinnerFromDate.addChangeListener(e -> {
            Date fromDate = (Date) spinnerFromDate.getValue();
            Date toDate = (Date) spinnerToDate.getValue();
            
            if (fromDate != null && toDate != null && fromDate.after(toDate)) {
                // Nếu From > To, tự động set To = From + 1 ngày
                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                spinnerToDate.setValue(cal.getTime());
            }
            
            // Cập nhật trạng thái nút Lọc
            updateFilterButtonState();
        });
        
        // Listener: Kiểm tra validation khi thay đổi spinnerTo
        spinnerToDate.addChangeListener(e -> {
            updateFilterButtonState();
        });

        // Nút Lọc
        btnFilter = new JButton("Lọc");
        btnFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnFilter.setPreferredSize(new Dimension(80, 30));
        btnFilter.setBackground(new Color(0, 120, 215));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.setBorderPainted(false);
        filterPanel.add(btnFilter);
        
        // Khởi tạo trạng thái nút Lọc
        updateFilterButtonState();

        northContainer.add(filterPanel, BorderLayout.SOUTH);
        add(northContainer, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] cols = {"Mã hóa đơn", "Ngày", "Tổng tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ===== BOTTOM PANEL =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        lblTotal = new JLabel("Tổng doanh thu: 0 đ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(lblTotal, BorderLayout.WEST);

        btnDetail = new JButton("Xem chi tiết");
        bottomPanel.add(btnDetail, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ===== GETTERS =====
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTable() {
        return table;
    }

    public void addDetailListener(ActionListener l) {
        btnDetail.addActionListener(l);
    }

    // ===== FILTER SUPPORT =====
    public Date getFromDate() {
        return (Date) spinnerFromDate.getValue();
    }

    public Date getToDate() {
        return (Date) spinnerToDate.getValue();
    }
    
    public boolean hasDateSelection() {
        Date fromDate = getFromDate();
        Date toDate = getToDate();
        return fromDate != null && toDate != null && !fromDate.after(toDate);
    }
    
    private void updateFilterButtonState() {
        Date fromDate = getFromDate();
        Date toDate = getToDate();
        
        if (fromDate == null || toDate == null) {
            btnFilter.setEnabled(false);
            btnFilter.setToolTipText("Vui lòng chọn đầy đủ từ ngày và đến ngày");
        } else if (fromDate.after(toDate)) {
            btnFilter.setEnabled(false);
            btnFilter.setToolTipText("Từ ngày phải nhỏ hơn hoặc bằng đến ngày");
        } else {
            btnFilter.setEnabled(true);
            btnFilter.setToolTipText(null);
        }
    }

    public void addFilterListener(ActionListener l) {
        btnFilter.addActionListener(l);
    }

    // ===== TOTAL LABEL =====
    public void setTotalText(String text) {
        lblTotal.setText(text);
    }
}
