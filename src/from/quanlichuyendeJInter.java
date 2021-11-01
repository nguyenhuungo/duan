/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package from;

import dao.chuyenDeDAO;
import helper.dialogHelper;
import helper.shareHelper;
import helper.utilityHelper;
import static java.awt.Color.pink;
import static java.awt.Color.white;
import java.io.File;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.chuyenDe;
/**
 *
 * @author Admin
 */
public class quanlichuyendeJInter extends javax.swing.JInternalFrame {

    /**
     * Creates new form quanlichuyendeJInter
     */
    public quanlichuyendeJInter() {
        initComponents();
        //this.innit();
        this.load();
    }
    private  int index=0;
    chuyenDeDAO cddao=new chuyenDeDAO();
    JFileChooser fChooser= new JFileChooser();
    
    private  void  innit(){
        tabs.setSelectedIndex(0);
        fChooser.setDialogTitle("Chọn Logo cho chuyên đề");
        
    }
     void init() {
        setFrameIcon(shareHelper.APP_ICON_1); //set Icon JInternalFrame 
        tabs.setSelectedIndex(1);       //chuyển tab panel sang tab 2
    }
    private void load() {
        DefaultTableModel model = (DefaultTableModel) tbbangchuyende.getModel();
        model.setRowCount(0);
        try {
            List<chuyenDe> list = cddao.select();
            for (chuyenDe cd : list) {
                Object[] row = {
                    cd.getMaCD(),
                    cd.getTenCD(),
                    cd.getHocPhi(),
                    cd.getThoiLuong(),
                    cd.getHinh()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            dialogHelper.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }
     
    private void insert() {
        chuyenDe model = getModel();
        try {
            cddao.insert(model);
            this.load();
            this.clear();
            dialogHelper.alert(this, "Thêm mới thành công!");
        } catch (Exception e) {
            dialogHelper.alert(this, "Thêm mới thất bại!");
        }
    }

    //lấy thông tin trên form, cập nhật vào bản ghi CSDL theo maCD
    //load lại bảng
    private void update() {
        chuyenDe model = getModel();
        try {
           cddao.update(model);
            this.load();
            dialogHelper.alert(this, "Cập nhật thành công!");
        } catch (Exception e) {
            dialogHelper.alert(this, "Cập nhật thất bại!");
        }
    }

    //xóa bản ghi trong CSDL theo maCD lấy trên form
    //load lại bảng
    //xóa trắng form, chuyển sang insertable
    private void delete() {
        if (dialogHelper.confirm(this, "Bạn có muốn xóa hay không?")) {
            String macd = txtmachuyende.getText();
            try {
                cddao.delete(macd);
                this.load();
                this.clear();
                dialogHelper.alert(this, "Xóa thành công!");
            } catch (Exception e) {
                dialogHelper.alert(this, "Xóa thất bại!");
            }
        }
    }

    //xóa trắng form, chuyển sang insertable
    private void clear() {
        this.setModel(new chuyenDe());
        this.setStatus(true);
    }

    //lấy maCD theo index, lấy đt chuyenDe từ CSDL theo maCD 
    //hiển thị thông tin từ đt chuyenDe lên form, chuyển sang editable
    private void edit() {
        try {
            String macd = (String) tbbangchuyende.getValueAt(this.index, 0);
            chuyenDe model = cddao.findById(macd);
            if (model != null) {
                this.setModel(model);
                this.setStatus(false);
            }
        } catch (Exception e) {
            dialogHelper.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    //lấy thông tin từ đt chuyenDe đưa lên form
    //lưu ý lấy hình ảnh từ thư mục logos đưa lên form theo tenFile lấy từ chuyenDe
    private void setModel(chuyenDe model) {
        txtmachuyende.setText(model.getMaCD());
        txttenchuyende.setText(model.getTenCD());
        txtthoiluong.setText(String.valueOf(model.getThoiLuong()));
        txthocphi.setText(String.valueOf(model.getHocPhi()));
        txtAmotachuyende.setText(model.getMoTa());
        lblimage.setToolTipText(model.getHinh());
        if (model.getHinh() != null) {
            lblimage.setIcon(shareHelper.readLogo(model.getHinh()));
            /*
            ImageIcon readLogo(String tenFile) đọc file trong thư mục logos theo tên file trả về ImageIcon
            void setIcon(ImageIcon icon) set Icon cho lbl
            */
        }else{
            lblimage.setIcon(shareHelper.readLogo("noImage.png"));
        }
    }
    
    //lấy thông tin trên form điền vào đt chuyenDe
    //return chuyenDe
     chuyenDe getModel() {
        chuyenDe model = new chuyenDe();
        model.setMaCD(txtmachuyende.getText());
        model.setTenCD(txttenchuyende.getText());
        model.setThoiLuong(Integer.valueOf(txtthoiluong.getText()));
        model.setHocPhi(Double.valueOf(txthocphi.getText()));
        model.setHinh(lblimage.getToolTipText());    //lấy tên hình
        model.setMoTa(txtAmotachuyende.getText());
        return model;
    }

    /*
    insertable:
        enable: txtMaCD, btnInsert, btnClear
        disable: btnUpdate, btnExit, 4 nút điều hướng
    editable:
        ngược lại
    btnClear lúc nào cũng enable
    */
    private void setStatus(boolean insertable) {
        txtmachuyende.setEditable(insertable);
        btnthem.setEnabled(insertable);
        btnsua.setEnabled(!insertable);
        btnxoa.setEnabled(!insertable);
        boolean first = this.index > 0;
        boolean last = this.index < tbbangchuyende.getRowCount() - 1;
        btndau.setEnabled(!insertable && first);
        btnlui.setEnabled(!insertable && first);
        btncuoi.setEnabled(!insertable && last);
        btntien.setEnabled(!insertable && last);
    }

    /*
    hiển thị hộp thoại cho người dùng chọn file
    copy file đó vào thư mục logos
    đọc file trong thư mục logos và hiển thị lên form, đặt tenFile là toolTipText của lbl
    ----------------------------------------------------------------------------
    khởi tạo: JFileChooser c = new JFileChooser();
        hoặc JFileChooser c = new JFileChooser(String duongDan); //đường dẫn đến folder mặc định
    hiển thị hộp thoại mở file: c.showOpenDialog(this);
    hiển thị hộp thoại lưu file: c.showSaveDialog(this);
    2 câu lệnh trên trả về 1 giá trị int là:
        JFileChooser.APPROVE_OPTION người dùng đã chọn (lưu) đc file
        JFileChooser.CANCEL_OPTION  người dùng chưa chọn (lưu) đc file
    nếu người dùng đã chọn (lưu) đc file
        File file = c.getSelectedFile(); lấy file chọn (lưu) đc
        String path = c.getSelectedFile().toString(); lấy đường dẫn file chọn (lưu) đc
    */
    private void selectImage() {
        if (fChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { //nếu người dùng đã chọn đc file
            File file = fChooser.getSelectedFile();    //lấy file người dùng chọn
            if (shareHelper.saveLogo(file)) {  //sao chép file đã chọn thư mục logos
                // Hiển thị hình lên form
                lblimage.setIcon(shareHelper.readLogo(file.getName())); //file.getName(); lấy tên của file
                //ImageIcon readLogo(String tenFile) đọc file trong thư mục logos theo tên file trả về ImageIcon
                //void setIcon(ImageIcon icon) set Icon cho lbl
                lblimage.setToolTipText(file.getName());
            }
        }
    }
    
    private boolean checkNullHinh(){
        if(lblimage.getToolTipText()!=null){
            return true;
        }else{
            dialogHelper.alert(this, "Không được để trống hình.");
            return false;
        }
    }
    private boolean checkTrungMa(JTextField txt) {
        txt.setBackground(white);
        if (cddao.findById(txt.getText()) == null) {
            return true;
        } else {
            txt.setBackground(pink);
            dialogHelper.alert(this, txt.getName() + " đã tồn tại.");
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

        jLabel3 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbbangchuyende = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txthocphi = new javax.swing.JTextField();
        txtthoiluong = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtAmotachuyende = new javax.swing.JTextArea();
        btnthem = new javax.swing.JButton();
        btnsua = new javax.swing.JButton();
        btnxoa = new javax.swing.JButton();
        btnmoi = new javax.swing.JButton();
        btnlui = new javax.swing.JButton();
        btndau = new javax.swing.JButton();
        btntien = new javax.swing.JButton();
        btncuoi = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txttenchuyende = new javax.swing.JTextField();
        txtmachuyende = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblimage = new javax.swing.JLabel();

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 255));
        jLabel3.setText("QUẢN LÍ CHUYÊN ĐỀ");

        tabs.setName(""); // NOI18N

        tbbangchuyende.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã CĐ", "Chuyên Đề", "Thời Lượng", "Học Phí"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tbbangchuyende.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbbangchuyendeMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbbangchuyende);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("Danh Sách", jPanel14);

        jLabel25.setText("Học phí");

        jLabel27.setText("Mô tả chuyên đề");

        jLabel28.setText("Thời lượng (giờ)");

        txthocphi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txthocphiActionPerformed(evt);
            }
        });

        txtAmotachuyende.setColumns(20);
        txtAmotachuyende.setRows(5);
        jScrollPane4.setViewportView(txtAmotachuyende);

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
        btncuoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncuoiActionPerformed(evt);
            }
        });

