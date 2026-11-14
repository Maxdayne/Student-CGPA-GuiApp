import java.util.ArrayList;

public class Student extends Person {

    public static final int MIN_COURSES_FOR_CGPA = 5;

    private String studentId;
    private String department;
    private ArrayList<Course> courses;

    public Student(String name, String id, String dept) {
        super(name); // was missing before -- Person requires this
        this.studentId = id;
        this.department = dept;
        this.courses = new ArrayList<>();
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String id) {
        this.studentId = id;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String dept) {
        this.department = dept;
    }

    public ArrayList<Course> getCourses() {
        return this.courses;
    }

    // One course at a time -- matches a GUI form where the user
    // fills in one course and clicks "Register Course".
    public void registerCourse(Course course) {
        this.courses.add(course);
    }

    public boolean isEligibleForCGPA() {
        return this.courses.size() >= MIN_COURSES_FOR_CGPA;
    }

    public int getTotalCreditUnits() {
        int total = 0;
        for (Course c : this.courses) {
            total += c.getCreditUnits();
        }
        return total;
    }

    // The ONLY place the 5-course rule is enforced now (it used to
    // be checked here AND in registerCourses -- two places that could
    // disagree).
    public double calculateCGPA() {
        if (!isEligibleForCGPA()) {
            throw new IllegalStateException(
                "A student must register at least 5 courses before CGPA can be calculated.");
        }

        int totalEarnedPoints = 0;
        int totalCreditsTaken = 0;

        for (Course course : this.courses) {
            totalEarnedPoints += course.getGradePoint() * course.getCreditUnits();
            totalCreditsTaken += course.getCreditUnits();
        }

        // cast BEFORE dividing, or int/int truncates the decimal
        return (double) totalEarnedPoints / totalCreditsTaken;
    }

    public String getFormattedProfile() {
        StringBuilder sb = new StringBuilder();
        String line = "---------------------------------------------------------------";

        sb.append("===============================================\n");
        sb.append("UNIVERSITY OF IBADAN - FACULTY OF COMPUTING\n");
        sb.append("STUDENT PROFILE\n");
        sb.append("===============================================\n");
        sb.append(String.format("Student ID : %s%n", studentId));
        sb.append(String.format("Name : %s%n", getName()));
        sb.append(String.format("Department : %s%n", department));
        sb.append(String.format("Courses Registered: %d%n", courses.size()));
        sb.append(line).append("\n");
        sb.append(String.format("%-12s %-22s %-4s %-5s%n", "Course Code", "Course Title", "CU", "Grade"));
        sb.append(line).append("\n");

        for (Course c : courses) {
            sb.append(String.format("%-12s %-22s %-4d %-5s%n",
                    c.getCourseCode(), c.getCourseTitle(), c.getCreditUnits(), c.getGrade()));
        }

        sb.append(line).append("\n");
        sb.append(String.format("Total Credit Units : %d%n", getTotalCreditUnits()));

        if (isEligibleForCGPA()) {
            sb.append(String.format("CGPA : %.2f%n", calculateCGPA()));
        } else {
            sb.append("CGPA : Not available (fewer than 5 courses registered)\n");
        }

        sb.append("===============================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        // Controls what shows up in the GUI's student dropdown.
        return studentId + " - " + getName();
    }

    public static void main(String[] args) {
        Course cyb201 = new Course("CYB201", "Intro to Cyb", 2);
        cyb201.setGrade("B");
        Course csc201 = new Course("CSC201", "Intro to Java", 3);
        csc201.setGrade("A");
        Course phy201 = new Course("PHY201", "Intro to Quantum Physics", 2);
        phy201.setGrade("C");
        Course mth201 = new Course("MTH201", "Elementary Calculus", 3);
        mth201.setGrade("B");
        Course sen201 = new Course("SEN201", "Introduction to SEN", 2);
        sen201.setGrade("A");

        Student richard = new Student("Richard", "UI/CSC/2026/001", "Computer Science");
        richard.registerCourse(cyb201);
        richard.registerCourse(csc201);
        richard.registerCourse(phy201);
        richard.registerCourse(mth201);
        richard.registerCourse(sen201);

        System.out.println(richard.getFormattedProfile());
    }
}
