public class Course {

    private String courseCode;
    private String courseTitle;
    private int creditUnits;
    private String grade;

    // units arrives here as a real int -- any "is this actually a
    // number" parsing belongs in the GUI's text field handler, not
    // here. This constructor's only job is to enforce the business
    // rule: units must be positive.
    public Course(String code, String title, int units) {
        this.courseCode = code;
        this.courseTitle = title;
        if (units <= 0) {
            throw new IllegalArgumentException("Credit units must be a positive integer");
        }
        this.creditUnits = units;
    }

    public String getCourseCode() {
        return this.courseCode;
    }

    public void setCourseCode(String code) {
        this.courseCode = code;
    }

    public String getCourseTitle() {
        return this.courseTitle;
    }

    public void setCourseTitle(String title) {
        this.courseTitle = title;
    }

    public int getCreditUnits() {
        return this.creditUnits;
    }

    public void setCreditUnits(int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("Credit units must be a positive integer");
        }
        this.creditUnits = units;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    // Moved here from Student: converting a letter grade to a point
    // value is the Course's own business, not the Student's.
    public int getGradePoint() {
        switch (this.grade.toUpperCase()) {
            case "A": return 5;
            case "B": return 4;
            case "C": return 3;
            case "D": return 2;
            case "E": return 1;
            case "F": return 0;
            default:
                throw new IllegalArgumentException("Invalid grade '" + grade + "'. Must be A-F.");
        }
    }
}
