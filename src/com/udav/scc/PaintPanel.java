
package com.udav.scc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

class PaintPanel extends JPanel {

    private Vertex[] V;
    private Edge[] E;
    static public int countComponent;
    public boolean firstChecked = true;
    private Point2D cursor;
    private int currentPoint;
    public boolean save = true;
    private int movePoint;
    private int mouseButtonPress;

    public PaintPanel() {
        V = new Vertex[100];
        E = new Edge[200];
        Vertex.count = 0;
        Edge.count = 0;
        cursor = new Point2D.Double();
        addMouseListener(new Mouse());
        addMouseMotionListener(new MouseMove());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Edge.count != 0) {
            for (int i = 0; i < Edge.count; i++) { //рисуем рёбра
                g.drawLine(V[E[i].firstPoint].x, V[E[i].firstPoint].y, V[E[i].secondPoint].x, V[E[i].secondPoint].y);

                double alfa, beta;
                double x1 = (double) V[E[i].firstPoint].x;
                double y1 = (double) V[E[i].firstPoint].y;
                double x2 = (double) V[E[i].secondPoint].x;
                double y2 = (double) V[E[i].secondPoint].y;

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
        }

        for (int i = 0; i < Vertex.count; i++) { //рисуем вершины
            if (V[i] != null) {
                if (V[i].idComponent == -1) {
                    g.setColor(Color.BLUE/*new Color( (i*100+20)%255, (i*80+30)%255, (i*50+50)%255)*/);
                } else {
                    Random r = new Random(V[i].idComponent);
                    g.setColor(new Color(r.nextInt(510)/2, r.nextInt(510)/2, r.nextInt(510)/2));
                }

                g.fillOval(V[i].x - Vertex.halfSize, V[i].y - Vertex.halfSize, Vertex.size, Vertex.size);
                g.setColor(Color.white);
                //g.setFont(new Font("1", 1, Vertex.halfSize));
                g.drawString(Integer.toString(i), V[i].x - Vertex.halfSize/2, V[i].y + Vertex.halfSize/2); //рисуем номер вершины
            }

        }
        g.setColor(Color.BLACK);
        if (firstChecked == true) {
         //   g.drawOval((int) cursor.getX() - Vertex.halfSize, (int) cursor.getY() - Vertex.halfSize, Vertex.size, Vertex.size);
        } else if ((Edge.firstClick == false) && currentPoint != -1) {
            g.drawLine(V[currentPoint].x, V[currentPoint].y, (int) cursor.getX(), (int) cursor.getY());
        }


        if (countComponent != 0) {
            g.setColor(Color.BLACK);
            g.drawString("Компонентов сильной связноти " + countComponent, 10, 15);
        }
    }

