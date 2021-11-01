/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package from;

import dao.nguoiHocDAO;
import helper.dateHelper;
import helper.dialogHelper;
import helper.shareHelper;
import helper.utilityHelper;
import static java.awt.Color.pink;
import static java.awt.Color.white;
import java.awt.HeadlessException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.nguoiHoc;

/**
 *
 * @author Admin
 */
public class quanlinguoihoc extends javax.swing.JFrame {

    /**
     * Creates new form Frame4
     */
    public quanlinguoihoc() {
        initComponents();
        load();
    }
    int index = 0;   //vị trí nguoiHoc đang được chọn
    nguoiHocDAO dao = new nguoiHocDAO();

    void init() {
        tabs.setSelectedIndex(1);     //chuyển tabPanel sang tab thứ 2
    }

    //tìm kiếm người học theo keyword rồi đổ list người học vào bảng
    //nếu không có keywork trong ô tìm kiếm thì đổ toàn bộ người học trong CSDL vào bảng
    void load() {
        //kết nối nội dung bảng (model) với thùng chứa bảng (tblGridView)
        DefaultTableModel model = (DefaultTableModel) tbbangnguoihoc.getModel();
        model.setRowCount(0);   //đưa số dòng bảng về 0 (xóa bảng)
        try {
            //tìm người học theo keywork
            //nếu ko có keywork thì sẽ là tất cả người học
            String keyword = txttimkiem.getText();
            List<nguoiHoc> list = dao.selectByKeyword(keyword);
            //đưa list tìm được lên bảng
            for (nguoiHoc nh : list) {
                Object[] row = {
                    nh.getMaNH(),
                    nh.getHoTen(),
                    nh.isGioiTinh() ? "Nam" : "Nữ",
                    dateHelper.toString(nh.getNgaySinh()),
                    nh.getDienThoai(),
                    nh.getEmail(),
                    nh.getMaNV(),
                    dateHelper.toString(nh.getNgayDK())
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            dialogHelper.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    //lấy thông tin trên form để
    //thêm 1 nguoiHoc mới vào CSDL (vẫn insertable)
    void insert() {
        nguoiHoc model = getModel();   //lấy thông tin trên form gán cho đt nguoiHoc
        try {
            dao.insert(model);    //thêm bản ghi mới vào CSDL theo tt từ nguoiHoc
            this.load();            //đổ thông tin mới vào bảng
            this.clear();           //xóa trằng form và vẫn để ở chế độ insertable
            dialogHelper.alert(this, "Thêm mới thành công!");
        } catch (Exception e) {
            dialogHelper.alert(this, "Thêm mới thất bại!");
        }
    }

    //lấy thông tin trên form
    //để chỉnh sửa nguoiHoc trong CSDL theo maNH
    void update() {
        nguoiHoc model = getModel(); //lấy thông tin form gán cho đt nguoiHoc
        try {
            dao.update(model);   //chỉnh sửa bản ghi theo tt từ nguoiHoc 
            this.load();         //đổ tt mới từ CSDL vào bảng
            dialogHelper.alert(this, "Cập nhật thành công!");
        } catch (Exception e) {
            dialogHelper.alert(this, "Cập nhật thất bại!");
            e.printStackTrace();
        }
    }

    //xóa bản ghi khỏi CSDL theo maNH lấy trên form
    //xóa trằng form và chuyển sang insertable
    void delete() {
        if (dialogHelper.confirm(this, "Bạn thực sự muốn xóa người học này?")) {
            String manh = txtmanguoihoc.getText();
            try {
                dao.delete(manh);
                this.load();
                this.clear();
                dialogHelper.alert(this, "Xóa thành công!");
            } catch (HeadlessException e) {
                dialogHelper.alert(this, "Xóa thất bại!");
            }
        }
    }

    public void setTrang() {
        txtmanguoihoc.setBackground(white);
        txthoten.setBackground(white);
        txtngaysinj.setBackground(white);
        txtsodienthoai.setBackground(white);
        txtdiachi.setBackground(white);
    }

    //xóa trằng form, ngayDK lấy ngày hôm nay, maNV tạo lấy maNV đang đăng nhập
    //chuyển sang insertable
    void clear() {
        setTrang();
        nguoiHoc model = new nguoiHoc();
        model.setMaNV(shareHelper.USER.getMaNV());
        model.setNgayDK(dateHelper.now());
        this.setModel(model);
        setStatus(true);
    }

    //điền thông tin lên form theo index
    //chuyển sang trạng thái editable
    void edit() {
        setTrang();
        try {
            String manh = (String) tbbangnguoihoc.getValueAt(this.index, 0); //lấy maNH theo index
            nguoiHoc model = dao.findById(manh);    //lấy nguoiHoc theo maNH
            if (model != null) {
                this.setModel(model);   //điền thông tin lên form theo nguoiHoc
                this.setStatus(false);
            }
        } catch (Exception e) {
            dialogHelper.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    //điền thông tin từ đối tượng nguoiHoc lên form
    void setModel(nguoiHoc model) {
        txtmanguoihoc.setText(model.getMaNH());
        txthoten.setText(model.getHoTen());
        cbbgioitinh.setSelectedIndex(model.isGioiTinh() ? 0 : 1);
        txtngaysinj.setText(dateHelper.toString(model.getNgaySinh()));
        txtsodienthoai.setText(model.getDienThoai());
        txtdiachi.setText(model.getEmail());
        txtAghichu.setText(model.getGhiChu());
    }

    //lấy thông trên form  cho vào đối tượng nguoiHoc
    //return nguoiHoc
    nguoiHoc getModel() {
        nguoiHoc model = new nguoiHoc();
        model.setMaNH(txtmanguoihoc.getText());
        model.setHoTen(txthoten.getText());
        model.setGioiTinh(cbbgioitinh.getSelectedIndex() == 0);
        model.setNgaySinh(dateHelper.toDate(txtngaysinj.getText()));
        model.setDienThoai(txtsodienthoai.getText());
        model.setEmail(txtdiachi.getText());
        model.setGhiChu(txtAghichu.getText());
        model.setMaNV(shareHelper.USER.getMaNV());
        model.setNgayDK(dateHelper.now());     //ngày đăng kí luôn là ngày hôm nay dù có sửa trên form
        return model;
    }

    /*
    insertable (true)
        enable: txtMaNH, btnInsert
        disable: btnDelete, btnUpdate, 4 nút điều hướng
    editable (false)     ngược lại
    btnClear lúc nào cũng enable
    ở đầu tiên disable first, prev, ở cuối cùng disable last, next
     */
    void setStatus(boolean insertable) {
        txtmanguoihoc.setEditable(insertable);
        btnthem.setEnabled(insertable);
        btnsua.setEnabled(!insertable);
        btnxoa.setEnabled(!insertable);
        boolean first = this.index > 0;
        boolean last = this.index < tbbangnguoihoc.getRowCount() - 1;
        btndau.setEnabled(!insertable && first);
        btnlui.setEnabled(!insertable && first);
        btntien.setEnabled(!insertable && last);
        btncuoi.setEnabled(!insertable && last);
    }

    //khi mở form đổ dữ liệu vào bảng
    //để ở trạng thái insertable
    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {                                         
        // TODO add your handling code here:
        this.load();
        this.setStatus(true);
    }                                        

    //lấy vị trí dòng được chọn
    //hiển thị thông tin bản ghi lên form
    //chuyển sang trạng thái editable
    private void tblGridViewMouseClicked(java.awt.event.MouseEvent evt) {                                         
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.index = tbbangnguoihoc.rowAtPoint(evt.getPoint()); //lấy vị trí dòng được chọn
            if (this.index >= 0) {
                this.edit();
                tabs.setSelectedIndex(0);
            }
        }
    }                                        
    public boolean check16Nam(JTextField txt) {
        txt.setBackground(white);
        Date date = dateHelper.toDate(txt.getText());
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date);
        c2.setTime(new Date());
        long a = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
        if (a >= 5844) {
            return true;
        } else {
            txt.setBackground(pink);
            dialogHelper.alert(this, txt.getName() + " phải cách đây ít nhất 16 năm.");
            return false;
        }
    }

    public boolean checkTrungMa(JTextField txt) {
        txt.setBackground(white);
        if (dao.findById(txt.getText()) == null) {
            return true;
        } else {
            txt.setBackground(pink);
            dialogHelper.alert(this, txt.getName() + " đã bị tồn tại.");
            return false;
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tabs = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbbangnguoihoc = new javax.swing.JTable();
        txttimkiem = new javax.swing.JTextField();
        btntimkiem = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        cbbgioitinh = new javax.swing.JComboBox<>();
        txthoten = new javax.swing.JTextField();
        txtmanguoihoc = new javax.swing.JTextField();
        txtdiachi = new javax.swing.JTextField();
        txtngaysinj = new javax.swing.JTextField();
        txtsodienthoai = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAghichu = new javax.swing.JTextArea();
        btnthem = new javax.swing.JButton();
        btnsua = new javax.swing.JButton();
        btnxoa = new javax.swing.JButton();
        btnmoi = new javax.swing.JButton();
        btnlui = new javax.swing.JButton();
        btndau = new javax.swing.JButton();
        btntien = new javax.swing.JButton();
        btncuoi = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabs.setName(""); // NOI18N

        tbbangnguoihoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã NH", "Họ và tên", "Giới tính", "Địa chỉ", "Số điện thoại", "Ngày sinh", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tbbangnguoihoc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbbangnguoihocMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbbangnguoihoc);

        btntimkiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Zoom.png"))); // NOI18N
        btntimkiem.setText("Tìm kiếm");
        btntimkiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntimkiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(txttimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 574, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(btntimkiem)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txttimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btntimkiem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );

        tabs.addTab("Danh Sách", jPanel12);

        jLabel22.setText("Mã người học");

        jLabel25.setText("Giới tính");

        jLabel26.setText("Số điện thoại");

        jLabel27.setText("Ghi chú");

        jLabel28.setText("Địa chỉ");

        jLabel29.setText("Họ và tên");

        jLabel30.setText("Ngày sinh");

        cbbgioitinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam" }));

        txthoten.setText("A");

        txtmanguoihoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmanguoihocActionPerformed(evt);
            }
        });

        txtdiachi.setText("Hà Nội");

        txtngaysinj.setText("2/2/2021");

        txtsodienthoai.setText("0982132132");
        txtsodienthoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtsodienthoaiActionPerformed(evt);
            }
        });

