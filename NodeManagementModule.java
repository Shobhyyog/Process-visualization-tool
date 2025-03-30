package processvisualization;

import java.awt.*;
import java.awt.geom.*;

/**
 * Module 2: Node Management
 * Handles creation and management of different node types
 */
public class NodeManagementModule {
    /**
     * Base class for all process node types
     */
    public class ProcessNode {
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
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);
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
    public class DiamondNode extends ProcessNode {
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
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fill(diamond);
            g2d.setColor(Color.BLACK);
            g2d.draw(diamond);
            drawCenteredString(g2d, label, x + width / 2, y + height / 2);
        }
    }
    
    /**
     * Oval shape for start/end nodes
     */
    public class OvalNode extends ProcessNode {
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
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fill(oval);
            g2d.setColor(Color.BLACK);
            g2d.draw(oval);
            drawCenteredString(g2d, label, x + width / 2, y + height / 2);
        }
    }
    
    /**
     * Parallelogram shape for input/output nodes
     */
    public class ParallelogramNode extends ProcessNode {
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
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fill(parallelogram);
            g2d.setColor(Color.BLACK);
            g2d.draw(parallelogram);
            drawCenteredString(g2d, label, x + width / 2, y + height / 2);
        }
    }
}
