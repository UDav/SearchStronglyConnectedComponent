
package com.udav.scc;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import javax.swing.JFrame;

public class Main {
    public static MainFrame window;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("./conf"));
            //Config.maxEdge = (int) in.read();
            //Config.maxVertex = (int) in.read();
           // Config.sizeVertex = (int) in.read();
            Vertex.size = (int) in.read();
            Vertex.halfSize = Math.round(Vertex.size/2);
            in.close();
        } catch (Exception e) {}
        window = new MainFrame("Поиск компонент сильной связности", 5, 5);
        //window.setDefaultCloseOperation(MainFrame.EXIT_ON_CLOSE);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent event) {
            }
            public void windowClosed(WindowEvent event) {
            }
            public void windowClosing(WindowEvent event) {
                if (window.newGraph() == true) System.exit(0);
            }
            public void windowDeactivated(WindowEvent event) {
            }
            public void windowDeiconified(WindowEvent event) {
            }
            public void windowIconified(WindowEvent event) {
            }
            public void windowOpened(WindowEvent event) {
            }
        });


    }
}
