package com.revbingo.price

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

//fun main(args: Array<String>) {
//    println("?")
//    readLine()
//    val pricingFile = loadFile( {
//        it.productFamily == "Compute Instance" && it.attributes.preInstalledSw == "NA" && it.attributes.licenseModel != "Bring your own license"
//    })
//
//    val instance = Instance().apply {
//        instanceType = "m3.medium"
//        placement = Placement().apply {
//            availabilityZone = "eu-west-1a"
//            tenancy = "Shared"
//        }
//        platform = "Linux/UNIX"
//    }
//
//    println(instance)
//
//    println(pricingFile.priceFor(instance))
//
//    while(true) {
//        sleep(100)
//    }
//}

val HOURLY_OFFER = "JRTCKXETXF"
val RATE_CODE = "6YS6EN2CT7"

fun loadFile(filter: (Product) -> Boolean = { it.productFamily == "Compute Instance" }):PricingFile {

    val mapper = jacksonObjectMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val pricingFile = mapper.readValue<PricingFile>(File("/Users/mark.piper/Downloads/ec2pricing.json"))

    pricingFile.products = pricingFile.products.values.filter(filter).associate { Pair(it.sku, it) }
    pricingFile.terms["OnDemand"] = pricingFile.terms["OnDemand"]!!.filter { it.key in pricingFile.products }
    pricingFile.terms["Reserved"] = pricingFile.terms["Reserved"]!!.filter { it.key in pricingFile.products }

    return pricingFile
}

fun PricingFile.priceFor(attr: Attributes): Price? {
    val product = this.products.values.find { it.attributes == attr }

    return this.terms.get("OnDemand")?.get(product?.sku)?.get("${product?.sku}.$HOURLY_OFFER")?.priceDimensions?.get("${product?.sku}.$HOURLY_OFFER.$RATE_CODE")?.pricePerUnit?.get("USD")
}

typealias InstancePrices = Map<String, Term>
typealias Term = Map<String, Offer>
typealias Currency = String
typealias Price = String

data class PricingFile(var products: Map<String, Product>, val terms: MutableMap<String, InstancePrices>)
data class Product(val sku: String, val productFamily: String, val attributes: Attributes, var onDemandPrice: String? = null)

data class Attributes(val location: String?, val instanceType: String?, val tenancy: String?, val operatingSystem: String?, val preInstalledSw: String? = null, val licenseModel: String? = null)
data class Offer(val effectiveDate: String, val priceDimensions: Map<String, PriceDimension>, val termAttributes: TermAttributes)
data class PriceDimension(val description: String, val pricePerUnit: Map<Currency, Price>)

data class TermAttributes(val leaseContractLength: String?, val offeringClass: String?, val purchaseOption: String?)

data class InstancePrice(val hourly: PriceDimension, val upfront: PriceDimension = PriceDimension("No upfront", mapOf("USD" to "0.00")))
//
//fun Instance.toAttributes() = Attributes(this.placement.availabilityZone.toLongRegionName(), this.instanceType, this.placement.tenancy, this.platform.adaptOS(), "NA", if(this.platform.adaptOS() == "Windows") "License Included" else "No License required")

typealias AvailabilityZone = String
typealias OperatingSystem = String

private fun AvailabilityZone.toLongRegionName(): String = when(this.dropLast(1)) {
    "eu-west-1" -> "EU (Ireland)"
    "us-west-1" -> "US West (N. California)"
    "us-west-2" -> "US West (Oregon)"
    "ap-southeast-1" -> "Asia Pacific (Singapore)"
    "ap-southeast-2" -> "Asia Pacific (Sydney)"
    else -> this.dropLast(1)
}

private fun OperatingSystem.adaptOS(): String = when(this) {
    "windows" -> "Windows"
    "Linux/UNIX" -> "Linux"
    else -> "Other"
}