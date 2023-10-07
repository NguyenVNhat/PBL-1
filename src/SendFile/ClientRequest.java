package SendFile;

import javax.swing.*;

class ClientRequest{
    private JLabel jLabel =new JLabel();
    private JTextArea jTextArea = new JTextArea();


    public ClientRequest(int xJLabel,int yJLabel,int wJTextArea,int hJTextArea,String LbText,String TaText,JFrame jFrame)
    {
        jLabel.setBounds(xJLabel,yJLabel,50,30);
        jTextArea.setBounds(xJLabel+50,yJLabel,wJTextArea,hJTextArea);
        this.jLabel.setText(LbText);
        this.jTextArea.setText(TaText);
        jFrame.add(jLabel);
        jFrame.add(jTextArea);
    }
}