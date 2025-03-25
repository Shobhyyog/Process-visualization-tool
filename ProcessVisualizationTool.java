import java.util.Scanner;

public class JavaFlowVisualizer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Paste your Java method code here:");
        StringBuilder javaCode = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) break;
            javaCode.append(line).append("\n");
        }
        scanner.close();
        
        String flowDiagram = generateFlowDiagram(javaCode.toString());
        System.out.println("\nFlow Diagram:");
        System.out.println(flowDiagram);
    }
    
    private static String generateFlowDiagram(String javaCode) {
        String[] lines = javaCode.split("\n");
        StringBuilder mermaidCode = new StringBuilder("stateDiagram-v2\n");
        int stateCounter = 0;

        for (String line : lines) {
            String cleanLine = line.trim();
            if (cleanLine.startsWith("if ")) {
                mermaidCode.append("  state \"Condition: ").append(cleanLine).append("\" as state").append(stateCounter).append("\n");
                mermaidCode.append("  [*] --> state").append(stateCounter).append("\n");
                mermaidCode.append("  state").append(stateCounter).append(" --> state").append(stateCounter + 1).append(": True\n");
                mermaidCode.append("  state").append(stateCounter).append(" --> state").append(stateCounter + 2).append(": False\n");
                stateCounter += 3;
            } else if (cleanLine.startsWith("for ") || cleanLine.startsWith("while ")) {
                mermaidCode.append("  state \"Loop: ").append(cleanLine).append("\" as state").append(stateCounter).append("\n");
                mermaidCode.append("  [*] --> state").append(stateCounter).append("\n");
                mermaidCode.append("  state").append(stateCounter).append(" --> state").append(stateCounter + 1).append(": Iterate\n");
                mermaidCode.append("  state").append(stateCounter).append(" --> [*]: Exit Loop\n");
                stateCounter += 2;
            } else if (cleanLine.startsWith("return ")) {
                mermaidCode.append("  state \"Return: ").append(cleanLine).append("\" as state").append(stateCounter).append("\n");
                mermaidCode.append("  state").append(stateCounter).append(" --> [*]\n");
                stateCounter++;
            }
        }
        return mermaidCode.toString();
    }
}
