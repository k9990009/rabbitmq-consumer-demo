package com.k9.demo;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author k9
 * @since 2019-11-02
 */
@Component
public class MyConsumer {

    private Logger  logger = LoggerFactory.getLogger(MyConsumer.class);

    @Value("${myQueue.other}")
    private String other;

    //ackMode 指定手动确认  exclusive 独占队列，只会发到一个消费者
    @RabbitListener(queues = "${myQueue.name}",ackMode = "MANUAL",exclusive = true)
    @RabbitHandler
    //@Transactional(rollbackFor = Exception.class)
    public void cosumer (Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG)long tag) throws IOException {
        logger.info("这是消费者{}",1);
        //long tag = message.getMessageProperties().getDeliveryTag();
        try {
            logger.info("业务参数：{}",other);
            logger.info("消息内容：{}",new String (message.getBody()));
            logger.info("消息tag:{}",tag);
            logger.info("消息id:{}",message.getMessageProperties().getMessageId());
            long s = 1/(tag%3);
            //确认消息
            //channel.basicAck(tag,false);
        }catch (Exception e){
            logger.info("处理异常:",tag);
            //消息扔回队列    tag不同了
            //channel.basicNack(tag,false,true);
            //丢弃消息
            logger.info("消息同步失败:{}",new String (message.getBody()));
            //channel.basicNack(tag,false,false);
            //如果入库，做事务的回滚
            //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

    }

}
