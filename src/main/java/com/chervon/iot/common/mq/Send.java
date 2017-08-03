package com.chervon.iot.common.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by ZAC on 2017-7-10.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@Component
public class Send implements RabbitTemplate.ConfirmCallback{
    private RabbitTemplate rabbitTemplate;

    /**
     * 构造方法注入
     * @param rabbitTemplate
     */
    @Autowired
    public Send(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setConfirmCallback(this);
    }

    private CorrelationData correlationData;
    private String msg;
    /**
     * 发送消息
     * @param msgType
     * @param jsonData
     */
    public void sendMsg(String msgType, String jsonData) {
//        MsgTable msgTable = new MsgTable();
//        msgTable.setMsgtype(msgType);
//        msgTable.setJson(jsonData);
//        byte[] msg = SerializationUtils.serialize(msgTable);

        correlationData = new CorrelationData(UUID.randomUUID().toString());
        msg = msgType + "'&10244201&'" + jsonData;
        rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTINGKEY_RABBIT,
                    msg,correlationData);
    }

    /**
     * 回调方法
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
//            System.out.println("消费成功！！");
        } else {
//            System.out.println("消息消费失败:" + cause);
        }
    }
}
