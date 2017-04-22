package com.revbingo.web

import com.revbingo.price.Price
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {

    @RequestMapping("/price")
    fun getPrice(@RequestParam(name="type") type: String): Price {
        return when(type) {
            "m3.large" -> "1.00"
            "m3.medium" -> "2.00"
            else -> "0.00"
        }
    }
}
