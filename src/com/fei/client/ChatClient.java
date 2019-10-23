package com.fei.client;

import com.fei.paintUI.PainterUI;
import com.fei.paintUI.util.ClientUtils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;


public class ChatClient extends JFrame {
    private Socket socket ;                  //负责和服务器通信
    private JTextArea sendArea ;        //消息编辑区域
    private JTextArea contentArea ;   //群聊消息显示框
    private String name ;                   //当前用户名称
    private JTextArea userListArea ;   //群聊用户名显示框
//    private ArrayList<String> namelist = new ArrayList<>();    //用户名列表
    private String authority;    //是否为管理员
//    private Boolean ismanager = false;


    public ChatClient(Socket socket, String name, String authority)  {
        this.authority = authority;
        this.socket = socket ;
        this.name  = name ;
        this.init();       //初始化聊天客户端
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);

        /*接下来启动单独线程，专门从服务器中读取数据
         *
         */
        ClientThread clientThread  = new ClientThread(socket,contentArea,userListArea,name) ;
        clientThread.start();

        //启动一个绘画线程
        PaintThread paintThread = new PaintThread(authority,socket);
        paintThread.start();

        //再启动一个线程用来不停地检查msgList(看看ui是伐画了新的东西)
        MsgThread msgThread = new MsgThread(socket);
        msgThread.start();
    }

//    public Boolean isManager(){
//        Boolean ismanager = false;
//        return ismanager;
//    }


    public void init( )  {
        this.setTitle("Chatting Room");
        this.setSize(500,400);
        int x = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() ;
        int y = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() ;

        this.setLocation( (x-this.getWidth() )*79/80, ( y-this.getHeight() )/2 );
        this.setResizable(false);      //不允许用户改变大小

        contentArea = new JTextArea() ;
        contentArea.setLineWrap(true);  //换行方法
        contentArea.setEditable(false);
        JScrollPane logPanel  = new JScrollPane(contentArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;

        sendArea = new JTextArea() ;
        sendArea.setLineWrap(true);    //控制每行显示长度最大不超过界面长度
        JScrollPane sendPanel  = new JScrollPane(sendArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;

        //创建一个分隔窗格
        JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,logPanel,sendPanel) ;
        splitpane.setDividerLocation(250);
        this.add(splitpane,BorderLayout.CENTER) ;

        ////////////////////////////////////////////////////
        userListArea = new JTextArea() ;
        userListArea.append("User List: \n");
        userListArea.setLineWrap(true);  //换行方法
        userListArea.setEditable(false);
        JScrollPane userListPanel  = new JScrollPane(userListArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
        this.add(userListPanel, BorderLayout.EAST);

        ////////////////////////////////////////////////////
        //按钮面板
        JPanel bPanel  = new JPanel() ;
        bPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)) ;
        this.add(bPanel,BorderLayout.SOUTH) ;

        JLabel namelabel = new JLabel("Username: "+this.name+"  ") ;
        bPanel.add(namelabel) ;

        //用户名列表
//        String chatContent = name+" "+time+" said: "+str ;

        String sendStrName="{\"name\":\"Username\",\"x1\":0,\"y1\":0,\"x2\":0,\"y2\":0,\"name\":\"Username\",\"red\":255,\"green\":255,\"blue\":255,\"text\":\""+ name +"\"}";
//        System.out.println(sendStrName);

        userListArea.append(name);
        userListArea.append("\n");

        PrintWriter out = null ;
        try  {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream() ) ) ;
            out.println(sendStrName) ;
            out.flush();
        }catch(Exception e1)  {
            e1.printStackTrace();
        }


        //踢人
        JTextField nameText = new JTextField(null,8);
        bPanel.add(nameText) ;

        //踢人
        JButton removeButton = new JButton("Kick");
        removeButton.addActionListener( new ActionListener( )  {
            public void actionPerformed(ActionEvent e)  {
                String[] strs = userListArea.getText().split("\n");
//                System.out.println(strs[0]);
//                System.out.println(strs[1]);
                String nt = nameText.getText();
                if(! nt.equals("")){
                    System.out.println(nt);
                    for(int i = 0;i <strs.length;i++){
                        if(nt.equals(strs[i])){
//                            System.out.println("进来了");
                            if(nt.equals(name)){
                                JOptionPane.showMessageDialog(null,"You cannot kick yourself!");
                            }
                            if(! nt.equals(name)){
                                String sendStrKick="{\"name\":\"Kick\",\"x1\":0,\"y1\":0,\"x2\":0,\"y2\":0,\"name\":\"Kick\",\"red\":255,\"green\":255,\"blue\":255,\"text\":\""+ nt +"\"}";

                                PrintWriter out = null ;
                                try  {
                                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream() ) ) ;
                                    out.println(sendStrKick) ;
                                    out.flush();
                                }catch(Exception e1)  {
                                    e1.printStackTrace();
                                }
                                //这里应该再加一个删除userlist上显示的被踢掉的用户名的功能
                                userListArea.setText("");
                                for(int j=0;j<i;j++) {
                                    userListArea.append(strs[j]);
                                    userListArea.append("\n");
                                }
                                for(int k=i+1;k<strs.length;k++){
                                    userListArea.append(strs[k]);
                                    userListArea.append("\n");
                                }


                                JOptionPane.showMessageDialog(null,"User has been kicked!");
                            }
                            nameText.setText("");



                        }
                    }
                }
            }
        });



        bPanel.add(removeButton) ;
        if (authority.equals("isClient")){
            removeButton.setEnabled(false);
        }

        //关闭聊天窗口
        JButton closeButton = new JButton("Close") ;
        closeButton.addActionListener( new ActionListener( )  {
            public void actionPerformed(ActionEvent e)  {
                dispose();
            }
        });
        bPanel.add(closeButton) ;

        //发送聊天信息
        JButton sendButton = new JButton("Send") ;
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                String str = sendArea.getText() ;
                SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss") ;
                String time  = formater.format(new Date() ) ;
                String chatContent = name+" "+time+" said: "+str ;

                String sendStrFull="{\"name\":\"Chatting\",\"x1\":0,\"y1\":0,\"x2\":0,\"y2\":0,\"name\":\"Chatting\",\"red\":255,\"green\":255,\"blue\":255,\"text\":\""+ chatContent +"\"}";
                System.out.println(sendStrFull);

                contentArea.append(chatContent);
                contentArea.append("\n");

                PrintWriter out = null ;
                try  {
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream() ) ) ;
                    out.println(sendStrFull) ;
                    out.flush();
                }catch(Exception e1)  {
                    e1.printStackTrace();
                }
                sendArea.setText("");
            }
        });

        bPanel.add(sendButton) ;

    }

}

