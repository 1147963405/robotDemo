package vanda.com.FlieToFtp.unit;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.URLConnection;
import java.util.TimeZone;
import java.util.logging.Logger;

public class FileToFtpUnits {

    private FTPClient ftpClient;
    private String strIp;
    private int intPort;
    private String user;
    private String password;

    private static Logger logger = Logger.getLogger(FileToFtpUnits.class.getName());

    /* *
     * Ftp构造函数
     */

    public FileToFtpUnits(String strIp, int intPort, String user, String Password) {

        this.strIp = strIp;
        this.intPort = intPort;
        this.user = user;
        this.password = Password;
        this.ftpClient = new FTPClient();

    }
    /**
     * @return 判断是否登入成功
     * */
    public boolean ftpLogin() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        this.ftpClient.setControlEncoding("GB2312");
        this.ftpClient.configure(ftpClientConfig);
        try {
            if (this.intPort > 0) {
                this.ftpClient.connect(this.strIp, this.intPort);
            }else {
                this.ftpClient.connect(this.strIp);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                logger.info("登录FTP服务失败！");
                return isLogin;
            }
            this.ftpClient.login(this.user, this.password);
            // 设置传输协议
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            logger.info("恭喜" + this.user + "成功登陆FTP服务器");
            isLogin = true;
        }catch (Exception e) {
            e.printStackTrace();
            logger.info(this.user + "登录FTP服务失败！" + e.getMessage());
        }
        this.ftpClient.setBufferSize(1024 * 2);
        this.ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }


    /**
     * @退出关闭服务器链接
     * */
    public void ftpLogOut() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                    logger.info("成功退出服务器");
                }
            }catch (IOException e) {
                e.printStackTrace();
                logger.info("退出FTP服务器异常！" + e.getMessage());
            }finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接
                }catch (IOException e) {
                    e.printStackTrace();
                    logger.info("关闭FTP服务器的连接异常！");
                }
            }
        }
    }


    /***
     * 上传Ftp文件
     * @param localFile 当地文件
     * @param romotUpLoadePath 上传服务器路径 - 应该以/结束
     * */

    public boolean uploadFile(File localFile, String romotUpLoadePath) {
        BufferedInputStream inStream = null;
        logger.info("============"+localFile);
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            logger.info("============"+inStream);
            logger.info(localFile.getName() + "开始上传.....");
            logger.info("============"+localFile.getName());
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            if (success == true) {
                logger.info(localFile.getName() + "成功上传到"+romotUpLoadePath);
                return success;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info(localFile + "未找到");
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != inStream) {
                try {
                    inStream.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /***
     * @上传文件夹
     * @param localDirectory
     *            当地文件夹
     * @param remoteDirectoryPath
     *            Ftp 服务器路径 以目录"/"结束
     * */
    public boolean uploadDirectory(String localDirectory, String remoteDirectoryPath) {
        File src = new File(localDirectory);
        try {
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            boolean makeDirFlag = this.ftpClient.makeDirectory(remoteDirectoryPath);

            System.out.println("localDirectory : " + localDirectory);
            System.out.println("remoteDirectoryPath : " + remoteDirectoryPath);
            System.out.println("src.getName() : " + src.getName());
            System.out.println("makeDirFlag : " + makeDirFlag);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info(remoteDirectoryPath + "目录创建失败");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] allFile = src.listFiles();
        for (int currentFile = 0;currentFile < allFile.length;currentFile++) {
            if (!allFile[currentFile].isDirectory()) {
                String srcName = allFile[currentFile].getPath().toString();
                uploadFile(new File(srcName), remoteDirectoryPath);
            }
        }
        for (int currentFile = 0;currentFile < allFile.length;currentFile++) {
            if (allFile[currentFile].isDirectory()) {
                // 递归
                uploadDirectory(allFile[currentFile].getPath().toString(),remoteDirectoryPath);
            }
        }
        return true;
    }

    // FtpClient的Set 和 Get 函数
    public FTPClient getFtpClient() {
        return ftpClient;
    }
    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }



    // 删除文件夹
    // param folderPath 文件夹完整绝对路径
    public  void delFolder(String folderPath) {
        try {

            delAllFile(folderPath);// 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 删除指定文件夹下所有文件
    // param localPath 文件夹完整绝对路径
    public  boolean delAllFile(String localPath) {
        boolean flag = false;
        File file = new File(localPath);
        System.out.println("file >>>>>>> "+file);
        System.out.println("localPath >>>>>>> "+localPath);
        System.out.println("file >>>>>>> "+file.getName());
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i <tempList.length; i++) {
            if (localPath.endsWith(File.separator)) {
                temp = new File(localPath + tempList[i]);
            } else {
                temp = new File(localPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                logger.info(temp+"开始删除...");
                temp.delete();
                logger.info(temp+"删除成功...");
            }
            if (temp.isDirectory()) {
                System.out.println(" temp  >>>>>   "+temp);
                delAllFile(localPath + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(localPath + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        file.delete(); //目录空了再进行删除
        return flag;
    }

    public static void main(String[] args) throws IOException {
        FileToFtpUnits ftp=new FileToFtpUnits("192.168.11.76",21,"iclean","123456");
        ftp.ftpLogin();
        System.out.println("1");
        //上传文件夹
        boolean uploadFlag = ftp.uploadDirectory("D:\\localhostPath", "/"); // /那么传的就是所有文件，如果只是/那么就是传文件夹
        System.out.println("uploadFlag : " + uploadFlag);
        //下载文件夹
//         ftp.delAllFile("D:/mq");
//        ftp.ftpLogOut();
    }
}
