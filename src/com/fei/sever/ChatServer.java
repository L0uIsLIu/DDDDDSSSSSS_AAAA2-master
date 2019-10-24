package com.fei.sever;


import com.fei.paintUI.shape.Shape;
import com.fei.paintUI.util.JacksonUtil;

import java.io.*;
import java.util.List;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ChatServer {

    private List<Socket> sockets = new ArrayList<Socket>() ;    //类集的应用
    private int port = 8888;

    public ChatServer(String[] args) throws IOException  {
        setPort(args);
        ServerSocket ss = new ServerSocket(port) ;
        System.out.println("Server is listening the port : "+port) ;

        while(true)  {
            Socket socket = ss.accept();
            sockets.add(socket);
            String ip = socket.getInetAddress().getHostAddress();
            System.out.println("New client！IP is: "+ip);
            Thread thread = new Thread(new ServerRunner(sockets,socket));
            thread.start();
        }
    }

    private void setPort(String[] args) {
        try {
            port = Integer.parseInt(args[0]);
            if (port > 65535 || port <= 0) {
                System.out.println("Invalid port number!");
                System.out.println("Using default port: 8888");

                port = 8888;
            } else {
                System.out.println("Using port: " + port);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No port number!");
            System.out.println("Using default port: 8888");
            port = 8888;

        } catch (NumberFormatException e) {
            System.out.println("Wrong port number format!");
            System.out.println("Using default port: 8888");
            port = 8888;

        }

    }

    public static void main(String[] args)  {
        try {
            new ChatServer(args) ;
        } catch(Exception e)  {
            e.printStackTrace();
        }
    }

}

class ServerRunner implements Runnable  {
    private List<Socket> sockets ;
    private Socket currentSocket ;   //当前socket

    public ServerRunner (List<Socket> sockets,Socket currentSocket)  {
        this.sockets = sockets ;
        this.currentSocket = currentSocket ;
    }

    public void run() {
        String ip = currentSocket.getInetAddress().getHostAddress();
        BufferedReader br = null ;
        try  {

            DataOutputStream output = new DataOutputStream(currentSocket.getOutputStream());
            if(sockets.size() == 1){
                output.writeUTF("isManager");
                System.out.println("isManager");
                output.flush();
            }else{
                output.writeUTF("isClient");
                System.out.println("isClient");
                output.flush();
            }


            br = new BufferedReader(new InputStreamReader(currentSocket.getInputStream())) ;
            String str = null ;
            while((str = br.readLine()) != null)  {

                for(Socket temp : sockets)  {
                    if(temp!=currentSocket){
                        PrintWriter pw = new PrintWriter(new OutputStreamWriter(temp.getOutputStream())) ;
                        pw.println(str) ;
                        pw.flush();
                    }

                }
            }


        }catch(IOException e)  {
            e.printStackTrace();
        }
    }
}


