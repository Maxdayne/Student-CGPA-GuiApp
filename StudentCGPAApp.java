import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * StudentCGPAApp.java
 *
 * The GUI layer, built against your corrected Person / Course / Student
 * classes. This class only handles: reading input from Swing
 * components, validating it, calling into Student/Course, and
 * writing results back to the screen. All the actual academic logic
 * (CGPA math, the 5-course rule, grade-point conversion, profile
 * formatting) lives in Student.java and Course.java -- the GUI never
 * duplicates it.
 */
public class StudentCGPAApp extends JFrame {

    // ---- Data ----
    private ArrayList<Student> students;

    // ---- Student-details section ----
    private JTextField nameField;
    private JTextField studentIdField;
    private JComboBox<String> departmentBox;
    private JButton addStudentButton;

    // ---- Student selector ----
    private JComboBox<Student> studentSelector;

    // ---- Course-details section ----
    private JTextField courseCodeField;
    private JTextField courseTitleField;
    private JTextField creditUnitField;
    private JComboBox<String> gradeBox;
    private JButton registerCourseButton;
    private JTextArea coursesSoFarArea;

    // ---- Action buttons ----
    private JButton calculateButton;
    private JButton displayProfileButton;
    private JButton clearButton;
    private JButton exitButton;

    // ---- Output ----
    private JTextArea outputArea;

