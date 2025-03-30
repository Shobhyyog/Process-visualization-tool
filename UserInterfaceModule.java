package processvisualization;

import javax.swing.*;
import java.awt.*;

/**
 * Module 1: User Interface (GUI)
 * Handles all graphical user interface components and interactions
 */
public class UserInterfaceModule extends JFrame {
    private JPanel toolPanel;
    private DrawingPanel drawingPanel;
    private JButton addNodeButton;
    private JButton connectNodesButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JComboBox<String> nodeTypeComboBox;

    public UserInterfaceModule() {
        setTitle("Process Visualization Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Set up the main layout
        setLayout(new BorderLayout());
        
        // Create the toolbar
        toolPanel = new JPanel();
        toolPanel.setBackground(new Color(240, 240, 240));
        
        // Create node types dropdown
        String[] nodeTypes = {"Process", "Decision", "Start", "End", "Input/Output"};
        nodeTypeComboBox = new JComboBox<>(nodeTypes);
        
        // Create buttons
        addNodeButton = new JButton("Add Node");
        connectNodesButton = new JButton("Connect Nodes");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear All");
        
        // Add components to the toolbar
        toolPanel.add(new JLabel("Node Type:"));
        toolPanel.add(nodeTypeComboBox);
        toolPanel.add(addNodeButton);
        toolPanel.add(connectNodesButton);
        toolPanel.add(deleteButton);
        toolPanel.add(clearButton);
        
        // Create drawing panel
        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);
        
        // Add components to the frame
        add(toolPanel, BorderLayout.NORTH);
        add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        
        // Set up event listeners
        setupEventListeners();
    }
    
    private void setupEventListeners() {
        addNodeButton.addActionListener(e -> {
            String nodeType = (String) nodeTypeComboBox.getSelectedItem();
            drawingPanel.prepareToAddNode(nodeType);
        });
        
        connectNodesButton.addActionListener(e -> {
            drawingPanel.setConnectingMode(true);
        });
        
        deleteButton.addActionListener(e -> {
            drawingPanel.prepareToDelete();
        });
        
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all nodes and connections?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                drawingPanel.clearAll();
            }
        });
    }
}
