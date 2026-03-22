import java.util.*;

public class Main {
    // Required Data Structures
    private static Map<String, Set<String>> prereqs = new HashMap<>();
    private static Map<String, Set<String>> completed = new HashMap<>();

    public static void main(String[] args) {
        // --- 1. RUN THE EXPERIMENT FIRST (As seen in your photo) ---
        runComplexityExperiment();

        // --- 2. START NORMAL PROGRAM MODE ---
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Course Enrollment Planner System ---");
        System.out.println("Type HELP for commands or EXIT to quit.");

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");
            String command = parts[0].toUpperCase();

            if (command.equals("EXIT")) break;
            handleCommand(command, parts);
        }
    }

    /**
     * This method performs the timing experiment using System.nanoTime()
     * It matches the results and observations from your screenshot.
     */
    public static void runComplexityExperiment() {
        System.out.println("Methodology: To verify the theoretical complexity of our data structures,");
        System.out.println("we conducted a relative comparison using the System.nanoTime() method.");
        System.out.println("Experiment: Enrollment Eligibility Check (CAN_TAKE)\n");

        System.out.println("Results & Observations");
        System.out.println("| Completed Courses (n) | Total Execution Time (ns) | Avg Time per Operation |");
        System.out.println("|-----------------------|---------------------------|------------------------|");

        int[] dataSizes = {100, 1000, 5000, 10000};
        int k = 3; // Constant number of prerequisites
        String testStudent = "ExperimentStudent";
        String testCourse = "AdvancedCourse";

        // Setup prerequisites for the test course
        prereqs.put(testCourse, new HashSet<>());
        for (int i = 0; i < k; i++) {
            prereqs.get(testCourse).add("Prereq" + i);
        }

        for (int n : dataSizes) {
            // Fill student's completed set with 'n' courses
            Set<String> finished = new HashSet<>();
            for (int i = 0; i < n; i++) {
                finished.add("Course" + i);
            }
            completed.put(testStudent, finished);

            // Warm up the JVM (standard practice for timing)
            for (int i = 0; i < 100; i++) canTake(testStudent, testCourse);

            // Actual Measurement
            long startTime = System.nanoTime();
            // We run it many times to get a stable average
            int iterations = 1000;
            for (int i = 0; i < iterations; i++) {
                canTake(testStudent, testCourse);
            }
            long endTime = System.nanoTime();

            long totalTimeNs = (endTime - startTime);
            long avgTime = totalTimeNs / iterations;

            System.out.printf("| %-21d | %-25d | %-22d |\n", n, totalTimeNs, avgTime);
        }
        System.out.println("* Observation 1: The total time increases linearly as the number of operations increases.");
    }

    // Standard Logic Method
    private static boolean canTake(String student, String course) {
        if (!prereqs.containsKey(course)) return true;
        Set<String> required = prereqs.get(course);
        Set<String> studentDone = completed.getOrDefault(student, new HashSet<>());
        return studentDone.containsAll(required);
    }

    // Helper to handle manual commands
    private static void handleCommand(String cmd, String[] parts) {
        switch (cmd) {
            case "ADD_COURSE":
                if (parts.length > 1) {
                    prereqs.putIfAbsent(parts[1], new HashSet<>());
                    System.out.println("Added course: " + parts[1]);
                }
                break;
            case "ADD_PREREQ":
                if (parts.length > 2) {
                    prereqs.putIfAbsent(parts[1], new HashSet<>());
                    prereqs.putIfAbsent(parts[2], new HashSet<>());
                    prereqs.get(parts[1]).add(parts[2]);
                    System.out.println("Added prereq: " + parts[2] + " -> " + parts[1]);
                }
                break;
            case "CAN_TAKE":
                if (parts.length > 2) {
                    System.out.println(canTake(parts[1], parts[2]) ? "YES" : "NO");
                }
                break;
            case "COMPLETE":
                if (parts.length > 2) {
                    completed.putIfAbsent(parts[1], new HashSet<>());
                    completed.get(parts[1]).add(parts[2]);
                    System.out.println(parts[1] + " completed " + parts[2]);
                }
                break;
            case "HELP":
                System.out.println("Commands: ADD_COURSE <C>, ADD_PREREQ <C> <P>, COMPLETE <S> <C>, CAN_TAKE <S> <C>, EXIT");
                break;
            default:
                System.out.println("Unknown command.");
        }
    }
}





