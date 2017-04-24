package com.revbingo.web

import com.revbingo.price.Attributes
import com.revbingo.price.PricingProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Component
@RestController
class ApiController @Autowired constructor(val pricingProvider: PricingProvider) {

    @RequestMapping("/price")
    fun getPrice(@RequestParam(name="type") type: String,
                 @RequestParam(name="region", defaultValue="us-east-1") region: String,
                 @RequestParam(name="tenancy", defaultValue = "Shared") tenancy: String,
                 @RequestParam(name="os", defaultValue = "Linux") os: String,
                 @RequestParam(name="software", defaultValue = "NA") software: String,
                 @RequestParam(name="license", defaultValue = "No License required") license: String): Any {
        val price = pricingProvider.priceFor(Attributes(region.toLongRegionName(), type, tenancy, os.adaptOS(), software, license))
        return price
    }
}
