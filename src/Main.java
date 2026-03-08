import java.util.*;

/**
 * Course Enrollment Planner Pro
 * This version utilizes:
 * - HashMap: For O(1) storage of courses and students.
 * - HashSet: To ensure prerequisites and completions are unique.
 * - ArrayList: To sort data for display and filter missing courses.
 * - ArrayDeque: To maintain a history of actions for an "Undo" feature.
 */
public class Main {

    // Main database: Course -> Set of Prerequisites
    private static Map<String, Set<String>> courseDatabase = new HashMap<>();

    // Student database: Student Name -> Set of Completed Courses
    private static Map<String, Set<String>> studentRecords = new HashMap<>();

    // History stack: Stores the names of added courses for the Undo command
    private static Deque<String> actionHistory = new ArrayDeque<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Global Course Enrollment System ===");
        printHelp();

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toUpperCase();

            switch (command) {
                case "ADD_COURSE":
                    if (tokens.length >= 2) {
                        String course = tokens[1];
                        courseDatabase.putIfAbsent(course, new HashSet<>());
                        actionHistory.push(course); // Save to history for Undo
                        System.out.println("Course [" + course + "] registered.");
                    }
                    break;

                case "ADD_PREREQ":
                    if (tokens.length >= 3) {
                        addPrerequisite(tokens[1], tokens[2]);
                    }
                    break;

                case "COMPLETE":
                    if (tokens.length >= 3) {
                        String student = tokens[1];
                        String course = tokens[2];
                        studentRecords.putIfAbsent(student, new HashSet<>());
                        studentRecords.get(student).add(course);
                        System.out.println(student + " successfully completed " + course);
                    }
                    break;

                case "CAN_TAKE":
                    if (tokens.length >= 3) {
                        evaluateEnrollment(tokens[1], tokens[2]);
                    }
                    break;

                case "LIST":
                    displaySortedCourses();
                    break;

                case "UNDO":
                    performUndo();
                    break;

                case "HELP":
                    printHelp();
                    break;

                case "EXIT":
                    System.out.println("Closing system. Goodbye!");
                    return;

                default:
                    System.out.println("Unknown command. Type HELP for options.");
            }
        }
    }

    private static void addPrerequisite(String course, String prereq) {
        if (course.equals(prereq)) {
            System.out.println("Error: A course cannot require itself.");
            return;
        }
        courseDatabase.putIfAbsent(course, new HashSet<>());
        courseDatabase.putIfAbsent(prereq, new HashSet<>());
        courseDatabase.get(course).add(prereq);
        System.out.println("Requirement set: " + prereq + " is needed for " + course);
    }

    private static void evaluateEnrollment(String student, String course) {
        if (!courseDatabase.containsKey(course)) {
            System.out.println("YES (No requirements found for this course)");
            return;
        }

        Set<String> required = courseDatabase.get(course);
        Set<String> completed = studentRecords.getOrDefault(student, new HashSet<>());

        if (completed.containsAll(required)) {
            System.out.println("YES");
        } else {
            // Use ArrayList to collect and show exactly what is missing
            List<String> missing = new ArrayList<>();
            for (String req : required) {
                if (!completed.contains(req)) missing.add(req);
            }
            System.out.println("NO. Missing prerequisites: " + missing);
        }
    }

    private static void displaySortedCourses() {
        // Convert Map keys to ArrayList for sorting capabilities
        List<String> sortedList = new ArrayList<>(courseDatabase.keySet());
        Collections.sort(sortedList);
        System.out.println("System Course List (Sorted): " + sortedList);
    }

    private static void performUndo() {
        if (!actionHistory.isEmpty()) {
            String lastCourse = actionHistory.pop();
            courseDatabase.remove(lastCourse);
            System.out.println("Undo successful: Course [" + lastCourse + "] removed.");
        } else {
            System.out.println("Nothing to undo.");
        }
    }

    private static void printHelp() {
        System.out.println("Commands: ADD_COURSE <C>, ADD_PREREQ <C> <P>, COMPLETE <S> <C>, CAN_TAKE <S> <C>, LIST, UNDO, EXIT");
    }
}
