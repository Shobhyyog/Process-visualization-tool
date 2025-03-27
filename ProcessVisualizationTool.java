import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * A simple process visualization tool built in Java
 * Allows users to create, connect, and manage process nodes in a visual workflow
 */
public class ProcessVisualizationTool extends JFrame {
    private JPanel toolPanel;
    private DrawingPanel drawingPanel;
    private JButton addNodeButton;
    private JButton connectNodesButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JComboBox<String> nodeTypeComboBox;

    public ProcessVisualizationTool() {
        setTitle("Process Visualization Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize components
        initComponents();
        
        // Make the frame visible
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
    
    public static void main(String[] args) {
        // Set the look and feel to the system's look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the application
        SwingUtilities.invokeLater(() -> {
            new ProcessVisualizationTool();
        });
    }
    
    /**
     * Panel for drawing and interacting with process nodes
     */
    private class DrawingPanel extends JPanel {
        private List<ProcessNode> nodes;
        private List<Connection> connections;
        private ProcessNode selectedNode;
        private ProcessNode sourceNode;
        private ProcessNode destinationNode;
        private Point dragStartPoint;
        private String nodeToAdd;
        private boolean connectingMode;
        private boolean deleteMode;
        
        public DrawingPanel() {
            nodes = new ArrayList<>();
            connections = new ArrayList<>();
            selectedNode = null;
            connectingMode = false;
            deleteMode = false;
            
            // Mouse listeners for node interactions
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMousePressed(e);
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    handleMouseReleased(e);
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    handleMouseDragged(e);
                }
                
                @Override
                public void mouseMoved(MouseEvent e) {
                    repaint();
                }
            };
            
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }
        
        private void handleMousePressed(MouseEvent e) {
            if (nodeToAdd != null) {
                // Add a new node
                ProcessNode node = createNode(nodeToAdd, e.getX(), e.getY());
                nodes.add(node);
                nodeToAdd = null;
                repaint();
                return;
            }
            
            ProcessNode clickedNode = getNodeAt(e.getX(), e.getY());
            
            if (deleteMode) {
                if (clickedNode != null) {
                    // Delete node and its connections
                    nodes.remove(clickedNode);
                    connections.removeIf(connection -> 
                        connection.getSource() == clickedNode || 
                        connection.getDestination() == clickedNode);
                    deleteMode = false;
                    repaint();
                }
                return;
            }
            
            if (connectingMode) {
                if (clickedNode != null) {
                    if (sourceNode == null) {
                        sourceNode = clickedNode;
                    } else {
                        destinationNode = clickedNode;
                        if (sourceNode != destinationNode) {
                            connections.add(new Connection(sourceNode, destinationNode));
                            sourceNode = null;
                            destinationNode = null;
                            connectingMode = false;
                        }
                    }
                    repaint();
                }
                return;
            }
            
            selectedNode = clickedNode;
            if (selectedNode != null) {
                dragStartPoint = e.getPoint();
            }
        }
        
        private void handleMouseReleased(MouseEvent e) {
            if (selectedNode != null) {
                selectedNode = null;
                dragStartPoint = null;
                repaint();
            }
        }
        
        private void handleMouseDragged(MouseEvent e) {
            if (selectedNode != null && dragStartPoint != null) {
                int dx = e.getX() - dragStartPoint.x;
                int dy = e.getY() - dragStartPoint.y;
                selectedNode.move(dx, dy);
                dragStartPoint = e.getPoint();
                repaint();
            }
        }
        
        private ProcessNode getNodeAt(int x, int y) {
            // Check from last (top) to first (bottom) to handle overlapping nodes
            for (int i = nodes.size() - 1; i >= 0; i--) {
                ProcessNode node = nodes.get(i);
                if (node.contains(x, y)) {
                    return node;
                }
            }
            return null;
        }
        
        public void prepareToAddNode(String nodeType) {
            nodeToAdd = nodeType;
            connectingMode = false;
            deleteMode = false;
            sourceNode = null;
            destinationNode = null;
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        
        public void setConnectingMode(boolean connecting) {
            connectingMode = connecting;
            deleteMode = false;
            nodeToAdd = null;
            sourceNode = null;
            destinationNode = null;
            if (connecting) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
        
        public void prepareToDelete() {
            deleteMode = true;
            connectingMode = false;
            nodeToAdd = null;
            sourceNode = null;
            destinationNode = null;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        
        public void clearAll() {
            nodes.clear();
            connections.clear();
            selectedNode = null;
            sourceNode = null;
            destinationNode = null;
            connectingMode = false;
            deleteMode = false;
            nodeToAdd = null;
            setCursor(Cursor.getDefaultCursor());
            repaint();
        }
        
        private ProcessNode createNode(String type, int x, int y) {
            switch (type) {
                case "Process":
                    return new ProcessNode(x, y, 120, 60, type, new Color(173, 216, 230));
                case "Decision":
                    return new DiamondNode(x, y, 100, 100, type, new Color(255, 255, 153));
                case "Start":
                case "End":
                    return new OvalNode(x, y, 80, 40, type, type.equals("Start") ? 
                          new Color(144, 238, 144) : new Color(255, 204, 204));
                case "Input/Output":
                    return new ParallelogramNode(x, y, 120, 60, type, new Color(204, 204, 255));
                default:
                    return new ProcessNode(x, y, 120, 60, "Generic", Color.lightGray);
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw connections
            for (Connection connection : connections) {
                connection.draw(g2d);
            }
            
            // Draw temporary connection line while in connecting mode
            if (connectingMode && sourceNode != null) {
                Point start = sourceNode.getConnectionPoint(getMousePosition());
                Point end = getMousePosition();
                if (end != null) {
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawLine(start.x, start.y, end.x, end.y);
                    
                    // Draw arrowhead
                    drawArrowHead(g2d, start, end);
                }
            }
            
            // Draw nodes
            for (ProcessNode node : nodes) {
                node.draw(g2d);
            }
            
            // Draw status text
            g2d.setColor(Color.DARK_GRAY);
            String statusText = "";
            if (nodeToAdd != null) {
                statusText = "Click to place a " + nodeToAdd + " node";
            } else if (connectingMode) {
                if (sourceNode == null) {
                    statusText = "Select source node";
                } else {
                    statusText = "Select destination node";
                }
            } else if (deleteMode) {
                statusText = "Click on a node to delete it";
            }
            g2d.drawString(statusText, 10, getHeight() - 10);
        }
        
        private void drawArrowHead(Graphics2D g2d, Point from, Point to) {
            double dx = to.x - from.x;
            double dy = to.y - from.y;
            double angle = Math.atan2(dy, dx);
            int len = 12;
            
            g2d.setPaint(Color.BLACK);
            
            // Create the arrow head
            Path2D.Double path = new Path2D.Double();
            path.moveTo(to.x, to.y);
            path.lineTo(to.x - len * Math.cos(angle - Math.PI/6), 
                       to.y - len * Math.sin(angle - Math.PI/6));
            path.lineTo(to.x - len * Math.cos(angle + Math.PI/6), 
                       to.y - len * Math.sin(angle + Math.PI/6));
            path.closePath();
            
            g2d.fill(path);
        }
    }
    
    /**
     * Represents a connection between two process nodes
     */
    private class Connection {
        private ProcessNode source;
        private ProcessNode destination;
        
        public Connection(ProcessNode source, ProcessNode destination) {
            this.source = source;
            this.destination = destination;
        }
        
        public ProcessNode getSource() {
            return source;
        }
        
        public ProcessNode getDestination() {
            return destination;
        }
        
        public void draw(Graphics2D g2d) {
            Point start = source.getConnectionPoint(destination.getCenter());
            Point end = destination.getConnectionPoint(source.getCenter());
            
            // Draw the connection line
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(start.x, start.y, end.x, end.y);
            
            // Draw the arrow head
            double dx = end.x - start.x;
            double dy = end.y - start.y;
            double angle = Math.atan2(dy, dx);
            int len = 12;
            
            // Create the arrow head
            Path2D.Double path = new Path2D.Double();
            path.moveTo(end.x, end.y);
            path.lineTo(end.x - len * Math.cos(angle - Math.PI/6), 
                       end.y - len * Math.sin(angle - Math.PI/6));
            path.lineTo(end.x - len * Math.cos(angle + Math.PI/6), 
                       end.y - len * Math.sin(angle + Math.PI/6));
            path.closePath();
            
            g2d.fill(path);
        }
    }
    
    /**
     * Base class for all process node types
     */
    private class ProcessNode {
        protected int x, y;
        protected int width, height;
        protected String label;
        protected Color color;
        protected Rectangle bounds;
        
        public ProcessNode(int x, int y, int width, int height, String label, Color color) {
            this.x = x - width / 2;
            this.y = y - height / 2;
            this.width = width;
            this.height = height;
            this.label = label;
            this.color = color;
            this.bounds = new Rectangle(this.x, this.y, width, height);
        }
        
        public boolean contains(int px, int py) {
            return bounds.contains(px, py);
        }
        
        public void move(int dx, int dy) {
            x += dx;
            y += dy;
            bounds.setLocation(x, y);
        }
        
        public Point getCenter() {
            return new Point(x + width / 2, y + height / 2);
        }
        
        public Point getConnectionPoint(Point target) {
            if (target == null) return getCenter();
            
            int cx = x + width / 2;
            int cy = y + height / 2;
            
            double angle = Math.atan2(target.y - cy, target.x - cx);
            
            // Find intersection with the rectangle
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            
            double t;
            if (Math.abs(dx) * height > Math.abs(dy) * width) {
                // Intersect with vertical edge
                t = (dx > 0 ? (x + width - cx) : (x - cx)) / dx;
            } else {
                // Intersect with horizontal edge
                t = (dy > 0 ? (y + height - cy) : (y - cy)) / dy;
            }
            
            return new Point(
                (int) (cx + dx * t),
                (int) (cy + dy * t)
            );
        }
        
        public void draw(Graphics2D g2d) {
            // Draw shape
            g2d.setColor(color);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);
            
            // Draw label
            drawCenteredString(g2d, label, x + width / 2, y + height / 2);
        }
        
        protected void drawCenteredString(Graphics2D g2d, String text, int centerX, int centerY) {
            FontMetrics metrics = g2d.getFontMetrics();
            int x = centerX - metrics.stringWidth(text) / 2;
            int y = centerY - metrics.getHeight() / 2 + metrics.getAscent();
            g2d.setColor(Color.BLACK);
            g2d.drawString(text, x, y);
        }
    }
    
    /**
     * Diamond shape for decision nodes
     */
    private class DiamondNode extends ProcessNode {
        private Polygon diamond;
        
        public DiamondNode(int x, int y, int width, int height, String label, Color color) {
            super(x, y, width, height, label, color);
            createDiamond();
        }
        
        private void createDiamond() {
            diamond = new Polygon();
            diamond.addPoint(x + width / 2, y);
            diamond.addPoint(x + width, y + height / 2);
            diamond.addPoint(x + width / 2, y + height);
            diamond.addPoint(x, y + height / 2);
        }
        
        @Override
        public boolean contains(int px, int py) {
            return diamond.contains(px, py);
        }
        
        @Override
        public void move(int dx, int dy) {
            super.move(dx, dy);
            createDiamond();
        }
        
        @Override
        public Point getConnectionPoint(Point target) {
            if (target == null) return getCenter();
            
            int cx = x + width / 2;
            int cy = y + height / 2;
            
            double angle = Math.atan2(target.y - cy, target.x - cx);
            
            // Calculate the point where the line intersects the diamond
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            
            // Handle pure horizontal or vertical angles for numerical stability
            if (Math.abs(dx) < 0.01) { // Almost vertical
                return new Point(cx, dy > 0 ? y + height : y);
            }
            if (Math.abs(dy) < 0.01) { // Almost horizontal
                return new Point(dx > 0 ? x + width : x, cy);
            }
            
            // General case - find intersection with diamond lines
            double slope = dy / dx;
            double halfW = width / 2.0;
            double halfH = height / 2.0;
            
            // Calculate intersection points with each edge of the diamond
            double t;
            if (Math.abs(slope) < halfH / halfW) { // Intersect with left or right edge
                t = halfW / Math.abs(dx);
            } else { // Intersect with top or bottom edge
                t = halfH / Math.abs(dy);
            }
            
            return new Point(
                (int) (cx + dx * t),
                (int) (cy + dy * t)
            );
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            // Draw diamond
            g2d.setColor(color);
            g2d.fill(diamond);
            g2d.setColor(Color.BLACK);
            g2d.draw(diamond);
            
            // Draw label
            drawCenteredString(g2d, label, x + width / 2, y + height / 2);
        }
    }
    
    /**
     * Oval shape for start/end nodes
     */
    private class OvalNode extends ProcessNode {
        private Ellipse2D.Double oval;
        
        public OvalNode(int x, int y, int width, int height, String label, Color color) {
            super(x, y, width, height, label, color);
            oval = new Ellipse2D.Double(x, y, width, height);
        }
        
        @Override
        public boolean contains(int px, int py) {
            return oval.contains(px, py);
        }
        
        @Override
        public void move(int dx, int dy) {
            super.move(dx, dy);
            oval = new Ellipse2D.Double(x, y, width, height);
        }
        
        @Override
        public Point getConnectionPoint(Point target) {
            if (target == null) return getCenter();
            
            int cx = x + width / 2;
            int cy = y + height / 2;
            
            double angle = Math.atan2(target.y - cy, target.x - cx);
            
            // Calculate the point where the line intersects the ellipse
            double a = width / 2.0;
            double b = height / 2.0;
            
            double t = Math.atan2(a * Math.sin(angle), b * Math.cos(angle));
            double px = cx + a * Math.cos(t) * Math.signum(Math.cos(angle));
            double py = cy + b * Math.sin(t) * Math.signum(Math.sin(angle));
            
            return new Point((int) px, (int) py);
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            // Draw oval
            g2d.setColor(color);
            g2d.fill(oval);
            g2d.setColor(Color.BLACK);
            g2d.draw(oval);
            
            // Draw label
            drawCenteredString(g2d, label, x + width / 2, y + height / 2);
        }
    }
    
    /**
     * Parallelogram shape for input/output nodes
     */
    private class ParallelogramNode extends ProcessNode {
        private Polygon parallelogram;
        private int offset = 20;
        
        public ParallelogramNode(int x, int y, int width, int height, String label, Color color) {
            super(x, y, width, height, label, color);
            createParallelogram();
        }
        
        private void createParallelogram() {
            parallelogram = new Polygon();
            parallelogram.addPoint(x + offset, y);
            parallelogram.addPoint(x + width, y);
            parallelogram.addPoint(x + width - offset, y + height);
            parallelogram.addPoint(x, y + height);
        }
        
        @Override
        public boolean contains(int px, int py) {
            return parallelogram.contains(px, py);
        }
        
        @Override
        public void move(int dx, int dy) {
            super.move(dx, dy);
            createParallelogram();
        }
        
        @Override
        public Point getConnectionPoint(Point target) {
            if (target == null) return getCenter();
            
            int cx = x + width / 2;
            int cy = y + height / 2;
            
            double angle = Math.atan2(target.y - cy, target.x - cx);
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            
            // Find intersection with the parallelogram edges
            double t;
            Point intersection = null;
            
            // Top edge
            if (dy < 0) {
                double tx = (y - cy) / dy;
                double ix = cx + tx * dx;
                if (ix >= x + offset && ix <= x + width) {
                    intersection = new Point((int) ix, y);
                }
            }
            
            // Bottom edge
            if (dy > 0 && intersection == null) {
                double tx = (y + height - cy) / dy;
                double ix = cx + tx * dx;
                if (ix >= x && ix <= x + width - offset) {
                    intersection = new Point((int) ix, y + height);
                }
            }
            
            // Left edge
            if (dx < 0 && intersection == null) {
                double ty = ((x - cx) * height + offset * (y - cy)) / (dx * height - dy * offset);
                double iy = cy + ty * dy;
                if (iy >= y && iy <= y + height) {
                    intersection = new Point(x + (int) ((iy - y) * offset / height), (int) iy);
                }
            }
            
            // Right edge
            if (dx > 0 && intersection == null) {
                double ty = ((x + width - cx) * height - offset * (y - cy)) / (dx * height + dy * offset);
                double iy = cy + ty * dy;
                if (iy >= y && iy <= y + height) {
                    intersection = new Point(x + width - (int) ((iy - y) * offset / height), (int) iy);
                }
            }
            
            return intersection != null ? intersection : getCenter();
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            // Draw parallelogram
            g2d.setColor(color);
            g2d.fill(parallelogram);
            g2d.setColor(Color.BLACK);
            g2d.draw(parallelogram);
            
            // Draw label
            drawCenteredString(g2d, label, x + width / 2, y + height / 2);
        }
    }
}
