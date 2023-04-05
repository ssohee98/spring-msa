package com.example.coffeeorder.messageque;

import com.example.coffeeorder.vo.CoffeeOrderVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public CoffeeOrderVO send(String kafkaTopic, CoffeeOrderVO vo) {
        ObjectMapper mapper = new ObjectMapper();

        String jsonString = "";

        //CoffeeOrderVO 객체를 json으로 변환
        try {
            jsonString = mapper.writeValueAsString(vo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //kafkaTemplate에 등록된 포트번호로 key-value 형태로 전송
        kafkaTemplate.send(kafkaTopic, jsonString);
        System.out.println("메시지 발행 : " + vo);

        return vo;
    }
}
