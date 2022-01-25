package vanda.com.FlieToFtp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FtpConfig {

    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Value("${ftp.server.ip}")
    private String ip;

    @Value("${ftp.server.port}")
    private int port;

    @Value("${ftp.server.username}")
    private String userName;

    @Value("${ftp.server.password}")
    private String password;


}
