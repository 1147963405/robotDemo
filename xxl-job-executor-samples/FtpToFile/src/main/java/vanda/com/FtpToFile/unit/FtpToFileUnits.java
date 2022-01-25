package vanda.com.FtpToFile.unit;

import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.net.MalformedURLException;
import java.util.TimeZone;

public class FtpToFileUnits {

    private final Logger logger = LoggerFactory.getLogger(FtpToFileUnits.class);

    public FTPClient ftp=null;

    /** ftp服务器地址 */
    private String hostname;

    /** 端口号 */
    private Integer port;

    /** ftp登录账号 */
    private String username;

    /** ftp登录密码 */
    private String password;

    public FtpToFileUnits(String hostname, Integer port, String username, String password) {
        super();
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * @param hostname
     *            FTPServer ip
     * @param port
     *            FTPServer 端口
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return FtpUtil实例
     * @date 2018年9月26日 下午4:39:02
     */
    public static FtpToFileUnits getFtpUtilInstance(String hostname, Integer port, String username, String password) {
        return new FtpToFileUnits(hostname, port, username, password);
    }

    /**
     * 初始化ftp服务器
     */
    public void initFtpClient() {
        ftp = new FTPClient();
        ftp.setControlEncoding("GB2312");
        try {
            ftp.connect(hostname, port); //连接ftp服务器
            ftp.login(username, password); //登录ftp服务器
            int replyCodes = ftp.getReplyCode();//是否成功登录服务器
            if(!FTPReply.isPositiveCompletion(replyCodes)){
                System.out.println("connect failed...ftp服务器:"+hostname+":"+port);
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 判断是否登入成功
     * */
    public boolean ftpLogin() {
        initFtpClient();
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        ftp.setControlEncoding("GB2312");
        ftp.configure(ftpClientConfig);
        try {
            if (port > 0) {
                ftp.connect(hostname, port);
            }else {
                ftp.connect(hostname);
            }
            // FTP服务器连接回答
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                logger.info("登录FTP服务失败！");
                return isLogin;
            }
            ftp.login(this.username, this.password);
            // 设置传输协议
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);//二进制文件
            logger.info("恭喜" + this.username + "成功登陆FTP服务器");
            isLogin = true;
        }catch (Exception e) {
            e.printStackTrace();
            logger.info(this.username + "登录FTP服务失败！" + e.getMessage());
        }
        ftp.setBufferSize(1024*1024);
        ftp.setDataTimeout(30*1000);
        return isLogin;
    }


    /**
     * @退出关闭服务器链接
     * */
    public void ftpLogOut() {
        initFtpClient();
        if (null != ftp && ftp.isConnected()) {
            try {
                boolean reuslt = ftp.logout();// 退出FTP服务器
                if (reuslt) {
                    logger.info("成功退出服务器");
                }
            }catch (IOException e) {
                e.printStackTrace();
                logger.info("退出FTP服务器异常！" + e.getMessage());
            }finally {
                try {
                    ftp.disconnect();// 关闭FTP服务器的连接
                }catch (IOException e) {
                    e.printStackTrace();
                    logger.info("关闭FTP服务器的连接异常！");
                }
            }
        }
    }



    /** * 下载文件夹
     * @param pathname FTP服务器文件目录 *
     * @param /filename 文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return */
    public  boolean downloadDirectory(String pathname,String localpath){
        boolean flag = false;
        OutputStream os=null;
        try {
            initFtpClient();//初始化ftp服务
            File f=new File(pathname);
            String localFile = localpath +"/"+f.getName();
            ftp.changeWorkingDirectory(pathname);
            boolean mkdirs = new File(localFile).mkdirs();
            System.out.println("pathname : " + pathname);
            System.out.println("localpath : " + localpath);
            System.out.println("localFile : " + localFile);
            System.out.println("mkdirs : " + mkdirs);
            //切换FTP目录
            FTPFile[] allFile = ftp.listFiles();
            for(FTPFile file : allFile){
                if (!file.isDirectory()) {
                    downloadFile(file.getName(),localFile);
                }
            }
            for(FTPFile file : allFile){
                if (file.isDirectory()) {
                    downloadDirectory(pathname+"/"+file.getName(),localFile);
                }
            }
            flag = true;
        } catch (Exception e) {
            System.out.println("下载文件失败");
            e.printStackTrace();
        } finally{
            if(ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /***
     * 下载文件
     * @param /remoteFileName   待下载文件名称
     * @param /localDires 下载到当地那个路径下
     * @param /remoteDownLoadPath remoteFileName所在的路径
     * @param localDires  */
    public boolean downloadFile(String remoteFileName, String localDires) {
        String strFilePath = localDires +"/"+ remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            ftp.changeWorkingDirectory(remoteFileName);//改变工作路径
            outStream = new BufferedOutputStream(new FileOutputStream(strFilePath));
            logger.info(remoteFileName + "开始下载....");
            success = ftp.retrieveFile(remoteFileName, outStream);
            if (success == true) {
                logger.info(remoteFileName + "下载成功");
                return success;
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.info(remoteFileName + "下载失败");
        } finally{
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }


    /** * 删除文件*
     * @param remoteDir FTP服务器保存目录 *
     * @return
     * **/

    public void deleteDirector(String remoteDir){
        try {
            System.out.println("==remoteDir=="+remoteDir);
            initFtpClient();
            ftp.changeWorkingDirectory(remoteDir);
            FTPFile[] allFile = ftp.listFiles();
            for(FTPFile file : allFile){
                if (file.isFile()) {
                    logger.info(file.getName()+"开始删除");
                    String ftpFile = remoteDir + "/" + file.getName();
                    ftp.deleteFile(ftpFile);
                }
            }
            for(FTPFile file : allFile){
                if (file.isDirectory()) {
                    deleteDirector(remoteDir + "/" + file.getName());
                }
            }
            ftp.removeDirectory(remoteDir);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(remoteDir+"删除失败");
        }
    }

  /*  public static void main(String[] args) throws IOException {
        FtpToFileUnits ftpUtil=new FtpToFileUnits("169.254.156.235",2121,"lyw","123456");
        ftpUtil.ftpLogin();//ftp服务器初始化
        ftpUtil.downloadDirectory("/ftpTest","D:");
        ftpUtil.deleteDirector("/ftpTest");
        ftpUtil.ftpLogOut();
    }*/

}
