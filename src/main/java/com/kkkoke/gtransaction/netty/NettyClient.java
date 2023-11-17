package com.kkkoke.gtransaction.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author KeyCheung
 * @date 2023/10/27
 * @desc
 */
@Slf4j
@Component
public class NettyClient implements InitializingBean {

    public NettyClientHandler client = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        start("localhost", 8080);
    }

    public void start(String hostname, Integer port) {
        log.info("netty client 开始建立连接。。。");

        client = new NettyClientHandler();

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("logger", new LoggingHandler(LogLevel.DEBUG));
                        pipeline.addLast("encoder", new StringEncoder());
                        pipeline.addLast("handler", client);
                    }
                });

        try {
            bootstrap.connect(hostname, port).sync();
        } catch (InterruptedException e) {
            log.error("netty 连接出现异常， {}", e.getLocalizedMessage());
        }
    }

    public void send(JSONObject jsonObject) {
        try {
            client.call(jsonObject);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
