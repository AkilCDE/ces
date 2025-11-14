
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
        String sql = "SELECT course_code, course_name, instructor, capacity, schedule, date_created FROM courses";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) courseTABLE.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getString("instructor"),
                rs.getInt("capacity"),
                rs.getString("schedule"),
                rs.getString("date_created")
            });
        }

        rs.close();
        pst.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
    }
}
 
 public void loadCourseCatalog() {
    try {
        String sql = "SELECT course_code, course_name FROM courses";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) DispalyAllCourseTable.getModel();
        model.setRowCount(0); // clear existing rows

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("course_code"),
                rs.getString("course_name")
            });
        }

        rs.close();
        pst.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading course catalog: " + e.getMessage());
    }
}
 
 public void searchCatalog(String searchText) {
    try {
        String sql = "SELECT course_code, course_name FROM courses " +
                     "WHERE course_code = ? OR course_name LIKE ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, searchText);
        pst.setString(2, "%" + searchText + "%");
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) DispalyAllCourseTable.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("course_code"),
                rs.getString("course_name")
            });
        }

        rs.close();
        pst.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error searching catalog: " + e.getMessage());
    }
}
 
 public void sortCourses() {
    String sortOption = course_sortby.getSelectedItem().toString();

    String sql = "";

    switch (sortOption) {
        case "Course Code":
            sql = "SELECT * FROM courses ORDER BY course_code ASC";
            break;

        case "Course Name":
            sql = "SELECT * FROM courses ORDER BY course_name ASC";
            break;

        case "Capacity":
            sql = "SELECT * FROM courses ORDER BY capacity ASC";
            break;

        default:
            loadCourses(); // No sorting
            return;
    }

    try {
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) courseTABLE.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getString("instructor"),
                rs.getInt("capacity"),
                rs.getString("schedule"),
                rs.getString("date_created")
            });
        }

        rs.close();
        pst.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error sorting courses: " + e.getMessage());
    }
}

public void loadStudents() {
    try {
        String sql = "SELECT * FROM students ORDER BY student_id ASC";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("student_id"),
                rs.getString("full_name"),
                rs.getString("program"),
                rs.getString("year_level"),
                rs.getString("date_registered")
            });
        }

        pst.close();
        rs.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
    }
}

public void enrollStudentPopup() {
    // Step 1: Check if user selected a student
    int row = studentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a student first!");
        return;
    }

    String studentID = studentTable.getValueAt(row, 0).toString();
    String fullName = studentTable.getValueAt(row, 1).toString();

    try {
        // Step 2: Load available courses
        PreparedStatement pst = conn.prepareStatement(
                "SELECT course_code, course_name FROM courses"
        );
        ResultSet rs = pst.executeQuery();

        ArrayList<String> courseList = new ArrayList<>();
        while (rs.next()) {
            courseList.add(rs.getString("course_code") + " - " + rs.getString("course_name"));
        }

        rs.close();
        pst.close();

        if (courseList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses available!");
            return;
        }

        // Step 3: Pop-up to choose a course
        String selectedCourse = (String) JOptionPane.showInputDialog(
                this,
                "Enroll " + fullName + " in which course?",
                "Enroll Student",
                JOptionPane.QUESTION_MESSAGE,
                null,
                courseList.toArray(),
                courseList.get(0)
        );

        if (selectedCourse == null) return; // user cancelled

        String courseCode = selectedCourse.split(" - ")[0];

        // Step 4: Enroll student
        enrollStudentInCourse(studentID, courseCode);

        // Step 5: Refresh tables in enrollment panels
        loadEnrollmentsTable();
        loadEnrollmentHistoryTable();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

public void enrollStudentInCourse(String studentID, String courseCode) {
    try {
        // Prevent duplicate enrollment
        String check = "SELECT * FROM enrollments WHERE student_id=? AND course_code=?";
        PreparedStatement cst = conn.prepareStatement(check);
        cst.setString(1, studentID);
        cst.setString(2, courseCode);
        ResultSet r = cst.executeQuery();
        if (r.next()) {
            JOptionPane.showMessageDialog(this, "Student is already enrolled in this course!");
            return;
        }

        // Insert enrollment
        String sql = "INSERT INTO enrollments (student_id, course_code) VALUES (?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, studentID);
        pst.setString(2, courseCode);
        pst.executeUpdate();
        pst.close();

        // Insert history
        String hist = "INSERT INTO enrollment_history (student_id, course_code, action_type) VALUES (?, ?, 'ENROLL')";
        PreparedStatement pst2 = conn.prepareStatement(hist);
        pst2.setString(1, studentID);
        pst2.setString(2, courseCode);
        pst2.executeUpdate();
        pst2.close();

        JOptionPane.showMessageDialog(this, "Student enrolled successfully!");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error enrolling student: " + e.getMessage());
    }
}

public void loadEnrollmentsTable() {
    try {
        String sql = "SELECT e.student_id, s.full_name, " +
                     "GROUP_CONCAT(CONCAT(e.course_code, ' - ', c.course_name) SEPARATOR ', ') as enrolled_courses, " +
                     "MAX(e.date_enrolled) as last_enrolled " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "GROUP BY e.student_id, s.full_name " +
                     "ORDER BY e.student_id";

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) jTableEnrollments.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("student_id"),
                rs.getString("full_name"),
                rs.getString("enrolled_courses"),
                rs.getString("last_enrolled")
            });
        }

        rs.close();
        pst.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading enrollments: " + e.getMessage());
    }
}

