package cn.keking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableScheduling
@ComponentScan(value = "cn.keking.*")
public class FilePreviewApplication {


    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(FilePreviewApplication.class, args);
        ConfigurableEnvironment env = application.getEnvironment();
        String serverInfo = String.format("\n----------------------------------------------------------\n\t" +
                        "应用 '%s' 启动成功! 访问连接:\n\t" +
                        "项目地址: \t\thttp://%s:%s\n\t" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
        System.out.println(serverInfo);
    }
}
