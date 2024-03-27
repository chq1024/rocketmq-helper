package com.game.log.engine.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟生产者生产消息
 * @author bk
 */
@RestController
@RequestMapping("/api")
public class ProductApi {


    @Autowired
    private IProductService productService;

    @GetMapping("/send")
    public void sendMsg() {
        productService.send();
    }
}
