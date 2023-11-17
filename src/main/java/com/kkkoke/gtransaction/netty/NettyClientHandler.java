package com.kkkoke.gtransaction.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kkkoke.gtransaction.transactional.BatchTransaction;
import com.kkkoke.gtransaction.transactional.GlobalTransactionManager;
import com.kkkoke.gtransaction.transactional.TransactionStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author KeyCheung
 * @date 2023/10/27
 * @desc
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("接受数据: {}", msg.toString());
        JSONObject jsonObject = JSON.parseObject((String) msg);

        String groupId = jsonObject.getString("groupId");
        String command = jsonObject.getString("command");

        log.info("接受command: {}", command);

        BatchTransaction batchTransaction = GlobalTransactionManager.getBatchTransaction(groupId);
        if (Objects.equals(command, "rollback")) {
            batchTransaction.setTransactionStatus(TransactionStatus.rollback);
        } else if (Objects.equals(command, "commit")) {
            batchTransaction.setTransactionStatus(TransactionStatus.commit);
        }
        synchronized (batchTransaction.getLock()) {
            batchTransaction.getLock().notify();
        }
    }

    public synchronized Object call(JSONObject data) throws Exception {
        context.writeAndFlush(data.toJSONString());
        return null;
    }
}
