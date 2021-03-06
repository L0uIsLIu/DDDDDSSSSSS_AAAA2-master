package com.fei.paintUI.util;

import com.fei.paintUI.shape.Shape;

import java.util.LinkedList;
import java.util.List;

public class ClientUtils {


    private static List<String> jsonMsg = new LinkedList<String>();

    private static List<Shape> shapes = new LinkedList<Shape>();

    //用来让UI放json数据
    public static List<String> getJsonMsg() {
        return jsonMsg;
    }

    public static void setJsonMsg(List<String> jsonMsg) {
        ClientUtils.jsonMsg = jsonMsg;
    }

    public static void sendMessage(Shape shape) {
        shape.setRGB();         //设置一下三个分色
        jsonMsg.add(JacksonUtil.shape2Json(shape));
    }

    public static Boolean isEmpty(){
        if(shapes.size()==0){
            return true;
        }else{
            return false;
        }
    }

    public static String putShape(String jsonStr) {
        //每次只有一个
        Shape shape = JacksonUtil.json2Shape(jsonStr);

        List list = new LinkedList ();

        System.out.println(shape);

        if(shape.name.equals("Chatting")){
            //说明是聊天内容
            return shape.text;
        }else{
            shapes.add(shape);
            return null;
        }
    }

    //读取用户名
    public static String putName(String jsonStr) {
        //每次只有一个
        Shape shape = JacksonUtil.json2Shape(jsonStr);

        List list = new LinkedList ();

        System.out.println(shape);

        if(shape.name.equals("Username")){
            //说明是用户名
            return shape.text;
        }else{
            shapes.add(shape);
            return null;
        }
    }

    //踢人
    public static String putKick(String jsonStr) {
        //每次只有一个
        Shape shape = JacksonUtil.json2Shape(jsonStr);

        List list = new LinkedList ();

        System.out.println(shape);

        if(shape.name.equals("Kick")){
            //说明是踢人
            return shape.text;
        }else{
            shapes.add(shape);
            return null;
        }
    }

    //EXIT
    public static String putEXIT(String jsonStr) {
        Shape shape = JacksonUtil.json2Shape(jsonStr);

        List list = new LinkedList ();

        System.out.println(shape);

        if(shape.name.equals("EXIT")){

            return shape.text;
        }else{
            shapes.add(shape);
            return null;
        }
    }

    public static Shape getShape() {
        if(shapes.size()!=0) {
            return shapes.remove(0);
        }
        //每次只有一个
        return null;
    }

}