//画图程序的thread
class PaintThread extends Thread  {

    private String authority;
    private Socket socket;

//    public PaintThread(String authority){
//        this.authority = authority;
//    }
    public PaintThread(String authority, Socket socket){
        this.authority = authority;
        this.socket = socket;
    }


    public void run()  {
        PainterUI pui = new PainterUI();
        pui.initUI(authority,socket);
    }
}

class MsgThread extends Thread  {

    private Socket socket ;

    public MsgThread(Socket socket)  {
        this.socket = socket ;
    }

    public void run()  {
        //不停地循环检查是否有新的消息
        while(true){
            if(ClientUtils.getJsonMsg().size()!=0){
                //取出第一个元素
                String msg = ClientUtils.getJsonMsg().remove(0);
                PrintWriter out = null;
                try  {
                    //将msg发送给服务器
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.println(msg);
                    out.flush();
                }catch(Exception e1)  {
                    e1.printStackTrace();
                }

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


//客户端与服务器端通信的线程类
class ClientThread extends Thread  {
    private Socket socket ;
    private JTextArea contentArea ;
    private JTextArea userListArea;
    private String name;

    public ClientThread(Socket socket, JTextArea  conteArea, JTextArea userListArea, String name)  {
        this.socket = socket ;
        this.contentArea = conteArea ;
        this.userListArea = userListArea;
        this.name = name;
    }

    public void run()  {
        BufferedReader br = null ;
        try  {
            //从socket读取数据
            br = new BufferedReader(new InputStreamReader(socket.getInputStream())) ;
            String str = null ;
            while( (str = br.readLine()) != null)  {
                System.out.println(str) ;
                String chatContent = ClientUtils.putShape(str);
                String nameContent = ClientUtils.putName(str);
                String kickContent = ClientUtils.putKick(str);
                String EXITContent = ClientUtils.putEXIT(str);
//                System.out.println(chatContent);
//                System.out.println(nameContent);
                //说明这个是聊天内容
                if(chatContent!=null){
                    contentArea.append(chatContent);
                    contentArea.append("\n");
                }
                //说明是用户名信息
                if(nameContent!=null){
                    userListArea.append(nameContent);
                    userListArea.append("\n");
                }
                //说明是踢人请求
                if(kickContent!=null){
                    if(kickContent.equals(name)){
                        socket.close();
                        System.exit(0);
                    }
                }
                //管理员要关闭所有的程序
                if(EXITContent!=null){
                    if(EXITContent.equals("EXIT")){
                        socket.close();
                        System.exit(0);
                    }
                }

            }
        } catch(IOException e)  {
            e.printStackTrace();
        } finally  {
            if(br != null)  {
                try  {
                    br.close () ;
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }}

