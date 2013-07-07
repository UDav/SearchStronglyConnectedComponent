package com.udav.scc;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;

public class MainFrame extends JFrame {
    private int lx, ly;
    private JScrollPane sp, sp2;
    private RButtonPanel topPanel;
    private PaintPanel middlePanel;
    private ResultPaintPanel middlePanel2;
    private JTextPane middlePanel3;
    private ButtonPanel bottomPanel;
    private JTabbedPane tabbedPane;
    private JMenuItem MNew, MLoad, MSave, MImport, MExport, MExportImg;
    private int arr[][];
    private JFileChooser fileLoad, fileSave, fileExport, fileImport, fileExportImg;
    private String path;
    private ConfigDialog conf;
    SSCC comp;



    public JFileChooser setUpdateUI(JFileChooser choose) {
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.lookInLabelText", "Смотреть в");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файла");

        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");

        UIManager.put("FileChooser.lookInLabelText", "Папка");
        UIManager.put("FileChooser.saveInLabelText", "Папка");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов");

        UIManager.put("FileChooser.upFolderToolTipText", "На один уровень вверх");
        UIManager.put("FileChooser.newFolderToolTipText", "Создание новой папки");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
        UIManager.put("FileChooser.fileNameHeaderText", "Имя");
        UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
        UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
        UIManager.put("FileChooser.fileDateHeaderText", "Изменен");
        UIManager.put("FileChooser.fileAttrHeaderText", "Атрибуты");

        UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
        choose.updateUI();
    return choose;
}
    public boolean newGraph() {
        if (middlePanel.save == false) {
            Object[] buttons = {"Да", "Нет", "Отмена"};
            int ans = JOptionPane.showOptionDialog(null, "Были внесены изменения! Сохраниить?", "Диалог сохранения", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
            switch(ans){
                case 0:
                    if (!saveGraph()) break;
                case 1:
                    middlePanel.save = true;
                default:
                    middlePanel.newGraph();
                    middlePanel2.newCond();
                    tabbedPane.setSelectedIndex(0);
                    tabbedPane.setEnabledAt(1, false);
                    tabbedPane.setEnabledAt(2, false);
                    middlePanel.setPreferredSize(new Dimension(0, 0));
                    sp.revalidate();
                    break;
                case 2: return false;
                case -1:    return false;
            }
        } else {middlePanel.newGraph(); middlePanel2.newCond();}
        return true;

    }
    public boolean saveGraph() {
        if (fileSave == null) {
            fileSave = new JFileChooser();
            fileSave.setCurrentDirectory(new File("."));
            fileSave = setUpdateUI(fileSave);
            fileSave.setFileFilter(new filterGrf());
        }
        if (fileSave.showDialog(MainFrame.this, "Сохранить") == JFileChooser.APPROVE_OPTION) {
            path = fileSave.getSelectedFile().getPath();
            boolean temp = true;
            for (int i = 0; i < path.length(); i++) {
                if (path.charAt(i) == '.') {
                    temp = false;
                }
            }
            if (temp == true) {
                path += ".grf";
            }
            if (tabbedPane.getSelectedIndex() == 0) {
                middlePanel.saveGraph(path);
            } else {
                middlePanel2.saveGraph(path);
            }
        return true;
        } else return false;

    }
    public MainFrame(String str, int X, int Y) {
        super(str);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        /*Image im = kit.getImage("ICO/ico.ico");
        this.setIconImage(im);*/
        lx = screenSize.width;
        ly = screenSize.height;
        setMinimumSize(new Dimension((int) Math.round(lx / 1.5), (int) Math.round(ly / 1.6)));
        setBounds(X * lx / 100, Y * ly / 100, (int) Math.round(lx / 1.1), (int) Math.round(ly / 1.2));
        Container c = getContentPane();

        /* Create menu */
        JMenu fileMenu = new JMenu("Файл");
        MNew = fileMenu.add(new AbstractAction("Новый", new ImageIcon("ICO/new.png")) {
            public void actionPerformed(ActionEvent e) {
                newGraph();
            }
        });
        MNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        MLoad = fileMenu.add(new AbstractAction("Загрузить") {
            public void actionPerformed(ActionEvent e) {
                
                if (fileLoad == null) {
                        fileLoad = new JFileChooser();
                        fileLoad.setCurrentDirectory(new File("."));
                        fileLoad = setUpdateUI(fileLoad);
                        fileLoad.setFileFilter(new filterGrf());
                    }

                    if (fileLoad.showDialog(MainFrame.this, "Загрузить") == JFileChooser.APPROVE_OPTION) {
                        if (newGraph()) {
                            path = fileLoad.getSelectedFile().getPath();
                            boolean temp = true;
                            for (int i = 0; i < path.length(); i++) {
                                if (path.charAt(i) == '.') {
                                    temp = false;
                                }
                            }
                            if (temp == true) {
                                path += ".grf";
                            }
                            middlePanel.loadGraph(path);
                        }
                    }
            }
        });
        MLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
        MSave = fileMenu.add(new AbstractAction("Сохранить", new ImageIcon("ICO/save.png")) {

            public void actionPerformed(ActionEvent e) {
                saveGraph();
            }
        });
        MSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        fileMenu.addSeparator();
        MImport = fileMenu.add(new AbstractAction("Импортировать", new ImageIcon("ICO/import.png")) {

            public void actionPerformed(ActionEvent e) {

                if (fileImport == null) {
                    fileImport = new JFileChooser();
                    fileImport.setCurrentDirectory(new File("."));
                    fileImport = setUpdateUI(fileImport);
                    fileImport.setFileFilter(new filterTxt());
                }
                if (fileImport.showDialog(MainFrame.this, "Импортировать") == JFileChooser.APPROVE_OPTION) {
                    if (newGraph()) {
                        path = fileImport.getSelectedFile().getPath();
                        boolean temp = true;
                        for (int i = 0; i < path.length(); i++) {
                            if (path.charAt(i) == '.') {
                                temp = false;
                            }
                        }
                        if (temp == true) {
                            path += ".txt";
                        }
                        middlePanel.importGraph(path);
                    }
                }
            }
            
        });
        MImport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        MExport = fileMenu.add(new AbstractAction("Экспортировать", new ImageIcon("ICO/export.png")) {

            public void actionPerformed(ActionEvent e) {
                if (fileExport == null) {
                    fileExport = new JFileChooser();
                    fileExport.setCurrentDirectory(new File("."));
                    fileExport = setUpdateUI(fileExport);
                    fileExport.setFileFilter(new filterTxt());
                }
                if (fileExport.showDialog(MainFrame.this, "Экспортировать") == JFileChooser.APPROVE_OPTION) {
                    path = fileExport.getSelectedFile().getPath();
                    boolean temp = true;
                    for (int i = 0; i < path.length(); i++) {
                        if (path.charAt(i) == '.') {
                            temp = false;
                        }
                    }
                    if (temp == true) {
                        path += ".txt";
                    }
                    middlePanel.exportGraph(path);
                }
            }
        });
        MExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        MExportImg = fileMenu.add(new AbstractAction("Экспортировать изображение", new ImageIcon("ICO/export.png")) {
            public void actionPerformed(ActionEvent e) {
                if (fileExportImg == null) {
                    fileExportImg = new JFileChooser();
                    fileExportImg.setCurrentDirectory(new File("."));
                    fileExportImg = setUpdateUI(fileExportImg);
                    fileExportImg.setFileFilter(new filterImg());
                }
                if (fileExportImg.showDialog(MainFrame.this, "Экспортировать") == JFileChooser.APPROVE_OPTION) {
                    path = fileExportImg.getSelectedFile().getPath();
                    boolean temp = true;
                    for (int i = 0; i < path.length(); i++) {
                        if (path.charAt(i) == '.') {
                            temp = false;
                        }
                    }
                    if (temp == true) {
                        path += ".jpeg";
                    }
                    if (tabbedPane.getSelectedIndex() == 0) {
                        middlePanel.exportImg(path);
                    }
                    if (tabbedPane.getSelectedIndex() == 1) {
                        middlePanel2.exportImg(path);
                    }
                }
            }
        });
        //Item5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        fileMenu.addSeparator();
        JMenuItem MExit = fileMenu.add(new AbstractAction("Выход", new ImageIcon("ICO/exit.png")) {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        MExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));

        JMenu SettingsMenu = new JMenu("Настройки");
        JMenuItem MSettings = SettingsMenu.add(new AbstractAction("Конфигурация") {

            public void actionPerformed(ActionEvent e) {
              if (conf == null){

                conf = new ConfigDialog(getWidth(),getHeight(),getLocationOnScreen().getX(),getLocationOnScreen().getY());
              }
              else conf.setVisible(true);
             
            }
        });

        JMenu helpMenu = new JMenu("Помощь");
        JMenuItem Item8 = helpMenu.add(new AbstractAction("Справка") {

            public void actionPerformed(ActionEvent e) {
                try {
                    Runtime.getRuntime().exec("C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE "+System.getProperty("user.dir")+"/help/index.html");
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JMenuItem Item9 = helpMenu.add(new AbstractAction("О программе") {

            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this, new JLabel("<html>Программа предназначена для поиска компонент сильной связности<br> и построения \"конденсации\" ориентированного графа</html>"), "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JMenuBar menuBar = new JMenuBar();

        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(SettingsMenu);
        menuBar.add(helpMenu);
        /*Menu cteated*/

        tabbedPane = new JTabbedPane();

        middlePanel = new PaintPanel();
        sp = new JScrollPane(middlePanel);
        tabbedPane.addTab("Исходный граф", sp);

        middlePanel2 = new ResultPaintPanel();
        sp2 = new JScrollPane(middlePanel2);
        tabbedPane.addTab("\"Конденсация\" графа", sp2);

        middlePanel3 = new JTextPane();
        JScrollPane sp3 = new JScrollPane(middlePanel3);
        tabbedPane.addTab("Информация", sp3);
        middlePanel3.setEditable(false);


        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);

        topPanel = new RButtonPanel();
        bottomPanel = new ButtonPanel();

        c.add(topPanel, BorderLayout.NORTH);
        c.add(tabbedPane, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);

        RButtonAction handler = new RButtonAction();
        ButtonAction action = new ButtonAction();
        TabbedAction tabAction = new TabbedAction();
        topPanel.first.addItemListener(handler);
        topPanel.second.addItemListener(handler);
        bottomPanel.button1.addActionListener(action);
        bottomPanel.button2.addActionListener(action);
        bottomPanel.button3.addActionListener(action);
        tabbedPane.addChangeListener(tabAction);
        addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentResized(java.awt.event.ComponentEvent evt) {
                middlePanel.setSizePanel();
                middlePanel2.setSizePanel();
            }
        });
        setVisible(true);
    }

    class TabbedAction implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            middlePanel2.setSizePanel();
            sp2.revalidate();
            if (tabbedPane.getSelectedIndex() == 0) {
                MLoad.setEnabled(true);
                MImport.setEnabled(true);
            } else {
                MLoad.setEnabled(false);
                MImport.setEnabled(false);
            }
            if (tabbedPane.getSelectedIndex() == 0 || tabbedPane.getSelectedIndex() == 1) {
                MSave.setEnabled(true);
                MExport.setEnabled(true);
                MExportImg.setEnabled(true);
            } else {
                MSave.setEnabled(false);
                MExport.setEnabled(false);
                MExportImg.setEnabled(false);
            }
        }
    }

    class RButtonAction implements ItemListener {

        public void itemStateChanged(ItemEvent event) {
            if (event.getSource() == topPanel.first) {
                middlePanel.firstChecked = true;
            } else if (event.getSource() == topPanel.second) {
                middlePanel.firstChecked = false;
            }
        }
    }

    class ButtonAction implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == bottomPanel.button1) {
                setEnabled(false);
                middlePanel2.newCond();
                arr = middlePanel.createArray();
                comp = new SSCC();
                //comp.setArray(arr);
                middlePanel3.setText(comp.searchComponent(arr));
                middlePanel.setColorVertex(comp.component);

                middlePanel2.setCond(comp.condArr, middlePanel.setComponentCoord(comp.component));
                tabbedPane.setEnabledAt(1, true);
                tabbedPane.setEnabledAt(2, true);
                setEnabled(true);
            }
            if (event.getSource() == bottomPanel.button2) {
                newGraph();
            }
            if (event.getSource() == bottomPanel.button3) {
                if (newGraph() == true) System.exit(0);
            }

        }
    }
}