public void loadEnrollmentHistoryTable() {
    try {
        String sql = "SELECT h.student_id, s.full_name, h.course_code, c.course_name, h.action_type, h.action_time " +
                     "FROM enrollment_history h " +
                     "JOIN students s ON h.student_id = s.student_id " +
                     "JOIN courses c ON h.course_code = c.course_code";

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) jtableHistory.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("student_id"),
                rs.getString("full_name"),
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getString("action_type"),
                rs.getString("action_time")
            });
        }

        rs.close();
        pst.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading enrollment history: " + e.getMessage());
    }
}

public void dropStudentFromCourse() {
    // Step 1: Check if user selected a student
    int row = jTableEnrollments.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a student to drop from courses!");
        return;
    }

    String studentID = jTableEnrollments.getValueAt(row, 0).toString();
    String studentName = jTableEnrollments.getValueAt(row, 1).toString();
    String enrolledCourses = jTableEnrollments.getValueAt(row, 2).toString();

    try {
        // Step 2: Get all courses this student is enrolled in
        String getCoursesSql = "SELECT course_code FROM enrollments WHERE student_id=?";
        PreparedStatement getCoursesStmt = conn.prepareStatement(getCoursesSql);
        getCoursesStmt.setString(1, studentID);
        ResultSet coursesRs = getCoursesStmt.executeQuery();

        ArrayList<String> courseList = new ArrayList<>();
        while (coursesRs.next()) {
            courseList.add(coursesRs.getString("course_code"));
        }
        coursesRs.close();
        getCoursesStmt.close();

        if (courseList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This student is not enrolled in any courses!");
            return;
        }

        // Step 3: Let user choose which course to drop
        String[] courseArray = courseList.toArray(new String[0]);
        String selectedCourse = (String) JOptionPane.showInputDialog(
            this,
            "Drop " + studentName + " from which course?",
            "Drop Student from Course",
            JOptionPane.QUESTION_MESSAGE,
            null,
            courseArray,
            courseArray[0]
        );

        if (selectedCourse == null) return; // user cancelled

        // Step 4: Confirm drop action
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to drop " + studentName + 
            " from course " + selectedCourse + "?",
            "Confirm Drop", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Step 5: Remove from enrollments table
        String deleteSql = "DELETE FROM enrollments WHERE student_id=? AND course_code=?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
        deleteStmt.setString(1, studentID);
        deleteStmt.setString(2, selectedCourse);
        deleteStmt.executeUpdate();
        deleteStmt.close();

        // Step 6: Add to enrollment history
        String historySql = "INSERT INTO enrollment_history (student_id, course_code, action_type) VALUES (?, ?, 'DROP')";
        PreparedStatement historyStmt = conn.prepareStatement(historySql);
        historyStmt.setString(1, studentID);
        historyStmt.setString(2, selectedCourse);
        historyStmt.executeUpdate();
        historyStmt.close();

        JOptionPane.showMessageDialog(this, "Student dropped from course successfully!");

        // Step 7: Refresh tables
        loadEnrollmentsTable();
        loadEnrollmentHistoryTable();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error dropping student: " + e.getMessage());
    }
}

