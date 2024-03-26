package com.game.log.engine.product.used;

import com.game.log.engine.consumer.used.ConsumerHandlerMessage;
import com.game.log.engine.utils.IdHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟生产者生产消息
 * @author bk
 */
@RestController
@RequestMapping("/api")
public class ProductApi {


    @Autowired
    private IProductService productService;

    @Autowired
    private ConsumerHandlerMessage handlerMessage;

    @GetMapping("/send")
    public void sendMsg() {
        productService.send();
    }

    @GetMapping("/pull")
    public void pullMsg() {
        handlerMessage.run();
    }
}
