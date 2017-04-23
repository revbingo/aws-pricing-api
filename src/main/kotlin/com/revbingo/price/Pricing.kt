package com.revbingo.price

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URL

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

@Component
class PricingProvider @Autowired constructor(@Value("\${ec2.offer.url}") val pricingFileUrl: String) {

    val HOURLY_OFFER = "JRTCKXETXF"
    val RATE_CODE = "6YS6EN2CT7"

    var pricingFile: PricingFile

    init {
        pricingFile = loadFile("https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonEC2/current/index.json")
    }

    fun loadFile(url: String, filter: (Product) -> Boolean = { it.productFamily == "Compute Instance" }):PricingFile {

        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        println("Loading pricing file")
        val file = mapper.readValue<PricingFile>(URL(url))
        println("**** Loaded!")
        file.products = file.products.values.filter(filter).associate { Pair(it.sku, it) }
        file.terms["OnDemand"] = file.terms["OnDemand"]!!.filter { it.key in file.products }
        file.terms["Reserved"] = file.terms["Reserved"]!!.filter { it.key in file.products }

        return file
    }

    fun priceFor(attr: Attributes): Price? {
        println("looking for ${attr}")
        val product = pricingFile!!.products.values.find { it.attributes == attr }

        return pricingFile!!.terms.get("OnDemand")?.get(product?.sku)?.get("${product?.sku}.$HOURLY_OFFER")?.priceDimensions?.get("${product?.sku}.$HOURLY_OFFER.$RATE_CODE")?.pricePerUnit?.get("USD")
    }
}

fun AvailabilityZone.toLongRegionName(): String = when(this) {
    "eu-west-1" -> "EU (Ireland)"
    "us-west-1" -> "US West (N. California)"
    "us-west-2" -> "US West (Oregon)"
    "ap-southeast-1" -> "Asia Pacific (Singapore)"
    "ap-southeast-2" -> "Asia Pacific (Sydney)"
    "us-east-1" -> "US East (N. Virginia)"
    else -> this
}

private fun OperatingSystem.adaptOS(): String = when(this.toLowerCase()) {
    "windows" -> "Windows"
    "linux/unix", "linux" -> "Linux"
    else -> "Other"
}


fun main(args: Array<String>) {
    val pricingProvider = PricingProvider("https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonEC2/current/index.json")
    pricingProvider.pricingFile.products.values.filter { it.attributes.instanceType == "m3.medium" }.forEach(::println)
    println(pricingProvider.priceFor(Attributes(location="us-east-1a".toLongRegionName(), instanceType="m3.medium", tenancy="Shared", operatingSystem="Linux", preInstalledSw="NA", licenseModel="No License required")))
}