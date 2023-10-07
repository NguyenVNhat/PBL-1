package SendFile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static ArrayList<String> selectedFiles = new ArrayList<>();
    private static ArrayList<String> listFile = new ArrayList<>();
    public static void main(String[] args) throws IOException {

        JFrame jFrame = new JFrame();
        jFrame.setSize(800,600);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(3);

        JLabel lbPort = new JLabel("Port :");
        lbPort.setBounds(10,10,30,30);

        JTextArea jTextArea = new JTextArea("4321");
        jTextArea.setBounds(50,10,200,30);

        JButton jButton1 = new JButton("Dong bo");
        jButton1.setBounds(50,490,200,40);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Tên tệp");
        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50,50,200,400);



        File dataDirectory = new File("Data");
        jFrame.add(jButton1);
        jFrame.add(lbPort);
        jFrame.add(jTextArea);
        jFrame.add(scrollPane);
        jFrame.setVisible(true);
        addFilesAndDirectories(dataDirectory, model);

        new Thread(() -> {
            try {
                while (true) {
                    if (TestConnect()) {
                        SenInfo_ForClient(model,4321);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        ServerSocket serverSocket = new ServerSocket(4322);
        FilesAndIp receive =  Synchnorous(serverSocket);

        String filenameList ="";
        for (String fileName :receive.getListFileName()
        ) {
            filenameList+= fileName+"\n";
        }

        ClientRequest clientRequest = new ClientRequest(400,50,300,300,receive.getIpClient(),filenameList,jFrame);



    }

    private static Boolean TestConnect() throws IOException {

        String connect = "";
        ServerSocket serverSocketMain = new ServerSocket(4321);
        Socket socketMain = serverSocketMain.accept();
        try (InputStream inputStream = socketMain.getInputStream();
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            connect = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketMain.close();
        serverSocketMain.close();
        if (connect.equals("Connect from Client")) return true;
        return false;

    }
    private static  void SenInfo_ForClient(DefaultTableModel model ,int port)
    {
        for (int i = 0; i < model.getRowCount(); i++) {
            String fileName = (String) model.getValueAt(i, 0);
            listFile.add(fileName);
        }
        for (String filename: listFile
        ) {
            sendFile(filename,"localhost",port);
        }
    }
    private  static  FilesAndIp  Synchnorous(ServerSocket serverSocket) throws IOException {

        while (true) {
            FilesAndIp receivedFilesAndIp = new FilesAndIp() ;
            Socket socketReceive = serverSocket.accept();
            try (InputStream inputStream = socketReceive.getInputStream())
            {
                ObjectInputStream objectIn = new ObjectInputStream(inputStream);
                receivedFilesAndIp = (FilesAndIp) objectIn.readObject();
                for (String fileName:receivedFilesAndIp.getListFileName()
                ) {
                    sendFile(fileName, receivedFilesAndIp.getIpClient(), 4323);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            socketReceive.close();
            return receivedFilesAndIp;
        }
    }
    private static void sendFile(String fileName,String ipaddress ,Integer port) {
        try {
            fileName = "Data\\"+fileName;
            Socket socketSend = new Socket(ipaddress, port);
            OutputStream out = socketSend.getOutputStream();
            FileInputStream inSend = new FileInputStream(fileName);
            DataOutputStream dataOut = new DataOutputStream(out);
            dataOut.writeUTF(new File(fileName).getName());

            byte[] buffer = new byte[1024];
            int count;
            while ((count = inSend.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
            socketSend.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static void addFilesAndDirectories(File directory, DefaultTableModel model) {
        File[] filesAndDirs = directory.listFiles();

        if (filesAndDirs != null) {
            for (File fileOrDir : filesAndDirs) {
                if(fileOrDir.isFile()) {
                    String name = fileOrDir.getName();
                    model.addRow(new Object[]{name});
                }

                if (fileOrDir.isDirectory()) {
                    addFilesAndDirectories(fileOrDir, model);
                }
            }
        }
    }

}