class filterGrf extends FileFilter {
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".grf") || f.isDirectory();
    }
    @Override
    public String getDescription() {
        return "grf (*.grf)";
    }
}

class filterTxt extends FileFilter {
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
    }
    @Override
    public String getDescription() {
        return "Текстовые документы (*.txt)";
    }
}

class filterImg extends FileFilter {
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".jpeg") || f.isDirectory();
    }
    @Override
    public String getDescription() {
        return "Изображения (*.jpeg)";
    }
}

class ButtonPanel extends JPanel {

    public JButton button1;
    public JButton button2;
    public JButton button3;

    public ButtonPanel() {
        button1 = new JButton("Выполнить");
        button1.setPreferredSize(new Dimension(100, 25));
        button2 = new JButton("Новый");
        button2.setPreferredSize(new Dimension(100, 25));
        button3 = new JButton("Выход");
        button3.setPreferredSize(new Dimension(100, 25));
        add(button1);
        add(button2);
        add(button3);
        //setBorder(BorderFactory.createLineBorder(Color.black));
    }
}

class RButtonPanel extends JPanel {

    public JRadioButton first;
    public JRadioButton second;

    public RButtonPanel() {
        ButtonGroup group = new ButtonGroup();
        first = new JRadioButton("Вершины", true);
        second = new JRadioButton("Дуги", false);
        JLabel label = new JLabel("Рисовать:");
        group.add(first);
        group.add(second);
        add(label);
        add(first);
        add(second);
        //setBorder(BorderFactory.createLineBorder(Color.black));

    }
}
