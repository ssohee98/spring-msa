package com.example.coffeestatus.messageque;

import com.example.coffeestatus.mapper.CoffeeMapper;
import com.example.coffeestatus.vo.CoffeeStatusVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaConsumer {

    @Autowired
    CoffeeMapper coffeeMapper;

    //정해둔 topics로 이벤트 발생시
    @KafkaListener(topics = "ssohee-kafka-test")
    public void prodessMessage(String kafkaMessage) {
        System.out.println("Kafka Message: => " + kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        CoffeeStatusVO vo = new CoffeeStatusVO();
        vo.setOrderNumber((String) map.get("orderNumber"));
        vo.setCoffeeName((String) map.get("coffeeName"));
        vo.setCoffeeCount((Integer) map.get("coffeeCount"));
        vo.setCustomerName((String) map.get("customerName"));

        //DB에 최종적으로 저장
        coffeeMapper.insertCoffeeOrderStatus(vo);
    }
}
