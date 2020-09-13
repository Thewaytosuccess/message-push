package com.push.config.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;

/**
 * netty 启动
 * @author xhzy
 */
@Configuration
@Slf4j
public class NettyServerConfig {

    @Value("${netty.server.port}")
    private int port;

    @Value("${netty.message.content-length}")
    private int maxContentLength;

    private EventLoopGroup boss;

    private EventLoopGroup worker;

    private ChannelFuture future;

    @PostConstruct
    public void init() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        //set codec
                        //channel.pipeline().addLast(new HttpServerCodec());
                        pipeline.addLast("encoder",new StringEncoder());
                        pipeline.addLast("decoder",new StringDecoder());
                        //pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8192,
                        //        Delimiters.lineDelimiter()));
                        //set content length
                        pipeline.addLast(new HttpObjectAggregator(maxContentLength));
                        pipeline.addLast(new ChunkedWriteHandler());
                        //register handler
                        pipeline.addLast(new NettyServerHandler());
                    }
                });
        future = serverBootstrap.bind(port).syncUninterruptibly().addListener(future -> {
            if(future.isSuccess()){
                log.info("netty server startup");
            } else {
                log.error("绑定端口失败");
            }
        });
    }

    @PreDestroy
    public void destroy() throws Exception {
        if(Objects.nonNull(boss) && !boss.isShuttingDown() && !boss.isTerminated()){
            boss.shutdownGracefully().sync();
        }
        if(Objects.nonNull(worker) && !worker.isShuttingDown() && !worker.isTerminated()){
            worker.shutdownGracefully().sync();
        }
        if(Objects.nonNull(future) && future.isSuccess()){
            future.channel().closeFuture().sync();
        }
        log.info("netty server shutdown");
    }
}