    private void createVertex(int X, int Y) {
        boolean overlay = false;
        for (int i = 0; i < Vertex.count; i++) {
            if ((X >= V[i].x - Vertex.halfSize) && (X <= V[i].x + Vertex.size - Vertex.halfSize)
                    && (Y >= V[i].y - Vertex.halfSize) && (Y <= V[i].y + Vertex.size - Vertex.halfSize)) {
                overlay = true;
            }
        }

        if (!overlay) {
            if (Vertex.count < 100) {
                V[Vertex.count] = new Vertex(X, Y);
                Vertex.count++;
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, new JLabel("Максимальное число вершин 100"), "Warning!", JOptionPane.WARNING_MESSAGE);
            }
        }

    }

    private void deleteVertex(int X, int Y) {
        int del = -1;
        for (int i = 0; i < Vertex.count; i++) {
            if (V[i] != null) {
                if ((X >= V[i].x - Vertex.halfSize)
                        && (X <= V[i].x + Vertex.size - Vertex.halfSize)
                        && (Y >= V[i].y - Vertex.halfSize)
                        && (Y <= V[i].y + Vertex.size - Vertex.halfSize)) {
                    del = i;
                }
            }
        }
        int temp = Edge.count;
        for (int i = 0; i < temp; i++) {
            if ((E[i].firstPoint == del) || E[i].secondPoint == del) {
                E[i] = null;
                Edge.count--;
            }
        }
        for (int i = 0; i < temp; i++) {
            if (E[i] == null) {
                int j = i;
                while ((E[j] == null) && j <= temp) {
                    j++;
                }
                E[i] = E[j];
                E[j] = null;
            }
        }

        if (del != -1) {
            V[del] = V[Vertex.count - 1];
            for (int i = 0; i < Edge.count; i++) {
                if (E[i].firstPoint == Vertex.count - 1) {
                    E[i].firstPoint = del;
                }
                if (E[i].secondPoint == Vertex.count - 1) {
                    E[i].secondPoint = del;
                }
            }

            V[Vertex.count - 1] = null;
            Vertex.count--;
        }

        repaint();

    }

    private void createEdge(int X, int Y, int button) {
        if ((Edge.firstClick == false) && (button == 3)) {
            E[Edge.count] = null;
            currentPoint = -1;
            Edge.firstClick = true;
        } else {
            for (int i = 0; i < Vertex.count; i++) {

                if ((X >= V[i].x - Vertex.halfSize) && (X <= V[i].x + Vertex.size - Vertex.halfSize)
                        && (Y >= V[i].y - Vertex.halfSize) && (Y <= V[i].y + Vertex.size - Vertex.halfSize)) {

                    if (Edge.firstClick == true) {
                        E[Edge.count] = new Edge();
                        E[Edge.count].firstPoint = i;
                        currentPoint = i;
                        Edge.firstClick = false;
                    } else {
                        E[Edge.count].secondPoint = i;
                        Edge.count++;
                        Edge.firstClick = true;
                    }
                }

            }
        }
        /* for (int i = 0; i<Edge.count; i++) {
        System.out.println(i+"ая линия:") ;
        System.out.println(E[i].firstPoint) ;
        System.out.println(E[i].secondPoint) ;
        }*/
        repaint();
    }

    private void deleteEdge(int X, int Y) {
        double AB, AC, CB;
        for (int i = 0; i < Edge.count; i++) {
            AB = Math.sqrt((V[E[i].firstPoint].x - V[E[i].secondPoint].x)
                    * (V[E[i].firstPoint].x - V[E[i].secondPoint].x)
                    + (V[E[i].firstPoint].y - V[E[i].secondPoint].y)
                    * (V[E[i].firstPoint].y - V[E[i].secondPoint].y));
            AC = Math.sqrt((V[E[i].firstPoint].x - X) * (V[E[i].firstPoint].x - X)
                    + (V[E[i].firstPoint].y - Y) * (V[E[i].firstPoint].y - Y));
            CB = Math.sqrt((V[E[i].secondPoint].x - X) * (V[E[i].secondPoint].x - X)
                    + (V[E[i].secondPoint].y - Y) * (V[E[i].secondPoint].y - Y));
            //  System.out.println("AB="+AB+" "+"AC+CB="+(AC+CB)) ;
            if ((((AB >= AC + CB - 1) && (AB <= AC + CB + 1)))
                    || ((AB <= AC + CB + 1) && (AB >= AC + CB - 1))) {
                E[i] = E[Edge.count - 1];
                E[Edge.count - 1] = null;
                Edge.count--;
            }
        }
        repaint();
    }

    public void newGraph() {
        movePoint = -1;
        Vertex.count = 0;
        Edge.count = 0;
        V = new Vertex[100];
        E = new Edge[200];
        countComponent = 0;
        repaint();

    }

    public void saveGraph(String path) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(V);
            out.write(Vertex.count);
            out.writeObject(E);
            out.write(Edge.count);
            out.flush();
            out.close();
            save = true;
        } catch (Exception e) {
        }
    }

    public void loadGraph(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            V = (Vertex[]) in.readObject();
            Vertex.count = (int) in.read();
            E = (Edge[]) in.readObject();
            Edge.count = (int) in.read();
            in.close();
            repaint();
        } catch (Exception e) {
        }
    }

    public void importGraph(String path) {
        BufferedReader in;
        int ar[][] = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            int countV = 0;
            while (in.ready()) {
                if (in.read() == '\n') {
                    countV++;
                }
            }
            in.close();

            in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String s = in.readLine();
            in.close();

            if (countV == s.length()) {
                ar = new int[s.length()][s.length()];
                in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                int j = 0;
                while (in.ready()) {
                    String s2 = in.readLine();

                    for (int i = 0; i < s2.length(); i++) {
                        ar[j][i] = s2.charAt(i) - 48;
                    }
                    j++;
                }
                in.close();
            }
        } catch (IOException e) {
        }

        if (ar != null) {
            PaintPanel.this.getHeight();
            double DAlfa = 270 / (double) ar.length;
            double Alfa = 0;
            int R = Math.round(PaintPanel.this.getHeight() / 2 - 10);
            for (int i = 0; i < ar.length; i++) {
                V[i] = new Vertex((int) Math.round(R * Math.cos(Alfa) + Math.round(PaintPanel.this.getWidth() / 2)),
                        (int) Math.round(R * Math.sin(Alfa) + Math.round(PaintPanel.this.getHeight() / 2)));
                Alfa += DAlfa;
            }
            Vertex.count = ar.length;

            for (int i = 0; i < ar.length; i++) {
                for (int j = 0; j < ar.length; j++) {
                    if ((ar[i][j] == 1) && (i != j)) {
                        E[Edge.count] = new Edge();
                        E[Edge.count].firstPoint = i;
                        E[Edge.count].secondPoint = j;
                        Edge.count++;
                    }
                }
            }

        } else {
            JOptionPane.showMessageDialog(this, new JLabel("Неправильный формат файла!"), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        repaint();
    }

    public void exportGraph(String path) {
        try {
            int arr[][];
            arr = this.createArray();
            if (arr != null) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path)), true);
                for (int i = 0; i < arr.length; i++) {
                    for (int j = 0; j < arr.length; j++) {
                        out.print(arr[i][j]);
                    }
                    out.println();
                }
            }
        } catch (IOException ex) {
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

    public void setColorVertex(SCC component[]) {
        for (int i = 0; i < Vertex.count; i++) {
            for (int j = 0; j < SCC.count; j++) {
                for (int k = 0; k < component[j].result.size(); k++) {
                    if (i == (Integer) component[j].result.get(k)) {
                        V[i].idComponent = j;
                    }
                }
            }
        }
        countComponent = SCC.count;
        repaint();
    }

    public void setSizePanel() {
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < Vertex.count; i++) {
            if (V[i].x > maxX) {
                maxX = V[i].x;
            }
            if (V[i].y > maxY) {
                maxY = V[i].y;
            }
        }
        setPreferredSize(new Dimension(maxX + Vertex.halfSize, maxY + Vertex.halfSize));
    }

    public SCC[] setComponentCoord(SCC component[]) {
        for (int i = 0; i < SCC.count; i++) {
            for (int j = 0; j < component[i].result.size(); j++) {
                component[i].x += V[(Integer) component[i].result.get(j)].x;
                component[i].y += V[(Integer) component[i].result.get(j)].y;
            }
            component[i].x = Math.round(component[i].x / component[i].result.size());
            component[i].y = Math.round(component[i].y / component[i].result.size());
        }

        return component;
    }

    public int[][] createArray() {
        int arr[][];
        arr = new int[Vertex.count][Vertex.count];
        for (int k = 0; k < Edge.count; k++) {
            for (int i = 0; i < Vertex.count; i++) {
                for (int j = 0; j < Vertex.count; j++) {
                    if ((E[k].firstPoint == i) && (E[k].secondPoint == j)) {
                        arr[i][j] = 1;
                    }
                }
            }
        }
        return arr;
    }

    private class Mouse extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {

            mouseButtonPress = event.getButton();
            save = false;
            movePoint = -1;
            for (int i = 0; i < Vertex.count; i++) {
                if ((event.getX() >= V[i].x - Vertex.halfSize) && (event.getX() <= V[i].x + Vertex.halfSize)
                        && (event.getY() >= V[i].y - Vertex.halfSize) && (event.getY() <= V[i].y + Vertex.halfSize)) {
                    movePoint = i;
                }
            }
            if (firstChecked == true) {
                switch (event.getButton()) {
                    case 1:
                        createVertex(event.getX(), event.getY());
                        break;
                    case 3:
                        deleteVertex(event.getX(), event.getY());
                        break;
                }
            } else {
                switch (event.getButton()) {
                    case 1:
                        createEdge(event.getX(), event.getY(), event.getButton());
                        break;
                    case 3:
                        createEdge(event.getX(), event.getY(), event.getButton());
                        deleteEdge(event.getX(), event.getY());
                        break;
                }

            }

        }
    }

    private class MouseMove implements MouseMotionListener {

        public void mouseDragged(MouseEvent e) {
            cursor = e.getPoint();
            if ((movePoint != -1) && (firstChecked == true) && mouseButtonPress == 1) {
                V[movePoint].x = e.getX();
                V[movePoint].y = e.getY();
            }
            repaint();

        }

        public void mouseMoved(MouseEvent e) {
            cursor = e.getPoint();
            repaint();
        }
    }
}
