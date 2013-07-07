

package com.udav.scc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;


/**
 *
 * @author Администратор
 */
public class ConfigDialog extends JDialog{
    private JButton BSave;
    private JButton BCancel;
    public JSlider vertexSize;
    public ConfigDialog(int width, int height,double a, double b) {
        setModal(true);
        setTitle("Конфигурация");
        setBounds((int)a + Math.round(width/3), (int)b + Math.round(height/3), 400, 150);
        BSave = new JButton("Сохранить");
        BSave.setPreferredSize(new Dimension(100, 25));
        BCancel = new JButton("Отмена");
        BCancel.setPreferredSize(new Dimension(100, 25));
        JPanel mainPanel = new JPanel(new GridLayout(2,1));
        JLabel l1 = new JLabel("  Размер вершин:");


        vertexSize = new JSlider();
        vertexSize.setValue(Vertex.size);
        vertexSize.setMajorTickSpacing(10);
        vertexSize.setMinorTickSpacing(1);
        vertexSize.setPaintTicks(true);
        vertexSize.setPaintLabels(true);
        vertexSize.setMaximum(50);
        vertexSize.setMinimum(20);
        

        mainPanel.add(l1);
        mainPanel.add(vertexSize);
        l1.setAlignmentX(JLabel.LEFT_ALIGNMENT);


        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(BSave);
        buttonsPanel.add(BCancel);

        
        add(buttonsPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.NORTH);
                
        ButtonAction action = new ButtonAction();
        BSave.addActionListener(action);
        BCancel.addActionListener(action);
        setResizable(false);
        setVisible(true);

    }

     class ButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == BSave) {
                Vertex.size = vertexSize.getValue();
                try {
                    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./conf"));
                    out.write(Vertex.size);
                    out.flush();
                    out.close();
                } catch (Exception e) {}
                Vertex.halfSize = Math.round(Vertex.size/2);
                setVisible(false);
                
            }
            if (event.getSource() == BCancel) {
                //setVisible(false);
                dispose();
            }
        }
    }
}


