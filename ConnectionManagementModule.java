package processvisualization;

import java.awt.*;
import java.awt.geom.*;

/**
 * Module 3: Connection Management
 * Handles creation and management of connections between nodes
 */
public class ConnectionManagementModule {
    /**
     * Represents a connection between two process nodes
     */
    public class Connection {
        private NodeManagementModule.ProcessNode source;
        private NodeManagementModule.ProcessNode destination;
        
        public Connection(NodeManagementModule.ProcessNode source, 
                        NodeManagementModule.ProcessNode destination) {
            this.source = source;
            this.destination = destination;
        }
        
        public NodeManagementModule.ProcessNode getSource() {
            return source;
        }
        
        public NodeManagementModule.ProcessNode getDestination() {
            return destination;
        }
        
        public void draw(Graphics2D g2d) {
            Point start = source.getCenter();
            Point end = destination.getCenter();
            
            // Draw the connection line
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(start.x, start.y, end.x, end.y);
            
            // Draw the arrow head
            drawArrowHead(g2d, start, end);
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
     * Validates if a connection can be made between two nodes
     */
    public boolean validateConnection(NodeManagementModule.ProcessNode source, 
                                   NodeManagementModule.ProcessNode destination) {
        // Prevent self-connections
        if (source == destination) {
            return false;
        }
        
        // Add any additional validation rules here
        return true;
    }
    
    /**
     * Creates a new connection between two nodes if valid
     */
    public Connection createConnection(NodeManagementModule.ProcessNode source, 
                                    NodeManagementModule.ProcessNode destination) {
        if (validateConnection(source, destination)) {
            return new Connection(source, destination);
        }
        return null;
    }
}
