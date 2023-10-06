///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
package chat_room_ui;
//
//
//import java.awt.Color;
//import java.io.IOException;
//import java.net.InetAddress;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JList;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//
///**
// *
// * @author Son Truong
// */
//public class Client extends JFrame{
//
//    public Client()
//    {
//         this.setTitle("CLIENT");
//        this.setSize(900,500);
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        getContentPane().setLayout(null);
//        
//        JLabel lblport =  new JLabel("PORT NO.");
//        lblport.setBounds(30,30,200,30);
//        lblport.setForeground(Color.red);
//        this.add(lblport);
//        JTextField txtport= new JTextField();
//        txtport.setBounds(120,30,200,30);
//        this.add(txtport);
//        
//        
//        JButton start= new JButton("Start");
//        start.setBounds(320,30,100,30);
//        this.add(start);
//        
//         String[] items = {};
//        JList<String> list = new JList<>(items);
//        JScrollPane scrollPane = new JScrollPane(list);
//        scrollPane.setBounds(30, 100, 350, 300);
//        this.add(scrollPane);
//        
//        JButton btnyeucau = new JButton("Yêu Cầu");
//        btnyeucau.setBounds(80,420,100,30);
//        btnyeucau.setBackground(Color.yellow);
//        this.add(btnyeucau);
//        
//        
//        
//        JPanel jp= new JPanel();
//        jp.setLayout(null);
//        jp.setSize(200,200);
//        jp.setBounds(420, 30, 450, 400);
//        jp.setBackground(Color.LIGHT_GRAY);
//        JLabel lb1= new JLabel("MESSENGER");
//        lb1.setBounds(180, 10   , 350, 30);
//        lb1.setForeground(Color.RED);
//       
//        jp.add(lb1);
//        
//           String[] itemschat = {};
//        JList<String> listitemchat = new JList<>(itemschat);
//        JScrollPane jScrollPaneChat = new JScrollPane(listitemchat);
//        jScrollPaneChat.setBounds(20, 50, 370, 230);
//        jp.add(jScrollPaneChat);
//        
//        
//        JTextArea txtchat= new JTextArea();
//        txtchat.setBounds(20, 320, 300, 50);
//        jp.add(txtchat);
//        
//        
//        JButton btnsent= new JButton("SEND");
//        btnsent.setBounds(330,320,100,50);
//        jp.add(btnsent);
//        
//        
//        this.add(jp);
//        
//        
//        this.setVisible(true);
//    }
//      public static void main(String[] args) throws IOException {
//        new Client();
//    }
//   
//}




import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private  InetAddress host;
    private  int port;
    public Client(InetAddress host,int port)
    {
        this.host=host;
        this.port=port;
    }
    private void execute1() throws IOException
    {
        Socket client=new Socket(host,port);
        readClient read= new readClient(client);
        read.start();
        Scanner sc= new Scanner(System.in);
        System.out.println("Nhap vao ten cua ban:");
        String name=sc.nextLine();
        
        WriteClient write= new WriteClient(client,name);
        write.start();
    }
    
    public static void main(String[] args) throws IOException {
        Client client= new Client(InetAddress.getLocalHost(), 15797);
        client.execute1();
    }
    
}

class readClient extends Thread
{
    private Socket client;
    public readClient(Socket client)
    {
        this.client=client;
    }
    @Override
    public void run()
    {
        DataInputStream dis=null;
        try {
            dis=new DataInputStream(client.getInputStream());
            while (true) {                
              String sms=  dis.readUTF();
                System.out.println(sms);
                
              
            }
        } catch (Exception e) {
            try {
                 dis.close();
            client.close();
            } catch (IOException  ex) {
                
            }
        }
    }
}
class WriteClient extends Thread{
    private Socket client;
    private  String name;
    public WriteClient(Socket client,String name)
    {
        this.client=client;
        this.name=name;
    }
    @Override
    public void run()
    {
        DataOutputStream dos=null;
        Scanner sc= null;
        try {
            dos=new DataOutputStream(client.getOutputStream());
            sc= new Scanner(System.in);
            while(true)
            {
                String sms= sc.nextLine();
                dos.writeUTF(name+":"+sms);
         
               
            }
        } catch (Exception e) {
              try {
                 dos.close();
            client.close();
                } catch (IOException  ex) {
                  System.out.println("Ngat ket noi");
            }
        }
    }
}