    public StudentCGPAApp() {
        super("University of Ibadan - Student CGPA Calculator");
        students = new ArrayList<>();

        buildUI();
        loadSampleStudents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 720);
        setLocationRelativeTo(null); // centers the window
        setVisible(true);
    }

    // ------------------------------------------------------------------
    //  UI CONSTRUCTION
    // ------------------------------------------------------------------
    private void buildUI() {
        setLayout(new BorderLayout(8, 8));

        JPanel formsPanel = new JPanel();
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS));
        formsPanel.add(buildStudentPanel());
        formsPanel.add(buildCoursePanel());
        formsPanel.add(buildActionPanel());

        add(formsPanel, BorderLayout.NORTH);
        add(buildOutputPanel(), BorderLayout.CENTER);

        wireEventListeners();
    }

    private JPanel buildStudentPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("1. Student Details"));

        nameField = new JTextField();
        studentIdField = new JTextField();
        departmentBox = new JComboBox<>(new String[]{"Computer Science", "Mathematics", "Physics"});
        addStudentButton = new JButton("Add New Student");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);

        panel.add(new JLabel("Department:"));
        panel.add(departmentBox);
        panel.add(new JLabel("")); // spacer
        panel.add(addStudentButton);

        return panel;
    }

    private JPanel buildCoursePanel() {
        JPanel outer = new JPanel(new BorderLayout(6, 6));
        outer.setBorder(BorderFactory.createTitledBorder("2. Register Courses (minimum 5 required)"));

        JPanel top = new JPanel(new GridLayout(1, 2, 6, 6));
        studentSelector = new JComboBox<>();
        top.add(new JLabel("Registering for student:"));
        top.add(studentSelector);
        outer.add(top, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(2, 4, 6, 6));
        courseCodeField = new JTextField();
        courseTitleField = new JTextField();
        creditUnitField = new JTextField();
        gradeBox = new JComboBox<>(new String[]{"A", "B", "C", "D", "E", "F"});
        registerCourseButton = new JButton("Register Course");

        fields.add(new JLabel("Course Code:"));
        fields.add(courseCodeField);
        fields.add(new JLabel("Course Title:"));
        fields.add(courseTitleField);

        fields.add(new JLabel("Credit Unit:"));
        fields.add(creditUnitField);
        fields.add(new JLabel("Grade:"));
        fields.add(gradeBox);
        outer.add(fields, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.add(registerCourseButton, BorderLayout.NORTH);
        coursesSoFarArea = new JTextArea(4, 40);
        coursesSoFarArea.setEditable(false);
        coursesSoFarArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        south.add(new JScrollPane(coursesSoFarArea), BorderLayout.CENTER);
        outer.add(south, BorderLayout.SOUTH);

        return outer;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("3. Actions"));

        calculateButton = new JButton("Calculate CGPA");
        displayProfileButton = new JButton("Display Profile");
        clearButton = new JButton("Clear Form");
        exitButton = new JButton("Exit");

        panel.add(calculateButton);
        panel.add(displayProfileButton);
        panel.add(clearButton);
        panel.add(exitButton);

        return panel;
    }

    private JScrollPane buildOutputPanel() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Student Profile Output"));
        return scroll;
    }

    // ------------------------------------------------------------------
    //  EVENT LISTENERS
    // ------------------------------------------------------------------
    private void wireEventListeners() {
        addStudentButton.addActionListener(this::onAddStudent);
        registerCourseButton.addActionListener(this::onRegisterCourse);
        calculateButton.addActionListener(this::onCalculateCGPA);
        displayProfileButton.addActionListener(this::onDisplayProfile);
        clearButton.addActionListener(this::onClearForm);
        exitButton.addActionListener(this::onExit);

        // Refresh the "courses registered so far" panel whenever the
        // selected student changes.
        studentSelector.addActionListener(e -> refreshCoursesSoFar());
    }

    private void onAddStudent(ActionEvent e) {
        String name = nameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String department = (String) departmentBox.getSelectedItem();

        // --- Input validation ---
        if (name.isEmpty() || studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name and Student ID cannot be empty.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Your Student constructor is (name, id, dept) -- matches here.
        Student student = new Student(name, studentId, department);
        students.add(student);
        studentSelector.addItem(student);
        studentSelector.setSelectedItem(student);

        nameField.setText("");
        studentIdField.setText("");

        JOptionPane.showMessageDialog(this,
                "Student added. Now register at least 5 courses for them below.",
                "Student Added", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onRegisterCourse(ActionEvent e) {
        Student selected = (Student) studentSelector.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Add or select a student before registering courses.",
                    "No Student Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = courseCodeField.getText().trim();
        String title = courseTitleField.getText().trim();
        String creditText = creditUnitField.getText().trim();
        String grade = (String) gradeBox.getSelectedItem();

        if (code.isEmpty() || title.isEmpty() || creditText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Course code, title and credit unit are all required.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- Granular exception handling: catch the specific
        //     exception (NumberFormatException), not a generic Exception.
        //     This is the ONLY place parsing from String -> int happens,
        //     which is exactly why Course's own constructor no longer
        //     needs to worry about NumberFormatException. ---
        int creditUnit;
        try {
            creditUnit = Integer.parseInt(creditText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Credit unit must be a whole number (e.g. 3).",
                    "Invalid Credit Unit", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Course's own constructor still guards against <= 0, so this
        // is also wrapped in case that IllegalArgumentException fires.
        try {
            Course course = new Course(code, title, creditUnit); // 3-arg constructor
            course.setGrade(grade);                              // grade set separately
            selected.registerCourse(course);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Invalid Course", JOptionPane.ERROR_MESSAGE);
            return;
        }

        courseCodeField.setText("");
        courseTitleField.setText("");
        creditUnitField.setText("");

        refreshCoursesSoFar();
    }

    private void onCalculateCGPA(ActionEvent e) {
        Student selected = (Student) studentSelector.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Select a student first.",
                    "No Student Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double cgpa = selected.calculateCGPA();
            JOptionPane.showMessageDialog(this,
                    String.format("%s's CGPA is %.2f", selected.getName(), cgpa),
                    "CGPA Calculated", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) {
            // Thrown by Student.calculateCGPA() when fewer than 5
            // courses are registered -- the assignment's required prompt.
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Cannot Calculate CGPA", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onDisplayProfile(ActionEvent e) {
        Student selected = (Student) studentSelector.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Select a student first.",
                    "No Student Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        outputArea.setText(selected.getFormattedProfile());
        outputArea.setCaretPosition(0);
    }

    private void onClearForm(ActionEvent e) {
        nameField.setText("");
        studentIdField.setText("");
        courseCodeField.setText("");
        courseTitleField.setText("");
        creditUnitField.setText("");
        gradeBox.setSelectedIndex(0);
        outputArea.setText("");
    }

    private void onExit(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ------------------------------------------------------------------
    //  HELPERS
    // ------------------------------------------------------------------
    private void refreshCoursesSoFar() {
        Student selected = (Student) studentSelector.getSelectedItem();
        if (selected == null) {
            coursesSoFarArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Course c : selected.getCourses()) {
            sb.append(String.format("%-10s %-22s CU:%-2d Grade:%s%n",
                    c.getCourseCode(), c.getCourseTitle(), c.getCreditUnits(), c.getGrade()));
        }
        sb.append(String.format("(%d course%s registered -- minimum 5 required for CGPA)",
                selected.getCourses().size(), selected.getCourses().size() == 1 ? "" : "s"));
        coursesSoFarArea.setText(sb.toString());
    }

    /**
     * Requirement 5: pre-populate three students across Computer
     * Science, Mathematics and Physics, each with 5 courses and a
     * computed CGPA, so the app is demo-ready the moment it launches.
     * Each Course is built with the 3-arg constructor, then given its
     * grade with setGrade() -- matching your Course class exactly.
     */
    private void loadSampleStudents() {
        Student s1 = new Student("John Ade", "UI/CSC/2026/001", "Computer Science");
        addGradedCourse(s1, "CSC201", "Data Structures", 3, "A");
        addGradedCourse(s1, "CSC203", "Computer Architecture", 3, "B");
        addGradedCourse(s1, "MTH201", "Mathematics II", 3, "A");
        addGradedCourse(s1, "CSC205", "Programming II", 3, "B");
        addGradedCourse(s1, "STA201", "Statistics", 2, "C");

        Student s2 = new Student("Amara Chukwu", "UI/MTH/2026/014", "Mathematics");
        addGradedCourse(s2, "MTH301", "Real Analysis", 3, "A");
        addGradedCourse(s2, "MTH303", "Abstract Algebra", 3, "B");
        addGradedCourse(s2, "MTH305", "Complex Analysis", 3, "A");
        addGradedCourse(s2, "STA301", "Probability Theory", 2, "B");
        addGradedCourse(s2, "CSC301", "Numerical Methods", 3, "C");

        Student s3 = new Student("Tunde Bakare", "UI/PHY/2026/027", "Physics");
        addGradedCourse(s3, "PHY201", "Classical Mechanics", 3, "B");
        addGradedCourse(s3, "PHY203", "Thermodynamics", 3, "A");
        addGradedCourse(s3, "PHY205", "Electromagnetism", 3, "B");
        addGradedCourse(s3, "MTH205", "Mathematical Methods", 3, "C");
        addGradedCourse(s3, "PHY207", "Optics", 2, "A");

        students.add(s1);
        students.add(s2);
        students.add(s3);

        for (Student s : students) {
            studentSelector.addItem(s);
        }
        studentSelector.setSelectedIndex(0);
        refreshCoursesSoFar();
    }

    // Small helper so loadSampleStudents() isn't six lines per course.
    private void addGradedCourse(Student student, String code, String title, int units, String grade) {
        Course course = new Course(code, title, units);
        course.setGrade(grade);
        student.registerCourse(course);
    }

    public static void main(String[] args) {
        // Swing components must be created/updated on the Event
        // Dispatch Thread, not on main() directly -- invokeLater hands
        // GUI construction off to that thread safely.
        SwingUtilities.invokeLater(StudentCGPAApp::new);
    }
}
