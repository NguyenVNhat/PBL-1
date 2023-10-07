package SendFile;

import java.io.Serializable;
import java.util.ArrayList;

public class FilesAndIp implements Serializable {
    private ArrayList<String> ListFileName = new ArrayList<>();
    private String IpClient;
    public  FilesAndIp()
    {

    }
    public FilesAndIp(ArrayList<String> listFileName,String ipClient)
    {
        this.ListFileName = listFileName;
        this.IpClient = ipClient;
    }
    public void setListFileName(ArrayList<String> listFileName)
    {
        this.ListFileName = listFileName;
    }
    public void setIpClient(String ipClient)
    {
        this.IpClient = ipClient;
    }
    public ArrayList<String> getListFileName()
    {
        return this.ListFileName;
    }
    public String getIpClient()
    {
        return this.IpClient;
    }
}
