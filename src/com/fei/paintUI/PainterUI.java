package com.fei.paintUI;

import com.fei.paintUI.shape.FillRect;
import com.fei.paintUI.shape.Shape;
import com.fei.paintUI.util.ClientUtils;
import com.fei.paintUI.util.IOUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import javax.swing.*;

public class PainterUI extends JPanel {
    private static final long serialVersionUID = 1L;
    private com.fei.paintUI.shape.Shape[] shapeParameter = new com.fei.paintUI.shape.Shape[20000];


    public void initUI(String authority, Socket socket) {
        // 新建窗体并命名
        JFrame jf = new JFrame("Shared White Board");
        // 设置窗体大小
        jf.setSize(1100, 700);
        // 窗体设置居中
        jf.setLocationRelativeTo(null);
        // 设置窗体关闭
        jf.setDefaultCloseOperation(3);
        // 设置窗体边界布局
        jf.setLayout(new BorderLayout());

        // 添加3个JPanel容器
        JPanel jp2 = new JPanel();
        JPanel jp3 = new JPanel();


        // 将JPanel布局到窗体中
        jf.add(this, BorderLayout.CENTER);
        jf.add(jp2, BorderLayout.WEST);
        jf.add(jp3, BorderLayout.EAST);

        // 设置jp1
        this.setPreferredSize(new Dimension(900, 700));
        this.setBackground(Color.white);

        // 创建事件监听器对象
        DrawListener dl = new DrawListener();
        // 给画布添加监听器
        this.addMouseListener(dl);
        this.addMouseMotionListener(dl);

        // 设置jp2
        jp2.setPreferredSize(new Dimension(100, 700));
        jp2.setBackground(Color.LIGHT_GRAY);
        // 设置jp3
        jp3.setPreferredSize(new Dimension(50, 700));
        jp3.setBackground(Color.LIGHT_GRAY);

        // 添加图形按钮
        String[] shapeArray = { "Line", "Brush","Rectangle","FillRect", "Oval","FillOval","Circle", "Text", "Rubber" ,"Clear"};

        Icon[] icons = new Icon[10];
//        URL fileURL=this.getClass().getResource("/Users/louisliu/Desktop/semester2/DS/ass2/DDDDDSSSSSS_AAAA2-master/resources");
//        System.out.println(fileURL.getFile());
//        icons[0] = new ImageIcon(fileURL.getPath());
        icons[0] = new ImageIcon("./resources/icon_line.png");
        icons[1] = new ImageIcon("./resources/icon_pen.png");
        icons[2] = new ImageIcon("./resources/icon_rect.png");
        icons[3] = new ImageIcon("./resources/icon_fillrect.png");
        icons[4] = new ImageIcon("./resources/icon_oval.png");
        icons[5] = new ImageIcon("./resources/icon_filloval.png");
        icons[6] = new ImageIcon("./resources/icon_circle.png");
        icons[7] = new ImageIcon("./resources/icon_text.png");
        icons[8] = new ImageIcon("./resources/icon_eraser.png");
        icons[9] = new ImageIcon("./resources/icon_clear.png");


        for (int i = 0; i < shapeArray.length; i++) {
            // 创建图形按钮
            JButton jbu1 = new JButton(shapeArray[i]);
            // 设置按钮大小
            jbu1.setPreferredSize(new Dimension(100, 40));
            jbu1.setIcon(icons[i]);
            // 将按钮添加到jp2容器中
            jp2.add(jbu1);
            // 给按钮注册监听器
            jbu1.addActionListener(dl);
        }

        // 设置颜色按钮
        Color[] colorArray = { Color.red, Color.pink, Color.orange, Color.yellow, new Color(176,247,13), Color.green, new Color(18,187,161),
                Color.cyan, Color.blue, new Color(84,13,247) ,new Color(255,0,182), new Color(138,0,148) ,
                Color.black, new Color(58,58,58), Color.gray, new Color(200,200,200), Color.white };
        for (int i = 0; i < colorArray.length; i++) {
            JButton jbu2 = new JButton();
            jbu2.setBackground(colorArray[i]);
            jbu2.setPreferredSize(new Dimension(50, 30));
            jp3.add(jbu2);
            jbu2.addActionListener(dl);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        jf.setJMenuBar(menuBar);
        JMenu menu1 = new JMenu("File");
        menuBar.add(menu1);
        JMenuItem item1 = new JMenuItem("Open");
        JMenuItem item2 = new JMenuItem("Save as");
        JMenuItem item4 = new JMenuItem("Exit");
        JMenuItem item5 = new JMenuItem("New");
        JMenuItem item6 = new JMenuItem("Save");
        menu1.add(item5);
        menu1.add(item1);
        menu1.add(item6);
        menu1.add(item2);
        menu1.addSeparator();
        menu1.add(item4);

        //如果是普通用户，不能修改文件
        if(authority.equals("isClient")){
            menu1.setEnabled(false);
        }

        //New
        item5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                int i=JOptionPane.showConfirmDialog(null, "Do you want to new a white board?",
                        "New confirmation dialog",JOptionPane.YES_NO_CANCEL_OPTION);
                //通过对话框中按钮的选择来决定结果
                if(i==0) {
                    try {
                        File file = new File("./new.txt");
                        file.createNewFile(); // 创建新文件
                        BufferedWriter out = new BufferedWriter(new FileWriter(file));
                        out.write("[{\"name\":\"Clear\",\"x1\":0,\"y1\":0,\"x2\":900,\"y2\":700,\"name\":\"Clear\",\"red\":255,\"green\":255,\"blue\":255,\"text\":null}]");
                        out.flush();
                        out.close();
                        Shape[] shapes = IOUtil.readFile(file);
                        dl.rebuild(shapes);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });

        //打开
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(item1)==JFileChooser.APPROVE_OPTION) {

                    File file = chooser.getSelectedFile();
                    Shape[] shapes = IOUtil.readFile(file);
                    dl.rebuild(shapes);
                }

            }
        });

        //保存
        item6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                JFileChooser chooser = new JFileChooser();

                if (chooser.showSaveDialog(item6)==JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    IOUtil.writeFile(file.getPath(),dl);
                }
            }
        });

        //另存为
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                JFileChooser chooser = new JFileChooser();

                if (chooser.showSaveDialog(item2)==JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    IOUtil.writeFile(file.getPath(),dl);
                }
            }
        });

        //菜单栏监听器
        //退出
        item4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e) {
                // TODO Auto-generated method stub
                int i=JOptionPane.showConfirmDialog(null, "Do you really want to quit the system?",
                        "Exit confirmation dialog",JOptionPane.YES_NO_CANCEL_OPTION);
                //通过对话框中按钮的选择来决定结果，单机yes时，窗口直接消失
                if(i==0) {
//                    jf.dispose();
                    String sendStrEXIT = "{\"name\":\"EXIT\",\"x1\":0,\"y1\":0,\"x2\":0,\"y2\":0,\"name\":\"EXIT\",\"red\":255,\"green\":255,\"blue\":255,\"text\":\"" + "EXIT" + "\"}";

                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.println(sendStrEXIT);
                        out.flush();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }

            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////


        // 设置窗体可见
        jf.setVisible(true);
        // 获取画笔
        Graphics g = this.getGraphics();
        // 将画笔传递过去
        dl.setGr(g);
        // 将图形数组传递过去
        dl.setSp(shapeParameter);

        Thread thread = new Thread(new PaintingRunner(dl)) ;
        thread.start();

    }



    // 重写父类方法
    public void paint(Graphics g) {
        super.paint(g);     //遍历图形数组，重绘图形
        for (int i = 0; i < shapeParameter.length; i++) {
            Shape shape = shapeParameter[i];
            if (shapeParameter[i] != null) {
                shape.drawShape(g);
            }
        }
    }


    //这个专门用来接受别人的画画的
    class PaintingRunner implements Runnable  {
        private DrawListener dl;

        public PaintingRunner (DrawListener dl)  {
            this.dl = dl ;
        }

        public void run()  {
            while(true){
                if(!ClientUtils.isEmpty()){
                    //取出第一个元素
                    dl.rebuild(ClientUtils.getShape());

                }else{  //如果没有内容就睡一会
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
