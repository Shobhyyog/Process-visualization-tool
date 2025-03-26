import java.util.*;

class Process {
    int id;         // Process ID
    int arrival;    // Arrival Time
    int burst;      // Burst Time (CPU Execution Time)
    int remaining;  // Remaining Burst Time (for preemptive algorithms)
    int waiting;    // Waiting Time
    int turnaround; // Turnaround Time
    int completion; // Completion Time
    List<int[]> executionTimeline; // Tracks execution intervals
    
    public Process(int id, int arrival, int burst) {
        this.id = id;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining = burst;
        this.waiting = 0;
        this.turnaround = 0;
        this.completion = 0;
        this.executionTimeline = new ArrayList<>();
    }
    
    // Copy constructor for creating duplicate process lists
    public Process(Process p) {
        this.id = p.id;
        this.arrival = p.arrival;
        this.burst = p.burst;
        this.remaining = p.burst;
        this.waiting = 0;
        this.turnaround = 0;
        this.completion = 0;
        this.executionTimeline = new ArrayList<>();
    }
}

public class ProcessScheduler {
    // ... (previous methods remain the same)

    // New method to generate and print Gantt Chart
    public static void printGanttChart(String algorithmName, List<Process> processes) {
        System.out.println("\n" + algorithmName + " Gantt Chart:");
        
        // Collect all execution intervals across all processes
        List<int[]> allIntervals = new ArrayList<>();
        for (Process p : processes) {
            allIntervals.addAll(p.executionTimeline);
        }
        
        // Sort intervals by start time
        allIntervals.sort(Comparator.comparingInt(a -> a[0]));
        
        // Print top border
        System.out.print("+");
        for (int[] interval : allIntervals) {
            for (int i = 0; i < interval[1] - interval[0]; i++) {
                System.out.print("-");
            }
            System.print("+");
        }
        System.out.println();
        
        // Print process IDs
        System.out.print("|");
        for (int[] interval : allIntervals) {
            String processLabel = "P" + interval[2];
            int padLeft = (interval[1] - interval[0] - processLabel.length()) / 2;
            int padRight = interval[1] - interval[0] - processLabel.length() - padLeft;
            
            for (int i = 0; i < padLeft; i++) System.print(" ");
            System.print(processLabel);
            for (int i = 0; i < padRight; i++) System.print(" ");
            System.print("|");
        }
        System.out.println();
        
        // Print bottom border with timestamps
        System.out.print("+");
        int prevEnd = 0;
        for (int[] interval : allIntervals) {
            for (int i = 0; i < interval[1] - interval[0]; i++) {
                System.print("-");
            }
            System.print("+");
            prevEnd = interval[1];
        }
        System.out.println();
        
        // Print time stamps
        System.out.print("0");
        prevEnd = 0;
        for (int[] interval : allIntervals) {
            // Print spaces before timestamp
            for (int i = 0; i < interval[1] - interval[0] - String.valueOf(interval[1]).length(); i++) {
                System.print(" ");
            }
            System.print(interval[1]);
            prevEnd = interval[1];
        }
        System.out.println();
    }

    // Modify existing scheduling algorithms to track execution timeline
    public static List<Process> fcfs(List<Process> originalProcesses) {
        List<Process> processes = copyProcesses(originalProcesses);
        processes.sort(Comparator.comparingInt(p -> p.arrival));
        
        int currentTime = 0;
        
        for (Process p : processes) {
            if (currentTime < p.arrival) {
                currentTime = p.arrival;
            }
            
            // Track execution timeline
            p.executionTimeline.add(new int[]{currentTime, currentTime + p.burst, p.id});
            
            p.waiting = currentTime - p.arrival;
            currentTime += p.burst;
            p.completion = currentTime;
            p.turnaround = p.completion - p.arrival;
        }
        
        return processes;
    }

    // Similar modifications for other scheduling algorithms (sjfNonPreemptive, sjfPreemptive, roundRobin)
    // Add tracking of execution timeline in each algorithm's implementation

    public static void main(String[] args) {
        // ... (previous main method code)
        
        // After displaying results, print Gantt charts
        printGanttChart("First-Come, First-Served (FCFS)", fcfsResult);
        printGanttChart("Shortest Job First (SJF) Non-Preemptive", sjfNonPreemptiveResult);
        printGanttChart("Shortest Job First (SJF) Preemptive (SRTF)", sjfPreemptiveResult);
        printGanttChart("Round Robin (RR)", rrResult);
    }
}
