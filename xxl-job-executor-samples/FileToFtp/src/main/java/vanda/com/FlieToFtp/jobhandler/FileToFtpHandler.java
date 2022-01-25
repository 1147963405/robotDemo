package vanda.com.FlieToFtp.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vanda.com.FlieToFtp.unit.FileToFtpUnits;



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
public class FileToFtpHandler{

    private static Logger logger = LoggerFactory.getLogger(FileToFtpHandler.class);

    @Value("${ftp.server.ip}")
    private String ip;

    @Value("${ftp.server.port}")
    private int port;

    @Value("${ftp.server.username}")
    private String userName;

    @Value("${ftp.server.password}")
    private String password;


    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("ftpJobHandler")
    public ReturnT<String> ftpJobHandler(String param)  {
        FileToFtpUnits ftpUnits=new FileToFtpUnits(ip,port,userName,password);
        //连接FTP服务器
        ftpUnits.ftpLogin();
        //文件上传
        ftpUnits.uploadDirectory("D:/ftpTest","/");
        //文件删除
        ftpUnits.delAllFile("D:/ftpTest");
        //关闭FTP服务器
        ftpUnits.ftpLogOut();

        return ReturnT.SUCCESS;
    }

}
