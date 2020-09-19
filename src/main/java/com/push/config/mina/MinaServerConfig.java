package com.push.config.mina;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author xhzy
 */
@Configuration
@Slf4j
public class MinaServerConfig {

    @Value("${mina.server.port}")
    private int port;

    @Value("${mina.server.buffer-size}")
    private int bufferSize;

    @Value("${mina.server.idle-time}")
    private int idleTime;

    @PostConstruct
    public void init(){
        IoAcceptor acceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        //add log filter
        chain.addLast("logging",new LoggingFilter());
        //add codec
        chain.addLast("codec",new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        acceptor.setHandler(new MinaServerHandler());

        IoSessionConfig sessionConfig = acceptor.getSessionConfig();
        sessionConfig.setReadBufferSize(bufferSize);
        sessionConfig.setBothIdleTime(idleTime);

        try {
            acceptor.bind(new InetSocketAddress(port));
            log.info("mina server startup");
        } catch (IOException e) {
            log.error("mina服务启动失败，error={}",e.getMessage());
        }
    }
}
