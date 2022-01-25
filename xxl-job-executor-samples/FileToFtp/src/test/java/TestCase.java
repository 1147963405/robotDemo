import org.junit.Test;

public class TestCase {

    @Test
    public void FavFTPTest(){

        String hostname = "192.168.0.125";
        int port = 21;
        String username = "lyw";
        String password = "123456";
        String pathname = "vanda/lv";
        String filename2="ly";
        String filename = "ly";
        String originfilename = "D:/li/csfw/pom.xml";
        String originfilename2 = "D:/";
        //FavFTPUtil.uploadFileFromProduction(hostname, port, username, password, pathname, filename, originfilename);//本地文件上传ftp服务器
        //FavFTPUtil.downloadFile(hostname, port, username, password, pathname, filename, originfilename2);//ftp服务器文件下载到本地

        //FavFTPUtil.deleteFile(hostname, port, username, password, pathname, filename2);//删除ftp服务器下的文件
    }

}
