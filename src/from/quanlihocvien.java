/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package from;

import dao.hocVienDAO;
import dao.nguoiHocDAO;
import helper.dialogHelper;
import helper.jdbcHelper;
import helper.shareHelper;
import helper.utilityHelper;
import static java.awt.Color.white;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import model.hocVien;
import model.nguoiHoc;

/**
 *
 * @author Admin
 */
public class quanlihocvien extends javax.swing.JFrame {

    /**
     * Creates new form Frame
     */
    public quanlihocvien() {
        initComponents();
        init();
        this.MaKH = MaKH;
    }
    public Integer MaKH;   //maKH được chọn nhập từ constructor
    hocVienDAO dao = new hocVienDAO();
    nguoiHocDAO nhdao = new nguoiHocDAO();

    void init() {
        setLocationRelativeTo(null);
    }

    //lấy tất cả đối tượng nguoiHoc không thuộc khoaHoc từ CSDL (theo maKH)
    //rồi thêm vào combobox
    void fillComboBox() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbten.getModel(); //kết nối cbo với model
        model.removeAllElements(); //xóa toàn bộ item
        try {
            //lấy tất cả đối tượng nguoiHoc không thuộc khoaHoc từ CSDL
            //rồi thêm vào combobox
            List<nguoiHoc> list = nhdao.selectByCourse(MaKH);
            for (nguoiHoc nh : list) {
                model.addElement(nh);
            }
        } catch (Exception e) {
            dialogHelper.alert(this, "Lỗi truy vấn học viên!");
        }
    }

    //lấy về MaHV, MaKH, MaNH, Diem, HoTen từ các bảng trong CSDL của các học viên thuộc khóa học (theo maKH)
    //điền các bản ghi tương ứng vào bảng theo: tất cả, chưa nhập điểm, đã nhập điểm
    void fillGridView() {
        DefaultTableModel model = (DefaultTableModel) tbbanghocvien.getModel();
        model.setRowCount(0);
        ResultSet rs = null;
        try {
            //lấy về MaHV, MaKH, MaNH, Diem, HoTen từ các bảng trong CSDL của các học viên thuộc 
            //khóa học (theo maKH)
            String sql = "SELECT hv.*, nh.HoTen FROM hocVien hv "
                    + " JOIN nguoiHoc nh ON nh.MaNH=hv.MaNH WHERE MaKH=?";
            rs = jdbcHelper.executeQuery(sql, MaKH);
            while (rs.next()) {
                double diem = rs.getDouble("Diem");
                Object[] row = {
                    rs.getInt("MaHV"), rs.getString("MaNH"),
                    rs.getString("HoTen"), diem, false
                };
                if (radiotatca.isSelected()) {  //tất cả thì add tất cả bản ghi vào 
                    model.addRow(row);
                } else if (radiodanhapdiem.isSelected() && diem >= 0) {//đã nhập thì chỉ add bản ghi điểm 0-10
                    model.addRow(row);
                } else if (radiochuanhapdiem.isSelected() && diem < 0) {//chưa nhập thì chỉ nhập bản ghi điểm -1
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            dialogHelper.alert(this, "Lỗi truy vấn học viên!");
        } finally {
            try {
                rs.getStatement().getConnection().close();
            } catch (SQLException ex) {
                throw new RuntimeException();
            }
        }
    }

    /*
    thêm đt hocVien vào CSDL bảng hocVien
    load lại combobox, bảng
    MaHV là tự sinh từ 1 đến ... trong CSDL ko cần nhập
    mã tự sinh này là cố định không đổi kể cả khi bản ghi phía trước bị xóa làm stt thay đổi
     */
    void insert() {
        nguoiHoc nguoiHoc = (nguoiHoc) cbbten.getSelectedItem(); //lấy đt nguoiHoc từ combobox
        hocVien model = new hocVien(); //tạo đt hocVien
        model.setMaKH(MaKH);
        model.setMaNH(nguoiHoc.getMaNH());
        try {
            dao.insert(model);  //thêm đt hocVien vào CSDL bảng hocVien
            this.fillComboBox(); //load lại combobox
            this.fillGridView(); //load lại bảng
        } catch (Exception e) {
            dialogHelper.alert(this, "Lỗi thêm học viên vào khóa học!");
        }
    }

    //chỉ sửa điểm hocVien, xóa hocVien bị tích
    //cập nhật vào CSDL, load lại bảng, load lại cbo
    void update() {
        int a = 0, b = 0;
        for (int i = 0; i < tbbanghocvien.getRowCount(); i++) {
            Integer mahv = (Integer) tbbanghocvien.getValueAt(i, 0);  //lấy maHV từ bảng(ko sửa đc)
            String manh = (String) tbbanghocvien.getValueAt(i, 1);  //lấy maNH từ bảng(ko sửa đc)
            Double diem = (Double) tbbanghocvien.getValueAt(i, 3);   //lấy điểm (sửa đc)
            Boolean isDelete = (Boolean) tbbanghocvien.getValueAt(i, 4);
            if (isDelete) {
                a++;
            }
            if (isDelete && shareHelper.USER.isVaiTro()) {     //nếu có tích thì xóa bản ghi đó đi
                dao.delete(mahv);
            } else {           //còn ko tích thì cập 
                if (shareHelper.USER.isVaiTro() == false) {
                    tbbanghocvien.setValueAt(false, i, 3);
                }
                if ((diem >= 0 && diem <= 10) || diem == -1) {
                    hocVien model = new hocVien();
                    model.setMaHV(mahv);
                    model.setMaKH(MaKH);
                    model.setMaNH(manh);
                    model.setDiem(diem);
                    dao.update(model);
                } else {
                    b++;
                }
            }
        }
        this.fillComboBox();
        this.fillGridView();
        if (a > 0 && shareHelper.USER.isVaiTro() == false) {
            dialogHelper.alert(this, "Chỉ trưởng phòng mới được xóa học viên\nbạn chỉ được thêm học viên và điểm");
            return;
        }
        if (b > 0) {
            dialogHelper.alert(this, "Điểm phải là số thực từ 0-10 hoặc chưa nhập (-1)");
            return;
        }
        dialogHelper.alert(this, "Cập nhật thành công!");
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
        jPanel2 = new javax.swing.JPanel();
        cbbten = new javax.swing.JComboBox<>();
        btnthem = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbbanghocvien = new javax.swing.JTable();
        radiotatca = new javax.swing.JRadioButton();
        radiodanhapdiem = new javax.swing.JRadioButton();
        radiochuanhapdiem = new javax.swing.JRadioButton();
        btncapnhap = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "HỌC VIÊN KHÁC", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        cbbten.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nguyễn Văn An" }));

        btnthem.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnthem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Add.png"))); // NOI18N
        btnthem.setText("Thêm");
        btnthem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnthemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(cbbten, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(btnthem, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbbten, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnthem, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "HỌC VIÊN CỦA KHÓA HỌC", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        tbbanghocvien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã HV", "Mã NH", "Họ và Tên", "Điểm (sửa được)", "Xóa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tbbanghocvien);

        radiotatca.setSelected(true);
        radiotatca.setText("Tất cả");
        radiotatca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiotatcaActionPerformed(evt);
            }
        });

        radiodanhapdiem.setText("Đã nhập điểm");
        radiodanhapdiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiodanhapdiemActionPerformed(evt);
            }
        });

        radiochuanhapdiem.setText("Chưa nhập điểm");
        radiochuanhapdiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiochuanhapdiemActionPerformed(evt);
            }
        });

        btncapnhap.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btncapnhap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Save as.png"))); // NOI18N
        btncapnhap.setText("Cập Nhập");
        btncapnhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncapnhapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(radiotatca)
                        .addGap(27, 27, 27)
                        .addComponent(radiodanhapdiem)
                        .addGap(18, 18, 18)
                        .addComponent(radiochuanhapdiem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btncapnhap))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(radiotatca)
                            .addComponent(radiodanhapdiem)
                            .addComponent(radiochuanhapdiem))
                        .addContainerGap(47, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btncapnhap)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
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

    private void radiotatcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiotatcaActionPerformed
        this.fillGridView();
    }//GEN-LAST:event_radiotatcaActionPerformed

    private void btncapnhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncapnhapActionPerformed
        update();
    }//GEN-LAST:event_btncapnhapActionPerformed

    private void radiodanhapdiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiodanhapdiemActionPerformed
        this.fillGridView();
    }//GEN-LAST:event_radiodanhapdiemActionPerformed

    private void radiochuanhapdiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiochuanhapdiemActionPerformed
        this.fillGridView();
    }//GEN-LAST:event_radiochuanhapdiemActionPerformed

    private void btnthemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnthemActionPerformed
        insert();

    }//GEN-LAST:event_btnthemActionPerformed

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
            java.util.logging.Logger.getLogger(quanlihocvien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(quanlihocvien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(quanlihocvien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(quanlihocvien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new quanlihocvien().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncapnhap;
    private javax.swing.JButton btnthem;
    private javax.swing.JComboBox<String> cbbten;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton radiochuanhapdiem;
    private javax.swing.JRadioButton radiodanhapdiem;
    private javax.swing.JRadioButton radiotatca;
    private javax.swing.JTable tbbanghocvien;
    // End of variables declaration//GEN-END:variables
}
