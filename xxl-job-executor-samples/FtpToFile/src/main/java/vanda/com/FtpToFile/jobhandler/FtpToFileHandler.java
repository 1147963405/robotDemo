package vanda.com.FtpToFile.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import vanda.com.FtpToFile.unit.FtpToFileUnits;


/**
 * XxlJob开发示例（Bean模式）
 *
 * 开发步骤：
 * 1、在Spring Bean实例中，开发Job方法，方式格式要求为 "public ReturnT<String> execute(String param)"
 * 2、为Job方法添加注解 "(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
@Configuration
public class FtpToFileHandler {

    private static Logger logger = LoggerFactory.getLogger(FtpToFileHandler.class);

    @Value("${ftp.server.ip}")
    private String ip;

    @Value("${ftp.server.port}")
    private int port;

    @Value("${ftp.server.username}")
    private String userName;

    @Value("${ftp.server.password}")
    private String password;


    @XxlJob("ftpJobHandler2")
    public ReturnT<String> ftpJobHandler2(String param){
        logger.debug("=============<<<<<<>>>>>>>>>===============");
       FtpToFileUnits ftpToFileUnits=new FtpToFileUnits(ip,port,userName,password);
        //连接FTP服务器
       ftpToFileUnits.ftpLogin();
        //下载文件
        ftpToFileUnits.downloadDirectory("/ftpTest","D:");
        //删除文件
        ftpToFileUnits.deleteDirector("/ftpTest");
        //关闭FTP服务器
        ftpToFileUnits.ftpLogOut();
        return ReturnT.SUCCESS;
    }


  /*  public static void main(String[] args) throws Exception {

        FtpToFileHandler ftpToFileHandler=new FtpToFileHandler();
        ReturnT<String> stringReturnT = ftpToFileHandler.ftpJobHandler2("E:\\vanda\\yk_temp");
        System.out.println("========================="+stringReturnT);
}*/
}
