package com.revbingo.pricing

import com.revbingo.price.PricingProvider

fun main(args: Array<String>) {
    val pricingProvider = PricingProvider("https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonEC2/current/index.json")
    pricingProvider.loadFile()

}
