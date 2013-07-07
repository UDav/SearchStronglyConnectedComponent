
package com.udav.scc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ResultPaintPanel extends JPanel {

    private int cond[][];
    private Vertex V2[];
    private Edge E2[];

    String showComp;
    private int movePoint;
    private SCC component[];

    public ResultPaintPanel() {
        V2 = new Vertex[100];
        E2 = new Edge[200];
        addMouseListener(new Mouse());
        addMouseMotionListener(new MouseMove());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < Edge.count2; i++) {
            g.drawLine(V2[E2[i].firstPoint].x, V2[E2[i].firstPoint].y,
                    V2[E2[i].secondPoint].x, V2[E2[i].secondPoint].y);
            double alfa, beta;
            double x1 = (double) V2[E2[i].firstPoint].x;
            double y1 = (double) V2[E2[i].firstPoint].y;
            double x2 = (double) V2[E2[i].secondPoint].x;
            double y2 = (double) V2[E2[i].secondPoint].y;

            int[] xPoints = new int[3];
            int[] yPoints = new int[3];

            double AB = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

            double AC = AB - Vertex.halfSize;

            xPoints[0] = (int) Math.round((x1 + (AC / Vertex.halfSize) * x2) / (1 + AC / Vertex.halfSize));
            yPoints[0] = (int) Math.round((y1 + (AC / Vertex.halfSize) * y2) / (1 + AC / Vertex.halfSize));


            beta = Math.atan2(y1 - y2, x2 - x1); /*ArcTan2 ищет арктангенс от x/y что бы неопределенностей не
            возникало типа деления на ноль*/
            alfa = Math.PI / 18; /*угол между основной осью стрелки и рисочки в конце*/
            double r1 = 30; /*длинна риски*/
            if (AB < 50) {
                r1 = AB / 2;
            }

            xPoints[1] = (int) Math.round(xPoints[0] - r1 * Math.cos(beta + alfa));
            yPoints[1] = (int) Math.round(yPoints[0] + r1 * Math.sin(beta + alfa));
            xPoints[2] = (int) Math.round(xPoints[0] - r1 * Math.cos(beta - alfa));
            yPoints[2] = (int) Math.round(yPoints[0] + r1 * Math.sin(beta - alfa));
            g.fillPolygon(xPoints, yPoints, 3);
        }


        for (int i = 0; i < Vertex.count2; i++) {
            Random r = new Random(i);
            g.setColor(new Color(r.nextInt(510)/2, r.nextInt(510)/2, r.nextInt(510)/2));
            g.fillOval(V2[i].x - Vertex.halfSize, V2[i].y - Vertex.halfSize, Vertex.size, Vertex.size);
            g.setColor(Color.WHITE);
            g.drawString("" + i, V2[i].x - Vertex.halfSize/2, V2[i].y + Vertex.halfSize/2);
        }
        if (showComp != null) {
            g.setColor(Color.BLACK);
            g.drawString(showComp, 10, 15);
        }
    }

    public void setCond(int arr[][], SCC component[]) {
        cond = arr;
        this.component = component;
        createVertex();

    }

    private void createVertex() {
        for (int i = 0; i < SCC.count; i++) {
            V2[i] = new Vertex(component[i].x, component[i].y);
        }
        Vertex.count2 = SCC.count;
        createEdge();
    }
    private void createEdge() {

        for (int i = 0; i < cond.length; i++) {
            for (int j = 0; j < cond.length; j++) {
                if ((cond[i][j] == 1) && (i != j)) {
                    E2[Edge.count2] = new Edge();
                    E2[Edge.count2].firstPoint = i;
                    E2[Edge.count2].secondPoint = j;
                    Edge.count2++;
                }
            }
        }
        repaint();
    }
    public void newCond() {
        cond = new int[0][0];
        Vertex.count2 = 0;
        Edge.count2 = 0;
        V2 = new Vertex[100];
        E2 = new Edge[200];
        repaint();

    }
    public void saveGraph(String path) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(V2);
            out.write(Vertex.count2);
            out.writeObject(E2);
            out.write(Edge.count2);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }
    public void exportImg(String path) {
        BufferedImage image = (BufferedImage) this.createImage(this.getWidth(), this.getHeight());
        Graphics g = image.createGraphics();
        this.paint(g);
        g.dispose();
        try {
            ImageIO.write(image, "jpeg", new File(path));
        } catch (IOException io) {
        }

    }
    public void setSizePanel() {
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < Vertex.count2; i++) {
            if (V2[i].x > maxX) {
                maxX = V2[i].x;
            }
            if (V2[i].y > maxY) {
                maxY = V2[i].y;
            }
        }
        setPreferredSize(new Dimension(maxX + Vertex.halfSize, maxY + Vertex.halfSize));
    }
    private void showInfo(int X, int Y) {
        for (int i=0; i<cond.length; i++) {
            if ((X >= V2[i].x - Vertex.halfSize) && (X <= V2[i].x+Vertex.size - Vertex.halfSize)
            && (Y >= V2[i].y - Vertex.halfSize) && (Y <= V2[i].y+Vertex.size - Vertex.halfSize)) {
                switch(component[i].result.size()){
                    case 1: showComp  = "В состав вершины "+i+" входит "; break;
                    default: showComp = "В состав вершины "+i+" входят ";;
                }
                for (int j=0; j<component[i].result.size(); j++)
                    showComp = showComp + " " + component[i].result.get(j)+"ая";
                switch(component[i].result.size()){
                    case 1: showComp += " вершина исходного графа"; break;
                    default: showComp += " вершины исходного графа";
                }
            } /*else showComp = "";*/
        }
        repaint();
    }

    private class Mouse extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            movePoint = -1;
            for (int i = 0; i < cond.length; i++) {
                if ((event.getX() >= V2[i].x - Vertex.halfSize) && (event.getX() <= V2[i].x + Vertex.halfSize)
                        && (event.getY() >= V2[i].y - Vertex.halfSize) && (event.getY() <= V2[i].y +  Vertex.halfSize)) {
                    movePoint = i;
                }
            }

        }
    }

    private class MouseMove implements MouseMotionListener {

        public void mouseDragged(MouseEvent e) {

            if ((movePoint != -1)) {
                V2[movePoint].x = e.getX();
                V2[movePoint].y = e.getY();
            }
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            /*showInfo(e.getX(), e.getY());*/
        }
    }
}
