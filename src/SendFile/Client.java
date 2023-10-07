package SendFile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
    private static ArrayList<String> selectedFiles = new ArrayList<>();
    private static String ipServer = "localhost";
    private static String  myIP;
    public static void processIP() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();

        String TempmyIP = ip.toString();
        String[] parts = TempmyIP.split("/");
        if (parts.length > 1) {
            myIP = parts[1];
        }
    }




    public static void main(String[] args) throws IOException {
        JFrame jFrame = new JFrame();
        jFrame.setSize(500,600);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(3);

        JLabel lbPort = new JLabel("Port :");
        lbPort.setBounds(10,10,30,30);

        JTextArea jTextArea = new JTextArea("4321");
        jTextArea.setBounds(50,10,200,30);

        JButton jButton1 = new JButton("Yêu cầu");
        jButton1.setBounds(50,490,200,40);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Tên tệp client");
        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50,50,200,400);

        jFrame.add(jButton1);
        jFrame.add(lbPort);
        jFrame.add(jTextArea);
        jFrame.add(scrollPane);
        jFrame.setVisible(true);

        processIP();
        //kết nối tới server
        ConnectToServer();

        Thread showFilesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        ShowInfo_ForClient(model);
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        });
        showFilesThread.start();


        ServerSocket serverSocket = new ServerSocket(4323);
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFiles.clear();

                int[] selectedRows = table.getSelectedRows();
                ShowListFile(selectedRows, model, jFrame);

                Thread getFileThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GetFile(serverSocket);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                getFileThread.start();

                try {
                    sendMessage("YeuCau");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }
    private static void ConnectToServer() throws IOException {
        Socket socketMain = new Socket(ipServer,4321);
        OutputStream out = socketMain.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeUTF("Connect from Client");
    }
    private static void ShowInfo_ForClient(DefaultTableModel model) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4321);
        while(true) {
            Socket socketReceive = serverSocket.accept();
            try (InputStream inputStream = socketReceive.getInputStream();
                 DataInputStream dataInputStream = new DataInputStream(inputStream)) {

                String fileName = dataInputStream.readUTF();
                model.addRow(new Object[]{fileName} );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void sendMessage(String message) throws IOException {
        System.out.println(myIP);
        FilesAndIp filesAndIp = new FilesAndIp(selectedFiles,myIP);

        for (String fileName: selectedFiles
        ) {
            Socket socketSend = new Socket(ipServer, 4322);
            OutputStream out = socketSend.getOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(filesAndIp);
        }
    }
    public static void GetFile(ServerSocket serverSocket) throws IOException {
        System.out.println("B5 : Nhan file");

        for (int i = 0; i < selectedFiles.size(); i++) {
            Socket socketReceive = serverSocket.accept();
            System.out.println(socketReceive);

            try (InputStream inputStream = socketReceive.getInputStream();
                 DataInputStream dataInputStream = new DataInputStream(inputStream)) {

                String fileName = dataInputStream.readUTF();
                System.out.println(fileName);
                String FileNames = "DataClient\\" + fileName;

                try (FileOutputStream fileOutputStream = new FileOutputStream(FileNames)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }

                    System.out.println("Received file: " + FileNames);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socketReceive.close();
            }
        }
    }


    public static void ShowListFile(int[] selectedRows,DefaultTableModel model,JFrame jFrame)
    {
        for (int rowIndex : selectedRows) {
            String fileName = (String) model.getValueAt(rowIndex, 0);
            selectedFiles.add(fileName);
        }
        // Hiển thị danh sách các tệp được chọn
        if (!selectedFiles.isEmpty()) {
            StringBuilder fileList = new StringBuilder("Danh sách file được chọn để đồng bộ \n\n");
            for (String fileName : selectedFiles) {
                fileList.append(fileName).append("\n");
            }
            JOptionPane.showMessageDialog(jFrame, fileList.toString());
        } else {
            JOptionPane.showMessageDialog(jFrame, "Không có file nào được chọn");
        }
    }
}