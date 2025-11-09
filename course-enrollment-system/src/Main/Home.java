
package Main;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JOptionPane;

public class Home extends javax.swing.JFrame {
 Connection conn = new Database.db_conn().conn();
    DefaultTableModel coursetable;
    DefaultTableModel studenttable;
    String sTable;
    String Course;
    String Year;
    String description;
    public static Home homeInstance;

   
  public void loadCourses() {
    try {
        String sql = "SELECT course_id, course_name, course_year, description FROM courses";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("course_id"),
                rs.getString("course_name"),
                rs.getString("course_year"),
                rs.getString("description")
            });
        }

        rs.close();
        pst.close();

        // Hide ID column visually
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
    }
}


    public Home() {
        
        initComponents();
         homeInstance = this;
       loadCourses(); 
           
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        vc_btn = new javax.swing.JButton();
        veh_btn = new javax.swing.JButton();
        view_student_btn = new javax.swing.JButton();
        lg_btn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        course_addbutton = new javax.swing.JButton();
        course_update_button = new javax.swing.JButton();
        course_remove_button = new javax.swing.JButton();
        course_searchbar = new javax.swing.JTextField();
        search_coursebutton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable();
        student_add_btn = new javax.swing.JButton();
        student_update_btn = new javax.swing.JButton();
        student_remove_btn = new javax.swing.JButton();
        student_searchbar = new javax.swing.JTextField();
        student_searchbtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(0, 51, 204));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vc_btn.setBackground(new java.awt.Color(102, 102, 102));
        vc_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        vc_btn.setText("view courses");
        vc_btn.setBorder(new javax.swing.border.MatteBorder(null));
        vc_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vc_btnActionPerformed(evt);
            }
        });
        jPanel4.add(vc_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 170, 30));

        veh_btn.setBackground(new java.awt.Color(102, 102, 102));
        veh_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        veh_btn.setText("view enrollment history");
        veh_btn.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel4.add(veh_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 170, 30));

        view_student_btn.setBackground(new java.awt.Color(102, 102, 102));
        view_student_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        view_student_btn.setText("view students");
        view_student_btn.setBorder(new javax.swing.border.MatteBorder(null));
        view_student_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view_student_btnActionPerformed(evt);
            }
        });
        jPanel4.add(view_student_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 170, 30));

        lg_btn.setText("logout");
        lg_btn.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel4.add(lg_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 510, 170, 30));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        jPanel5.setBackground(new java.awt.Color(0, 51, 204));
        jPanel5.setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("All Courses Available");

        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jTable1.setForeground(new java.awt.Color(0, 0, 0));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "id", "course", "year", "Description"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        course_addbutton.setText("Add");
        course_addbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_addbuttonActionPerformed(evt);
            }
        });

        course_update_button.setText("Update");
        course_update_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_update_buttonActionPerformed(evt);
            }
        });

        course_remove_button.setText("Remove");
        course_remove_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_remove_buttonActionPerformed(evt);
            }
        });

        course_searchbar.setBackground(new java.awt.Color(255, 255, 255));
        course_searchbar.setForeground(new java.awt.Color(0, 0, 0));
        course_searchbar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        search_coursebutton.setBackground(new java.awt.Color(102, 102, 102));
        search_coursebutton.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        search_coursebutton.setText("Search Course");
        search_coursebutton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jScrollPane1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(course_searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(search_coursebutton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(170, 170, 170)
                                .addComponent(course_addbutton)
                                .addGap(120, 120, 120)
                                .addComponent(course_update_button)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 115, Short.MAX_VALUE)
                                .addComponent(course_remove_button)))
                        .addGap(0, 155, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(course_searchbar)
                    .addComponent(search_coursebutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_addbutton)
                    .addComponent(course_update_button)
                    .addComponent(course_remove_button))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel1, "card2");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Registered Students");

        studentTable.setBackground(new java.awt.Color(255, 255, 255));
        studentTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        studentTable.setForeground(new java.awt.Color(0, 0, 0));
        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "fname", "lname", "age", "sex", "year", "course", "department"
            }
        ));
        jScrollPane2.setViewportView(studentTable);

        student_add_btn.setText("Add");

        student_update_btn.setText("Update");
        student_update_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_update_btnActionPerformed(evt);
            }
        });

        student_remove_btn.setText("Remove");

        student_searchbar.setBackground(new java.awt.Color(255, 255, 255));
        student_searchbar.setForeground(new java.awt.Color(0, 0, 0));
        student_searchbar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        student_searchbtn.setBackground(new java.awt.Color(102, 102, 102));
        student_searchbtn.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        student_searchbtn.setText("Search Student");
        student_searchbtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(170, 170, 170)
                                .addComponent(student_add_btn)
                                .addGap(120, 120, 120)
                                .addComponent(student_update_btn)
                                .addGap(115, 115, 115)
                                .addComponent(student_remove_btn))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(student_searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(student_searchbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 155, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(student_searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(student_searchbtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(student_remove_btn)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(student_add_btn)
                        .addComponent(student_update_btn)))
                .addGap(17, 17, 17))
        );

        jPanel5.add(jPanel2, "card3");

        jPanel3.setBackground(new java.awt.Color(0, 204, 204));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 795, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel3, "card4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vc_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vc_btnActionPerformed
    loadCourses();        
    }//GEN-LAST:event_vc_btnActionPerformed

    private void student_update_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_update_btnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_student_update_btnActionPerformed

    private void course_update_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_update_buttonActionPerformed
         int selectedRow = jTable1.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a course to update!");
        return;
    }

    int courseId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
    String oldName = jTable1.getValueAt(selectedRow, 1).toString();
    String oldYear = jTable1.getValueAt(selectedRow, 2).toString();
    String oldDesc = jTable1.getValueAt(selectedRow, 3).toString();

    // Let user edit values using simple dialogs
    String newName = JOptionPane.showInputDialog(this, "Edit Course Name:", oldName);
    if (newName == null) return;
    String newYear = JOptionPane.showInputDialog(this, "Edit Year:", oldYear);
    if (newYear == null) return;
    String newDesc = JOptionPane.showInputDialog(this, "Edit Description:", oldDesc);
    if (newDesc == null) return;

    try {
        String sql = "UPDATE courses SET course_name=?, course_year=?, description=? WHERE course_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newName);
        pst.setString(2, newYear);
        pst.setString(3, newDesc);
        pst.setInt(4, courseId);
        pst.executeUpdate();
        pst.close();

        JOptionPane.showMessageDialog(this, "✅ Course updated successfully!");
        loadCourses();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error updating course: " + e.getMessage());
    }
    }//GEN-LAST:event_course_update_buttonActionPerformed

    private void course_addbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_addbuttonActionPerformed
      add_course ac = new add_course();
      ac.setVisible(true);
    }//GEN-LAST:event_course_addbuttonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
    if (selectedRow != -1) {
        String course = jTable1.getValueAt(selectedRow, 1).toString();
        String year = jTable1.getValueAt(selectedRow, 2).toString();
        String desc = jTable1.getValueAt(selectedRow, 3).toString();

        // Optional: show selected details
        System.out.println("Selected course: " + course + " (" + year + ")");
    }
    }//GEN-LAST:event_jTable1MouseClicked

    private void view_student_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view_student_btnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_view_student_btnActionPerformed

    private void course_remove_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_remove_buttonActionPerformed
         int selectedRow = jTable1.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a course to remove!");
        return;
    }

    int courseId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
    String courseName = jTable1.getValueAt(selectedRow, 1).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to delete course: " + courseName + "?",
        "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        String sql = "DELETE FROM courses WHERE course_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, courseId);
        pst.executeUpdate();
        pst.close();

        JOptionPane.showMessageDialog(this, "✅ Course removed successfully!");
        loadCourses();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error deleting course: " + e.getMessage());
    }
    }//GEN-LAST:event_course_remove_buttonActionPerformed

    
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
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton course_addbutton;
    private javax.swing.JButton course_remove_button;
    private javax.swing.JTextField course_searchbar;
    private javax.swing.JButton course_update_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton lg_btn;
    private javax.swing.JButton search_coursebutton;
    private javax.swing.JTable studentTable;
    private javax.swing.JButton student_add_btn;
    private javax.swing.JButton student_remove_btn;
    private javax.swing.JTextField student_searchbar;
    private javax.swing.JButton student_searchbtn;
    private javax.swing.JButton student_update_btn;
    private javax.swing.JButton vc_btn;
    private javax.swing.JButton veh_btn;
    private javax.swing.JButton view_student_btn;
    // End of variables declaration//GEN-END:variables
}
