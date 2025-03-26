import java.util.*;
import javax.swing.*;
import java.awt.*;

public class ProcessSchedulerGantt {
    static class Process {
        int id, arrivalTime, burstTime, completionTime, waitingTime, turnAroundTime, remainingTime;

        public Process(int id, int arrivalTime, int burstTime) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
        }
    }

    static class GanttChart extends JPanel {
        private final List<int[]> timeline;

        public GanttChart(List<int[]> timeline) {
            this.timeline = timeline;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = 50, y = 50;
            int width = 50;
            g.setFont(new Font("Arial", Font.BOLD, 14));

            for (int[] entry : timeline) {
                int pid = entry[0], start = entry[1], end = entry[2];
                g.setColor(new Color(100 + (pid * 30) % 156, 50 + (pid * 50) % 156, 200 - (pid * 25) % 156));
                g.fillRect(x, y, width, 40);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, 40);
                g.drawString("P" + pid, x + 15, y + 25);
                g.drawString(String.valueOf(start), x, y + 60);
                x += width;
            }
            g.drawString(String.valueOf(timeline.get(timeline.size() - 1)[2]), x, y + 60);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter arrival time of process " + (i + 1) + ": ");
            int arrivalTime = sc.nextInt();
            System.out.print("Enter burst time of process " + (i + 1) + ": ");
            int burstTime = sc.nextInt();
            processes[i] = new Process(i + 1, arrivalTime, burstTime);
        }

        System.out.print("Enter time quantum for Round Robin: ");
        int timeQuantum = sc.nextInt();

        System.out.println("\nScheduling Algorithms Results:");

        displayGanttChart("FCFS Scheduling", scheduleFCFS(processes.clone()));
        displayGanttChart("SJF Non-Preemptive Scheduling", scheduleSJFNonPreemptive(processes.clone()));
        displayGanttChart("SJF Preemptive Scheduling", scheduleSJFPreemptive(processes.clone()));
        displayGanttChart("Round Robin Scheduling", scheduleRoundRobin(processes.clone(), timeQuantum));
    }

    private static void displayGanttChart(String title, List<int[]> timeline) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 300);
        frame.add(new GanttChart(timeline));
        frame.setVisible(true);
    }

    private static List<int[]> scheduleFCFS(Process[] processes) {
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));
        List<int[]> timeline = new ArrayList<>();
        int time = 0;
        for (Process p : processes) {
            if (time < p.arrivalTime) time = p.arrivalTime;
            timeline.add(new int[]{p.id, time, time + p.burstTime});
            time += p.burstTime;
        }
        return timeline;
    }

    private static List<int[]> scheduleSJFNonPreemptive(Process[] processes) {
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));
        List<int[]> timeline = new ArrayList<>();
        List<Process> processList = new ArrayList<>(Arrays.asList(processes));
        int time = 0;
        while (!processList.isEmpty()) {
            Process shortest = processList.stream().filter(p -> p.arrivalTime <= time)
                .min(Comparator.comparingInt(p -> p.burstTime)).orElse(null);
            if (shortest == null) {
                time++;
                continue;
            }
            processList.remove(shortest);
            timeline.add(new int[]{shortest.id, time, time + shortest.burstTime});
            time += shortest.burstTime;
        }
        return timeline;
    }

    private static List<int[]> scheduleSJFPreemptive(Process[] processes) {
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));
        List<int[]> timeline = new ArrayList<>();
        int time = 0, completed = 0;
        while (completed < processes.length) {
            Process shortest = Arrays.stream(processes).filter(p -> p.arrivalTime <= time && p.remainingTime > 0)
                .min(Comparator.comparingInt(p -> p.remainingTime)).orElse(null);
            if (shortest == null) {
                time++;
                continue;
            }
            timeline.add(new int[]{shortest.id, time, time + 1});
            shortest.remainingTime--;
            time++;
            if (shortest.remainingTime == 0) completed++;
        }
        return timeline;
    }

    private static List<int[]> scheduleRoundRobin(Process[] processes, int quantum) {
        Queue<Process> queue = new LinkedList<>(Arrays.asList(processes));
        List<int[]> timeline = new ArrayList<>();
        int time = 0;
        while (!queue.isEmpty()) {
            Process p = queue.poll();
            if (p.arrivalTime > time) time = p.arrivalTime;
            int executedTime = Math.min(p.remainingTime, quantum);
            timeline.add(new int[]{p.id, time, time + executedTime});
            p.remainingTime -= executedTime;
            time += executedTime;
            if (p.remainingTime > 0) queue.add(p);
        }
        return timeline;
    }
}
