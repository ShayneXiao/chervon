package com.chervon.iot.common.mq;

import com.chervon.iot.common.db2csv.DB2CSV;
import com.chervon.iot.common.mq.jdbcutils.JDBCUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZAC on 2017-7-10.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@Configuration
@ComponentScan(value = {"com.chervon.iot.mobile.util","com.chervon.iot.common.db2csv"})
public class AmqpConfig {
    public static final String EXCHANGE = "chervon";
    public static final String ROUTINGKEY_RABBIT = "rabbit";
    public static final String ROUTINGKEY_PUBSUB = "pubsub";
    public static final String QUEUE_NAME_RABBIT = "rabbit";
    public static final String QUEUE_NAME_PUBSUB = "pubsub";
    private Long receivedTime = 30L;        //两次接收消息的间隔
    private Integer doInsertNum = 5000;     //每次插入数据库条数
    private Integer doExportNum = 10000;    //每次导出数据库条数


    @Autowired
    private JDBCUtils jdbcUtils;
    @Autowired
    private DB2CSV db2CSV;

    @Bean
    public CachingConnectionFactory connectionFactory() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        String uri = System.getenv("AMQP_URL");
        if (uri == null) uri = "amqp://guest:guest@localhost";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(uri);

        CachingConnectionFactory cachingConnectionFactory =
                new CachingConnectionFactory(connectionFactory);

        //显示调用，进行消息的回调
        cachingConnectionFactory.setPublisherConfirms(false);
        return cachingConnectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)     //必须是prototype类型
    public RabbitTemplate rabbitTemplate() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        return rabbitTemplate;
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     *
     *
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(EXCHANGE,true,false);
    }

    @Bean
    public Queue queueRabbit() {
        return new Queue(QUEUE_NAME_RABBIT, true); //队列持久
    }

    @Bean
    public Queue queuePubSub() {
        Map<String,Object> args = new HashMap();
        args.put("x-message-ttl", 0);
        return new Queue(QUEUE_NAME_PUBSUB, true); //队列持久
    }

    @Bean
    public Binding bindingChervon() {
        return BindingBuilder.bind(queueRabbit()).to(defaultExchange()).with(AmqpConfig.ROUTINGKEY_RABBIT);
    }

    @Bean
    public Binding bindingHeroku() {
        return BindingBuilder.bind(queuePubSub()).to(defaultExchange()).with(AmqpConfig.ROUTINGKEY_PUBSUB);
    }

    private static int i = 0;
    private static Integer count = 0;
    private static Long before = System.currentTimeMillis();
    private static Long current = null;
    private static List<String> sqls = new ArrayList<>();
    @Bean
    public SimpleMessageListenerContainer messageContainer() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(queueRabbit());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认
        container.setTxSize(1);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                String msg = new String(message.getBody(), "UTF-8");

//                System.out.println("Received Message:" + msg);

                String[] strs = msg.split("'&10244201&'");
                if (strs != null && strs.length == 2) {
                    String sql = "INSERT INTO msgtable(finalstatus,msgtype,json)" +
                            " VALUES('null', " + "'" + strs[0] + "'" + ", " + "'" +
                            strs[1] + "'" + ");";
                    sqls.add(sql);
                    i++;
                }

                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

                //1、获取当前时间，减去第一次执行时的时间，并转换成分钟数
                current = System.currentTimeMillis();
                Long time = ((current - before) / 1000 / 60);
                //2、判断：如果当前时间与第一次执行时间>30分钟，则将msgTables中的数据插入数据库，不管是否
                //   达到了200条
                if (time >= receivedTime) {
                    try {
                        Long beforeInsert = System.currentTimeMillis();
                        System.out.println("-----开始插入-----" + beforeInsert);
                        jdbcUtils.insertBatch(sqls);
                        Long afterInsert = System.currentTimeMillis();
                        System.out.println("--插入"+ sqls.size() +"条成功--" + afterInsert +
                                "__" + (afterInsert-beforeInsert));

                        i = 0;
                        before = System.currentTimeMillis();

                        //每执行一次sql，count++
                        count += sqls.size();
                        if (doExportNum < count){
                            try {
                                db2CSV.startTableToCSV();
                                count = 0;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("插入"+ sqls.size() +"条失败-----------" + System.currentTimeMillis());
                        e.printStackTrace();
                        throw e;
                    } finally {
                        sqls.clear();
                    }
                }

                if (i >= doInsertNum) {
                    try {
                        Long beforeInsert = System.currentTimeMillis();
                        System.out.println("-----开始插入-----" + beforeInsert);
                        jdbcUtils.insertBatch(sqls);
                        Long afterInsert = System.currentTimeMillis();
                        System.out.println("--插入"+ sqls.size() +"条成功--" + afterInsert +
                                "__" + (afterInsert-beforeInsert));

                        //每执行一次sql，count+
                        count += sqls.size();
                        i = 0;
                        before = System.currentTimeMillis();

                        if (count >= doExportNum){
                            try {
                                Long beforeCsvBulk = System.currentTimeMillis();
                                System.out.println("---------调用Csv&Bulk Api执行开始----"+beforeCsvBulk);
                                db2CSV.startTableToCSV();
                                Long afterCsvBulk = System.currentTimeMillis();
                                System.out.println("---------调用Csv&Bulk Api执行成功----"+afterCsvBulk +
                                    "__"+(afterCsvBulk - beforeCsvBulk));
                                count = 0;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("--插入"+ sqls.size() +"条失败--" + System.currentTimeMillis());
                        e.printStackTrace();
                        throw e;
                    } finally {
                        sqls.clear();
                    }
                }
            }
        });
        return container;
    }
}