        jLabel29.setText("Tên chuyên đề");

        jLabel1.setText("Hình Logo");

        txttenchuyende.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttenchuyendeActionPerformed(evt);
            }
        });

        txtmachuyende.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmachuyendeActionPerformed(evt);
            }
        });

        jLabel30.setText("Mã chuyên đề");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblimage.setText("Image");
        lblimage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblimageMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblimage, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblimage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(btnthem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnsua)
                                .addGap(18, 18, 18)
                                .addComponent(btnxoa)
                                .addGap(28, 28, 28)
                                .addComponent(btnmoi)
                                .addGap(51, 51, 51)
                                .addComponent(btndau)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnlui)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btntien)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btncuoi)))
                        .addContainerGap())))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel30)
                            .addComponent(jLabel29))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtmachuyende, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txttenchuyende, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txthocphi)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtthoiluong, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(86, 86, 86))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(txtmachuyende, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txttenchuyende, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtthoiluong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jLabel25)
                        .addGap(19, 19, 19)
                        .addComponent(txthocphi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnthem)
                        .addComponent(btnsua)
                        .addComponent(btnxoa)
                        .addComponent(btnmoi))
                    .addComponent(btndau)
                    .addComponent(btnlui)
                    .addComponent(btntien)
                    .addComponent(btncuoi))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("Cập Nhập", jPanel10);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(253, 253, 253)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbbangchuyendeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbbangchuyendeMouseClicked
        if (evt.getClickCount() == 2) {
            this.index = tbbangchuyende.rowAtPoint(evt.getPoint());
            if (this.index >= 0) {
                this.edit();
                tabs.setSelectedIndex(1);
            }
        }
    }//GEN-LAST:event_tbbangchuyendeMouseClicked

    private void txthocphiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txthocphiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txthocphiActionPerformed

    private void btnthemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnthemActionPerformed
        if(utilityHelper.checkNullText(txtmachuyende)&&
            utilityHelper.checkNullText(txttenchuyende)&&
            utilityHelper.checkNullText(txtthoiluong)&&
            utilityHelper.checkNullText(txthocphi)&&
            utilityHelper.checkNullText(txtAmotachuyende)&&
            checkNullHinh()){
            if(utilityHelper.checkMaCD(txtmachuyende)&&
                utilityHelper.checkTenCD(txttenchuyende)&&
                utilityHelper.checkThoiLuong(txtthoiluong)&&
                utilityHelper.checkHocPhi(txthocphi)&&
                utilityHelper.checkMoTaCD(txtAmotachuyende)){
                if(checkTrungMa(txtmachuyende)){
                    insert();
                }
            }
        }
    }//GEN-LAST:event_btnthemActionPerformed

    private void btnsuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsuaActionPerformed
        // TODO add your handling code here:
        if(utilityHelper.checkNullText(txtmachuyende)&&
            utilityHelper.checkNullText(txttenchuyende)&&
            utilityHelper.checkNullText(txtthoiluong)&&
            utilityHelper.checkNullText(txthocphi)&&
            utilityHelper.checkNullText(txtAmotachuyende)&&
            checkNullHinh()){
            if(utilityHelper.checkMaCD(txtmachuyende)&&
                utilityHelper.checkTenCD(txttenchuyende)&&
                utilityHelper.checkThoiLuong(txtthoiluong)&&
                utilityHelper.checkHocPhi(txthocphi)&&
                utilityHelper.checkMoTaCD(txtAmotachuyende)){
                if(checkTrungMa(txtmachuyende)){
                    update();
                }
            }
        }
    }//GEN-LAST:event_btnsuaActionPerformed

    private void btnxoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnxoaActionPerformed
        if(shareHelper.USER.isVaiTro()){
            delete();
        }else{
            dialogHelper.alert(this, "Chỉ trưởng phòng mới được phép xóa");
        }
    }//GEN-LAST:event_btnxoaActionPerformed

    private void btnmoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmoiActionPerformed
        this.clear();
    }//GEN-LAST:event_btnmoiActionPerformed

    private void btnluiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnluiActionPerformed
        this.index--;
        this.edit();
    }//GEN-LAST:event_btnluiActionPerformed

    private void btndauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndauActionPerformed
        this.index = 0;
        this.edit();
    }//GEN-LAST:event_btndauActionPerformed

    private void btntienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntienActionPerformed
        this.index++;
        this.edit();
    }//GEN-LAST:event_btntienActionPerformed

    private void btncuoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncuoiActionPerformed
        this.index = tbbangchuyende.getRowCount()-1;
        this.edit();
    }//GEN-LAST:event_btncuoiActionPerformed

    private void txttenchuyendeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttenchuyendeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttenchuyendeActionPerformed

    private void txtmachuyendeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtmachuyendeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmachuyendeActionPerformed

    private void lblimageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblimageMouseClicked
        this.selectImage();
    }//GEN-LAST:event_lblimageMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncuoi;
    private javax.swing.JButton btndau;
    private javax.swing.JButton btnlui;
    private javax.swing.JButton btnmoi;
    private javax.swing.JButton btnsua;
    private javax.swing.JButton btnthem;
    private javax.swing.JButton btntien;
    private javax.swing.JButton btnxoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblimage;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tbbangchuyende;
    private javax.swing.JTextArea txtAmotachuyende;
    private javax.swing.JTextField txthocphi;
    private javax.swing.JTextField txtmachuyende;
    private javax.swing.JTextField txttenchuyende;
    private javax.swing.JTextField txtthoiluong;
    // End of variables declaration//GEN-END:variables
}