public void viewStudentSchedule() {
    // Step 1: Check if user selected a student
    int row = jTableEnrollments.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a student first!");
        return;
    }

    String studentID = jTableEnrollments.getValueAt(row, 0).toString();
    String studentName = jTableEnrollments.getValueAt(row, 1).toString();

    try {
        // Step 2: Get all courses the student is enrolled in
        String sql = "SELECT c.course_code, c.course_name, c.schedule " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE e.student_id = ? " +
                     "ORDER BY c.course_code";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, studentID);
        ResultSet rs = pst.executeQuery();

        // Step 3: Build schedule message
        StringBuilder schedule = new StringBuilder();
        schedule.append("<html><b>Student: ").append(studentName).append(" (").append(studentID).append(")</b><br><br>");
        schedule.append("<b>Enrolled Courses:</b><br>");

        int courseCount = 0;
        while (rs.next()) {
            courseCount++;
            schedule.append("• ").append(rs.getString("course_code"))
                   .append(" - ").append(rs.getString("course_name"))
                   .append(" (").append(rs.getString("schedule")).append(")<br>");
        }

        if (courseCount == 0) {
            schedule.append("No courses enrolled.");
        } else {
            schedule.append("<br><b>Total Courses: ").append(courseCount).append("</b>");
        }

        schedule.append("</html>");

        rs.close();
        pst.close();

        // Step 4: Show in message dialog
        JOptionPane.showMessageDialog(this, schedule.toString(), 
                                    "Student Schedule", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading student schedule: " + e.getMessage());
    }
}
//constructor
    public Home() {
        
        initComponents();
         homeInstance = this;
         loadCourses();    
         loadCourseCatalog();   
         course_sortby.addActionListener(evt -> sortCourses());
         loadStudents();
         setLocationRelativeTo(null); // center form
         setResizable(false);  


    }  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NavigationPanel = new javax.swing.JPanel();
        vc_btn = new javax.swing.JButton();
        veh_btn = new javax.swing.JButton();
        view_student_btn = new javax.swing.JButton();
        lg_btn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        vcousecatalog = new javax.swing.JButton();
        vcourse_btn = new javax.swing.JButton();
        AllPanels = new javax.swing.JPanel();
        courseCatalogPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        DispalyAllCourseTable = new javax.swing.JTable();
        CoursesPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        courseTABLE = new javax.swing.JTable();
        course_searchbar = new javax.swing.JTextField();
        search_coursebutton = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        course_addbutton = new javax.swing.JButton();
        course_update_button = new javax.swing.JButton();
        course_remove_button = new javax.swing.JButton();
        course_sortby = new javax.swing.JComboBox<>();
        StudentPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable();
        student_searchbar = new javax.swing.JTextField();
        student_searchbtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        student_add_btn = new javax.swing.JButton();
        student_update_btn = new javax.swing.JButton();
        student_remove_btn = new javax.swing.JButton();
        student_enroll_btn = new javax.swing.JButton();
        EnrollementsPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableEnrollments = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        DropstudentBTN = new javax.swing.JButton();
        ViewStudent_btn = new javax.swing.JButton();
        EnrollmentHistoryPanel = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtableHistory = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        NavigationPanel.setBackground(new java.awt.Color(0, 51, 204));
        NavigationPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vc_btn.setBackground(new java.awt.Color(0, 51, 204));
        vc_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        vc_btn.setForeground(new java.awt.Color(0, 0, 0));
        vc_btn.setText("Enrollments");
        vc_btn.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)));
        vc_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vc_btnActionPerformed(evt);
            }
        });
        NavigationPanel.add(vc_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 170, 30));

        veh_btn.setBackground(new java.awt.Color(0, 51, 204));
        veh_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        veh_btn.setForeground(new java.awt.Color(0, 0, 0));
        veh_btn.setText("Enrollment History");
        veh_btn.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)));
        veh_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                veh_btnActionPerformed(evt);
            }
        });
        NavigationPanel.add(veh_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, 170, 30));

        view_student_btn.setBackground(new java.awt.Color(0, 51, 204));
        view_student_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        view_student_btn.setForeground(new java.awt.Color(0, 0, 0));
        view_student_btn.setText("Manage Students");
        view_student_btn.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)));
        view_student_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view_student_btnActionPerformed(evt);
            }
        });
        NavigationPanel.add(view_student_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 170, 30));

        lg_btn.setBackground(new java.awt.Color(0, 51, 204));
        lg_btn.setText("logout");
        lg_btn.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 0, 0)));
        lg_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lg_btnActionPerformed(evt);
            }
        });
        NavigationPanel.add(lg_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 510, 170, 30));

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

        NavigationPanel.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        vcousecatalog.setBackground(new java.awt.Color(0, 51, 204));
        vcousecatalog.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        vcousecatalog.setForeground(new java.awt.Color(0, 0, 0));
        vcousecatalog.setText("Course Catalog");
        vcousecatalog.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)));
        vcousecatalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vcousecatalogActionPerformed(evt);
            }
        });
        NavigationPanel.add(vcousecatalog, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 170, 30));

        vcourse_btn.setBackground(new java.awt.Color(0, 51, 204));
        vcourse_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        vcourse_btn.setForeground(new java.awt.Color(0, 0, 0));
        vcourse_btn.setText("Manage Courses");
        vcourse_btn.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)));
        vcourse_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vcourse_btnActionPerformed(evt);
            }
        });
        NavigationPanel.add(vcourse_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 170, 30));

        AllPanels.setBackground(new java.awt.Color(0, 51, 204));
        AllPanels.setLayout(new java.awt.CardLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel9.setBackground(new java.awt.Color(0, 51, 204));
        jPanel9.setPreferredSize(new java.awt.Dimension(162, 44));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Course Catalog");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        DispalyAllCourseTable.setBackground(new java.awt.Color(255, 255, 255));
        DispalyAllCourseTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        DispalyAllCourseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Course Code", "Course Name"
            }
        ));
        jScrollPane3.setViewportView(DispalyAllCourseTable);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 661, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout courseCatalogPanelLayout = new javax.swing.GroupLayout(courseCatalogPanel);
        courseCatalogPanel.setLayout(courseCatalogPanelLayout);
        courseCatalogPanelLayout.setHorizontalGroup(
            courseCatalogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        courseCatalogPanelLayout.setVerticalGroup(
            courseCatalogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        AllPanels.add(courseCatalogPanel, "card5");

        CoursesPanel.setBackground(new java.awt.Color(255, 255, 255));

        courseTABLE.setBackground(new java.awt.Color(255, 255, 255));
        courseTABLE.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        courseTABLE.setForeground(new java.awt.Color(0, 0, 0));
        courseTABLE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Course Code", "Course Name", "Instructor", "Capacity", "Schedule", "Date created"
            }
        ));
        courseTABLE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                courseTABLEMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(courseTABLE);
        if (courseTABLE.getColumnModel().getColumnCount() > 0) {
            courseTABLE.getColumnModel().getColumn(0).setHeaderValue("Course Code");
        }

        course_searchbar.setBackground(new java.awt.Color(255, 255, 255));
        course_searchbar.setForeground(new java.awt.Color(0, 0, 0));
        course_searchbar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        course_searchbar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                course_searchbarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                course_searchbarKeyTyped(evt);
            }
        });

        search_coursebutton.setBackground(new java.awt.Color(255, 255, 255));
        search_coursebutton.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        search_coursebutton.setForeground(new java.awt.Color(0, 0, 0));
        search_coursebutton.setText("Search Course");
        search_coursebutton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        search_coursebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_coursebuttonActionPerformed(evt);
            }
        });

        jPanel10.setBackground(new java.awt.Color(0, 51, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("All Courses Available");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(0, 51, 204));

        course_addbutton.setBackground(new java.awt.Color(0, 204, 0));
        course_addbutton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        course_addbutton.setForeground(new java.awt.Color(0, 0, 0));
        course_addbutton.setText("Add");
        course_addbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_addbuttonActionPerformed(evt);
            }
        });

        course_update_button.setBackground(new java.awt.Color(0, 204, 0));
        course_update_button.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        course_update_button.setForeground(new java.awt.Color(0, 0, 0));
        course_update_button.setText("Update");
        course_update_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_update_buttonActionPerformed(evt);
            }
        });

        course_remove_button.setBackground(new java.awt.Color(255, 51, 51));
        course_remove_button.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        course_remove_button.setForeground(new java.awt.Color(0, 0, 0));
        course_remove_button.setText("Remove");
        course_remove_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_remove_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(course_addbutton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(course_update_button)
                .addGap(108, 108, 108)
                .addComponent(course_remove_button)
                .addGap(51, 51, 51))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_addbutton)
                    .addComponent(course_update_button)
                    .addComponent(course_remove_button))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        course_sortby.setBackground(new java.awt.Color(255, 255, 255));
        course_sortby.setForeground(new java.awt.Color(0, 0, 0));
        course_sortby.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort By", "Course Code", "Course Name", "Capacity" }));
        course_sortby.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        course_sortby.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_sortbyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CoursesPanelLayout = new javax.swing.GroupLayout(CoursesPanel);
        CoursesPanel.setLayout(CoursesPanelLayout);
        CoursesPanelLayout.setHorizontalGroup(
            CoursesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CoursesPanelLayout.createSequentialGroup()
                .addGroup(CoursesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CoursesPanelLayout.createSequentialGroup()
                        .addGroup(CoursesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CoursesPanelLayout.createSequentialGroup()
                                .addGap(119, 119, 119)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(CoursesPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(course_searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(search_coursebutton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(course_sortby, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 127, Short.MAX_VALUE))
                    .addGroup(CoursesPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(CoursesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        CoursesPanelLayout.setVerticalGroup(
            CoursesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CoursesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(CoursesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(course_searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search_coursebutton)
                    .addComponent(course_sortby, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        AllPanels.add(CoursesPanel, "card2");

        StudentPanel.setBackground(new java.awt.Color(255, 255, 255));

        studentTable.setBackground(new java.awt.Color(255, 255, 255));
        studentTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        studentTable.setForeground(new java.awt.Color(0, 0, 0));
        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Student Id", "Fullname", "Program", "Year Level", "Date Registered"
            }
        ));
        jScrollPane2.setViewportView(studentTable);

        student_searchbar.setBackground(new java.awt.Color(255, 255, 255));
        student_searchbar.setForeground(new java.awt.Color(0, 0, 0));
        student_searchbar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        student_searchbar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                student_searchbarKeyReleased(evt);
            }
        });

        student_searchbtn.setBackground(new java.awt.Color(255, 255, 255));
        student_searchbtn.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        student_searchbtn.setForeground(new java.awt.Color(0, 0, 0));
        student_searchbtn.setText("Search Student");
        student_searchbtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        student_searchbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_searchbtnActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 51, 204));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Registered Students");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(0, 51, 204));

        student_add_btn.setBackground(new java.awt.Color(0, 204, 0));
        student_add_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        student_add_btn.setForeground(new java.awt.Color(0, 0, 0));
        student_add_btn.setText("Add");
        student_add_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_add_btnActionPerformed(evt);
            }
        });

        student_update_btn.setBackground(new java.awt.Color(0, 204, 0));
        student_update_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        student_update_btn.setForeground(new java.awt.Color(0, 0, 0));
        student_update_btn.setText("Update");
        student_update_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_update_btnActionPerformed(evt);
            }
        });

        student_remove_btn.setBackground(new java.awt.Color(255, 51, 51));
        student_remove_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        student_remove_btn.setForeground(new java.awt.Color(0, 0, 0));
        student_remove_btn.setText("Remove");
        student_remove_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_remove_btnActionPerformed(evt);
            }
        });

        student_enroll_btn.setBackground(new java.awt.Color(0, 204, 0));
        student_enroll_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        student_enroll_btn.setForeground(new java.awt.Color(0, 0, 0));
        student_enroll_btn.setText("Enroll Student");
        student_enroll_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                student_enroll_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(student_enroll_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                .addComponent(student_add_btn)
                .addGap(45, 45, 45)
                .addComponent(student_update_btn)
                .addGap(40, 40, 40)
                .addComponent(student_remove_btn)
                .addGap(32, 32, 32))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(student_add_btn)
                    .addComponent(student_update_btn)
                    .addComponent(student_remove_btn)
                    .addComponent(student_enroll_btn))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout StudentPanelLayout = new javax.swing.GroupLayout(StudentPanel);
        StudentPanel.setLayout(StudentPanelLayout);
        StudentPanelLayout.setHorizontalGroup(
            StudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StudentPanelLayout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(96, Short.MAX_VALUE))
            .addGroup(StudentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(StudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(StudentPanelLayout.createSequentialGroup()
                        .addComponent(student_searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(student_searchbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        StudentPanelLayout.setVerticalGroup(
            StudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StudentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(StudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(student_searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(student_searchbtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        AllPanels.add(StudentPanel, "card3");

        EnrollementsPanel.setBackground(new java.awt.Color(255, 255, 255));

        jTableEnrollments.setBackground(new java.awt.Color(255, 255, 255));
        jTableEnrollments.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTableEnrollments.setForeground(new java.awt.Color(0, 0, 0));
        jTableEnrollments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Student ID", "Full Name", "Enrolled Courses", "Last Enrolled"
            }
        ));
        jScrollPane5.setViewportView(jTableEnrollments);

        jPanel4.setBackground(new java.awt.Color(0, 51, 204));

        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        DropstudentBTN.setText("Drop Student ");
        DropstudentBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DropstudentBTNActionPerformed(evt);
            }
        });

        ViewStudent_btn.setText("View Student");
        ViewStudent_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewStudent_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout EnrollementsPanelLayout = new javax.swing.GroupLayout(EnrollementsPanel);
        EnrollementsPanel.setLayout(EnrollementsPanelLayout);
        EnrollementsPanelLayout.setHorizontalGroup(
            EnrollementsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
            .addGroup(EnrollementsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(EnrollementsPanelLayout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(ViewStudent_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 205, Short.MAX_VALUE)
                .addComponent(DropstudentBTN)
                .addGap(204, 204, 204))
        );
        EnrollementsPanelLayout.setVerticalGroup(
            EnrollementsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EnrollementsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75)
                .addGroup(EnrollementsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DropstudentBTN)
                    .addComponent(ViewStudent_btn))
                .addContainerGap(136, Short.MAX_VALUE))
        );

        AllPanels.add(EnrollementsPanel, "card4");

        EnrollmentHistoryPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jtableHistory.setBackground(new java.awt.Color(255, 255, 255));
        jtableHistory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jtableHistory.setForeground(new java.awt.Color(0, 0, 0));
        jtableHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Student Id", "Fullname", "Course Code", "Course Name", "Action Type", "Action Time"
            }
        ));
        jScrollPane4.setViewportView(jtableHistory);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );

        javax.swing.GroupLayout EnrollmentHistoryPanelLayout = new javax.swing.GroupLayout(EnrollmentHistoryPanel);
        EnrollmentHistoryPanel.setLayout(EnrollmentHistoryPanelLayout);
        EnrollmentHistoryPanelLayout.setHorizontalGroup(
            EnrollmentHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        EnrollmentHistoryPanelLayout.setVerticalGroup(
            EnrollmentHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        AllPanels.add(EnrollmentHistoryPanel, "card6");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(NavigationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AllPanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(NavigationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(AllPanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vc_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vc_btnActionPerformed
    
      AllPanels.removeAll();
    AllPanels.add(EnrollementsPanel);
    AllPanels.repaint();
    AllPanels.revalidate();

    loadEnrollmentsTable();         
    }//GEN-LAST:event_vc_btnActionPerformed

    private void student_update_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_update_btnActionPerformed
        int selectedRow = studentTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a student to update!");
        return;
    }

    String studentID = studentTable.getValueAt(selectedRow, 0).toString();
    String oldName = studentTable.getValueAt(selectedRow, 1).toString();
    String oldProgram = studentTable.getValueAt(selectedRow, 2).toString();
    String oldYear = studentTable.getValueAt(selectedRow, 3).toString();

    // Pop-up inputs for new values
    String newName = JOptionPane.showInputDialog(this, "Edit Full Name:", oldName);
    if (newName == null) return;

    String newProgram = JOptionPane.showInputDialog(this, "Edit Program:", oldProgram);
    if (newProgram == null) return;

    String newYear = JOptionPane.showInputDialog(this, "Edit Year Level:", oldYear);
    if (newYear == null) return;

    try {
        String sql = "UPDATE students SET fullname=?, program=?, year_level=? WHERE student_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);

        pst.setString(1, newName);
        pst.setString(2, newProgram);
        pst.setString(3, newYear);
        pst.setString(4, studentID);

        pst.executeUpdate();
        pst.close();

        JOptionPane.showMessageDialog(this, "✅ Student updated successfully!");
        loadStudents();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error updating student: " + e.getMessage());
    }
    }//GEN-LAST:event_student_update_btnActionPerformed

    private void course_update_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_update_buttonActionPerformed
         int selectedRow = courseTABLE.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a course to update!");
        return;
    }

    String courseCode = courseTABLE.getValueAt(selectedRow, 0).toString();
    String oldName = courseTABLE.getValueAt(selectedRow, 1).toString();
    String oldInstructor = courseTABLE.getValueAt(selectedRow, 2).toString();
    String oldCapacity = courseTABLE.getValueAt(selectedRow, 3).toString();
    String oldSchedule = courseTABLE.getValueAt(selectedRow, 4).toString();

    String newName = JOptionPane.showInputDialog(this, "Edit Course Name:", oldName);
    if (newName == null) return;

    String newInstructor = JOptionPane.showInputDialog(this, "Edit Instructor:", oldInstructor);
    if (newInstructor == null) return;

    String newCapacity = JOptionPane.showInputDialog(this, "Edit Capacity:", oldCapacity);
    if (newCapacity == null) return;

    String newSchedule = JOptionPane.showInputDialog(this, "Edit Schedule:", oldSchedule);
    if (newSchedule == null) return;

    try {
        String sql = "UPDATE courses SET course_name=?, instructor=?, capacity=?, schedule=? WHERE course_code=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newName);
        pst.setString(2, newInstructor);
        pst.setInt(3, Integer.parseInt(newCapacity));
        pst.setString(4, newSchedule);
        pst.setString(5, courseCode);
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

    private void courseTABLEMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_courseTABLEMouseClicked
        int selectedRow = courseTABLE.getSelectedRow();
    if (selectedRow != -1) {
        String course = courseTABLE.getValueAt(selectedRow, 1).toString();
        String year = courseTABLE.getValueAt(selectedRow, 2).toString();
        String desc = courseTABLE.getValueAt(selectedRow, 3).toString();

        // Optional: show selected details
        System.out.println("Selected course: " + course + " (" + year + ")");
    }
    }//GEN-LAST:event_courseTABLEMouseClicked

    private void view_student_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view_student_btnActionPerformed
    AllPanels.removeAll();
    AllPanels.add(StudentPanel);
    AllPanels.repaint();
    AllPanels.revalidate();

    loadStudents();
    }//GEN-LAST:event_view_student_btnActionPerformed

    private void course_remove_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_remove_buttonActionPerformed
         int selectedRow = courseTABLE.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a course to delete!");
        return;
    }

    String courseCode = courseTABLE.getValueAt(selectedRow, 0).toString();
    String courseName = courseTABLE.getValueAt(selectedRow, 1).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to delete: " + courseName + "?",
        "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        String sql = "DELETE FROM courses WHERE course_code=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, courseCode);
        pst.executeUpdate();
        pst.close();

        JOptionPane.showMessageDialog(this, "✅ Course deleted successfully!");
        loadCourses();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error deleting course: " + e.getMessage());
    }
    }//GEN-LAST:event_course_remove_buttonActionPerformed

    private void search_coursebuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_coursebuttonActionPerformed
        String searchText = course_searchbar.getText().trim();

    if (searchText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter a Course Code or Name to search!");
        loadCourses(); // show all if empty
        return;
    }

    try {
        String sql = "SELECT course_code, course_name, instructor, capacity, schedule, date_created " +
                     "FROM courses WHERE course_code = ? OR course_name LIKE ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, searchText);
        pst.setString(2, "%" + searchText + "%"); // partial match for names

        ResultSet rs = pst.executeQuery();
        DefaultTableModel model = (DefaultTableModel) courseTABLE.getModel();
        model.setRowCount(0);

        boolean hasResults = false;
        while (rs.next()) {
            hasResults = true;
            model.addRow(new Object[]{
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getString("instructor"),
                rs.getInt("capacity"),
                rs.getString("schedule"),
                rs.getString("date_created")
            });
        }

        if (!hasResults) {
            JOptionPane.showMessageDialog(this, "No course found for: " + searchText);
        }

        rs.close();
        pst.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error searching course: " + e.getMessage());
    }
    }//GEN-LAST:event_search_coursebuttonActionPerformed

    private void student_searchbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_searchbtnActionPerformed
        String searchText = student_searchbar.getText().trim();

    if (searchText.isEmpty()) {
        loadStudents();
        return;
    }

    try {
        String sql = "SELECT * FROM students WHERE student_id = ? OR full_name LIKE ?";
        PreparedStatement pst = conn.prepareStatement(sql);

        pst.setString(1, searchText);
        pst.setString(2, "%" + searchText + "%");

        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("student_id"),
                rs.getString("full_name"),
                rs.getString("program"),
                rs.getString("year_level"),
                rs.getString("date_registered")
            });
        }

        rs.close();
        pst.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error searching student: " + e.getMessage());
    }
    }//GEN-LAST:event_student_searchbtnActionPerformed

    private void student_add_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_add_btnActionPerformed
       AddStudent add = new AddStudent();
    add.setVisible(true);
    }//GEN-LAST:event_student_add_btnActionPerformed

    private void student_remove_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_remove_btnActionPerformed
        int row = studentTable.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a student to delete!");
        return;
    }

    String studentID = studentTable.getValueAt(row, 0).toString();
    String fullname = studentTable.getValueAt(row, 1).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
        "Delete student: " + fullname + "?",
        "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        String sql = "DELETE FROM students WHERE student_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);

        pst.setString(1, studentID);
        pst.executeUpdate();

        pst.close();
        JOptionPane.showMessageDialog(this, "Student removed successfully!");
        loadStudents();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage());
    }
    }//GEN-LAST:event_student_remove_btnActionPerformed

    private void lg_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lg_btnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lg_btnActionPerformed

    private void vcousecatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vcousecatalogActionPerformed
    AllPanels.removeAll();
    AllPanels.add(courseCatalogPanel);
    AllPanels.repaint();
    AllPanels.revalidate();

    loadCourseCatalog();
    }//GEN-LAST:event_vcousecatalogActionPerformed

    private void vcourse_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vcourse_btnActionPerformed
    AllPanels.removeAll();
    AllPanels.add(CoursesPanel);
    AllPanels.repaint();
    AllPanels.revalidate();

    loadCourses();
    }//GEN-LAST:event_vcourse_btnActionPerformed

    private void course_searchbarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_course_searchbarKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_course_searchbarKeyTyped

    private void course_searchbarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_course_searchbarKeyReleased
         if (course_searchbar.getText().trim().isEmpty()) {
            loadCourses(); // reload all when cleared
        }
    }//GEN-LAST:event_course_searchbarKeyReleased

    private void course_sortbyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_course_sortbyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_course_sortbyActionPerformed

    private void student_searchbarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_student_searchbarKeyReleased
        if (student_searchbar.getText().trim().isEmpty()) {
            loadStudents();
        }// reload all when cleared
    }//GEN-LAST:event_student_searchbarKeyReleased

    private void student_enroll_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_student_enroll_btnActionPerformed
        enrollStudentPopup();
    }//GEN-LAST:event_student_enroll_btnActionPerformed

    private void veh_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_veh_btnActionPerformed
        AllPanels.removeAll();
    AllPanels.add(EnrollmentHistoryPanel);
    AllPanels.repaint();
    AllPanels.revalidate();

    loadEnrollmentHistoryTable();
    }//GEN-LAST:event_veh_btnActionPerformed

    private void DropstudentBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DropstudentBTNActionPerformed
 dropStudentFromCourse();      

    }//GEN-LAST:event_DropstudentBTNActionPerformed

    private void ViewStudent_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewStudent_btnActionPerformed
      viewStudentSchedule();
    }//GEN-LAST:event_ViewStudent_btnActionPerformed

    
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
    private javax.swing.JPanel AllPanels;
    private javax.swing.JPanel CoursesPanel;
    private javax.swing.JTable DispalyAllCourseTable;
    private javax.swing.JButton DropstudentBTN;
    private javax.swing.JPanel EnrollementsPanel;
    private javax.swing.JPanel EnrollmentHistoryPanel;
    private javax.swing.JPanel NavigationPanel;
    private javax.swing.JPanel StudentPanel;
    private javax.swing.JButton ViewStudent_btn;
    private javax.swing.JPanel courseCatalogPanel;
    private javax.swing.JTable courseTABLE;
    private javax.swing.JButton course_addbutton;
    private javax.swing.JButton course_remove_button;
    private javax.swing.JTextField course_searchbar;
    private javax.swing.JComboBox<String> course_sortby;
    private javax.swing.JButton course_update_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTableEnrollments;
    private javax.swing.JTable jtableHistory;
    private javax.swing.JButton lg_btn;
    private javax.swing.JButton search_coursebutton;
    private javax.swing.JTable studentTable;
    private javax.swing.JButton student_add_btn;
    private javax.swing.JButton student_enroll_btn;
    private javax.swing.JButton student_remove_btn;
    private javax.swing.JTextField student_searchbar;
    private javax.swing.JButton student_searchbtn;
    private javax.swing.JButton student_update_btn;
    private javax.swing.JButton vc_btn;
    private javax.swing.JButton vcourse_btn;
    private javax.swing.JButton vcousecatalog;
    private javax.swing.JButton veh_btn;
    private javax.swing.JButton view_student_btn;
    // End of variables declaration//GEN-END:variables
}
