package com.example.finalproject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

public class View2Controller {
    @FXML
    private TextField HostID;

    @FXML
    private TextField PortID;

    @FXML
    private ListView listView;
    @FXML
    private TextField myMessage;

    PrintWriter pw;

    @FXML
    public void Connect() throws Exception{
        String host=HostID.getText();
        int port=Integer.parseInt(PortID.getText());
        //Socket
        Socket s=new Socket(host,port);
        InputStream is = s.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        OutputStream os = s.getOutputStream();
        String Ip = s.getRemoteSocketAddress().toString();
        pw=new PrintWriter(os,true);
        new Thread(()->{
            while (true){
                try {
                    String reponse=br.readLine();
                    Platform.runLater(()->{
                        listView.getItems().add(reponse);
                    });
                    HostID.setDisable(true);
                    PortID.setDisable(true);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void close(){
        System.exit(0);
    }

    @FXML
    public void Envoyer(){
        String message= myMessage.getText();
        pw.println(message);
        myMessage.setText("");
    }
}