        txtAghichu.setColumns(20);
        txtAghichu.setRows(5);
        txtAghichu.setText("Hello Man");
        jScrollPane2.setViewportView(txtAghichu);

        btnthem.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnthem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Add.png"))); // NOI18N
        btnthem.setText("Thêm");
        btnthem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnthemActionPerformed(evt);
            }
        });

        btnsua.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnsua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Notes.png"))); // NOI18N
        btnsua.setText("Sửa");
        btnsua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsuaActionPerformed(evt);
            }
        });

        btnxoa.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnxoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Delete.png"))); // NOI18N
        btnxoa.setText("Xóa");
        btnxoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnxoaActionPerformed(evt);
            }
        });

        btnmoi.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnmoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Unordered list.png"))); // NOI18N
        btnmoi.setText("Mới");
        btnmoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmoiActionPerformed(evt);
            }
        });

        btnlui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/lui.png"))); // NOI18N
        btnlui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnluiActionPerformed(evt);
            }
        });

        btndau.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/dau.png"))); // NOI18N
        btndau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndauActionPerformed(evt);
            }
        });

        btntien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/tien.png"))); // NOI18N
        btntien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntienActionPerformed(evt);
            }
        });

        btncuoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/cuoi.png"))); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(txthoten)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel26)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel22)
                                    .addComponent(cbbgioitinh, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel25)
                                    .addComponent(txtsodienthoai, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtngaysinj, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel30)
                                    .addComponent(txtdiachi, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(txtmanguoihoc))
                        .addGap(38, 38, 38))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btnthem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnsua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnxoa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnmoi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btndau)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnlui)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(btntien)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btncuoi)
                .addGap(9, 9, 9))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btncuoi)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtmanguoihoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txthoten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel28))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtdiachi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbbgioitinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtsodienthoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtngaysinj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btndau)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnthem)
                                .addComponent(btnsua)
                                .addComponent(btnxoa)
                                .addComponent(btnmoi))
                            .addComponent(btnlui)
                            .addComponent(btntien))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("Cập Nhập", jPanel10);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 0, 255));
        jLabel1.setText("QUẢN LÍ NGƯỜI HỌC");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(293, 293, 293)
                .addComponent(jLabel1)
                .addContainerGap(356, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tabs)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(621, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(56, 56, 56)
                    .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(57, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbbangnguoihocMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbbangnguoihocMouseClicked
        if (evt.getClickCount() == 2) {
            this.index = tbbangnguoihoc.rowAtPoint(evt.getPoint()); //lấy vị trí dòng được chọn
            if (this.index >= 0) {
                this.edit();
                tabs.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_tbbangnguoihocMouseClicked

    private void txtmanguoihocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtmanguoihocActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmanguoihocActionPerformed

    private void txtsodienthoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsodienthoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsodienthoaiActionPerformed

    private void btnxoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnxoaActionPerformed
         if (shareHelper.USER.isVaiTro()) {
            delete();
        } else {
            dialogHelper.alert(this, "Chỉ trưởng phòng mới được phép xóa");
        }
    }//GEN-LAST:event_btnxoaActionPerformed

    private void btnthemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnthemActionPerformed
         if (utilityHelper.checkNullText(txtmanguoihoc)
                && utilityHelper.checkNullText(txthoten)
                && utilityHelper.checkNullText(txtngaysinj)
                && utilityHelper.checkNullText(txtsodienthoai)
                && utilityHelper.checkNullText(txtdiachi)) {
            if (utilityHelper.checkMaNH(txtmanguoihoc)
                    && utilityHelper.checkName(txthoten)
                    && utilityHelper.checkDate(txtngaysinj)
                    && utilityHelper.checkSDT(txtsodienthoai)
                    && utilityHelper.checkEmail(txtdiachi)) {
                if (checkTrungMa(txtmanguoihoc)) {
                    if (check16Nam(txtngaysinj)) {
                        insert();
                    }
                }
            }
        }
    }//GEN-LAST:event_btnthemActionPerformed

    private void btnsuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsuaActionPerformed
         if (utilityHelper.checkNullText(txthoten)
                && utilityHelper.checkNullText(txtngaysinj)
                && utilityHelper.checkNullText(txtsodienthoai)
                && utilityHelper.checkNullText(txtdiachi)) {
            if (utilityHelper.checkName(txthoten)
                    && utilityHelper.checkDate(txtngaysinj)
                    && utilityHelper.checkSDT(txtsodienthoai)
                    && utilityHelper.checkEmail(txtdiachi)) {
                if (check16Nam(txtngaysinj)) {
                    update();
                }
            }
        }
    }//GEN-LAST:event_btnsuaActionPerformed

    private void btnmoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmoiActionPerformed
        clear();
    }//GEN-LAST:event_btnmoiActionPerformed

    private void btndauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndauActionPerformed
        this.index = 0;
        this.edit();
    }//GEN-LAST:event_btndauActionPerformed

    private void btnluiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnluiActionPerformed
        this.index--;
        this.edit();
    }//GEN-LAST:event_btnluiActionPerformed

    private void btntienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntienActionPerformed
        this.index++;
        this.edit();
    }//GEN-LAST:event_btntienActionPerformed

    private void btntimkiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntimkiemActionPerformed
        this.load();
        this.clear();
    }//GEN-LAST:event_btntimkiemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(quanlinguoihoc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(quanlinguoihoc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(quanlinguoihoc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(quanlinguoihoc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new quanlinguoihoc().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncuoi;
    private javax.swing.JButton btndau;
    private javax.swing.JButton btnlui;
    private javax.swing.JButton btnmoi;
    private javax.swing.JButton btnsua;
    private javax.swing.JButton btnthem;
    private javax.swing.JButton btntien;
    private javax.swing.JButton btntimkiem;
    private javax.swing.JButton btnxoa;
    private javax.swing.JComboBox<String> cbbgioitinh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tbbangnguoihoc;
    private javax.swing.JTextArea txtAghichu;
    private javax.swing.JTextField txtdiachi;
    private javax.swing.JTextField txthoten;
    private javax.swing.JTextField txtmanguoihoc;
    private javax.swing.JTextField txtngaysinj;
    private javax.swing.JTextField txtsodienthoai;
    private javax.swing.JTextField txttimkiem;
    // End of variables declaration//GEN-END:variables
}
