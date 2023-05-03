package com.example.demo2022.example.mq;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaTest {

    private Producer producer;
    ProducerRecord<String, String> record = new ProducerRecord<>("CustomerCountry", "Precision Products","France");

    /**
     * 同步发送时，只要注意捕获异常即可。
     */
    public void test01(){
        try {
              producer.send(record).get();
            System.out.println(" 消息发送成功。");
        } catch (Throwable e) {
            System.out.println(" 消息发送失败！");
            System.out.println(e);
        }
    }

    /**
     * 异步发送时，则需要在回调方法里进行检查。这个地方是需要特别注意的，很多丢消息的原因就是，我们使用了异步发送，却没有在回调中检查发送结果。
     */
    public void test02(){
        producer.send(record, (metadata, exception) -> {
            if (metadata != null) {
                System.out.println(" 消息发送成功。");
            } else {
                System.out.println(" 消息发送失败！");
                System.out.println(exception);
            }
        });
    }
}
