package com.example.coffeeorder.controller;

import com.example.coffeeorder.client.OrderServiceClient;
import com.example.coffeeorder.messageque.KafkaProducer;
import com.example.coffeeorder.service.CoffeeOrderService;
import com.example.coffeeorder.vo.CoffeeOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coffee-order")
public class CoffeeOrderRestController {

    @Autowired
    private CoffeeOrderService coffeeOrderService;

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private KafkaProducer kafkaProducer;

    @GetMapping("/")
    public String order() {
        return "coffee-order service :)";
    }

    @PostMapping("/coffeeOrder")
    public ResponseEntity<CoffeeOrderVO> coffeeOrder(@RequestBody CoffeeOrderVO vo) {
        //회원 유무 체크
        if (!orderServiceClient.getCoffeeMember(vo.getCustomerName())) {
            System.out.println(vo.getCustomerName() + " is not a member :(");
            return new ResponseEntity<CoffeeOrderVO>(vo, HttpStatus.BAD_REQUEST);
        }
        System.out.println(vo.getCustomerName() + " is a member :)");

        //커피 주문
        coffeeOrderService.coffeeOrder(vo);

        //카프카에게 토픽(coffee-status의 KafkaConsumer의 토픽과 같아야함) 전송
        kafkaProducer.send("ssohee-kafka-test", vo);

        return new ResponseEntity<CoffeeOrderVO>(vo, HttpStatus.OK);
    }
}
