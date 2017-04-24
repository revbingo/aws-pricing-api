package com.revbingo.price

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.net.URL
import java.util.logging.Logger

typealias InstancePrices = Map<String, Term>
typealias Term = Map<String, Offer>
typealias Currency = String
typealias Price = String
typealias AvailabilityZone = String
typealias OperatingSystem = String

data class PricingFile(var products: Map<String, Product>, val terms: MutableMap<String, InstancePrices>)
data class Product(val sku: String, val productFamily: String, val attributes: Attributes, var onDemandPrice: String? = null)
data class Attributes(val location: String?, val instanceType: String?, val tenancy: String?, val operatingSystem: String?, val preInstalledSw: String? = null, val licenseModel: String? = null)
data class Offer(val effectiveDate: String, val priceDimensions: Map<String, PriceDimension>, val termAttributes: TermAttributes)
data class PriceDimension(val description: String, val pricePerUnit: Map<Currency, Price>)
data class TermAttributes(val leaseContractLength: String?, val offeringClass: String?, val purchaseOption: String?)

data class ErrorResponse(val errorMessage: String)
data class PriceResponse(val price: BigDecimal, val currency: String = "USD", val sku: String, val attributes: Attributes)

val transactionLogger = Logger.getLogger("transactions")
val logger = Logger.getLogger("com.revbingo.web")

@Component
class PricingProvider @Autowired constructor(@Value("\${ec2.offer.url}") val pricingFileUrl: String) {

    val HOURLY_OFFER = "JRTCKXETXF"
    val RATE_CODE = "6YS6EN2CT7"

    val products: MutableMap<String, Product> = mutableMapOf()
    val onDemandTerms: MutableMap<String, Term> = mutableMapOf()
    val reservedTerms: MutableMap<String, Term> = mutableMapOf()

    init {
        val pricingFile = loadFile()

        pricingFile.products.values.filter { it.productFamily == "Compute Instance" }.associateTo(products) { Pair(it.sku, it) }
        pricingFile.terms["OnDemand"]!!.filterTo(onDemandTerms) { it.key in products }
        pricingFile.terms["Reserved"]!!.filterTo(reservedTerms) { it.key in products }

        logger.info("Filtered file to ${products.count()} products and ${onDemandTerms.count() + reservedTerms.count()} terms")
    }

    fun loadFile() : PricingFile {
        logger.info("Loading pricing file")

        val mapper = jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        val file = mapper.readValue<PricingFile>(URL(pricingFileUrl))

        logger.info("Loaded file with ${file.products.count()} products and ${file.terms["OnDemand"]!!.count() + file.terms["Reserved"]!!.count()} terms")

        return file
    }

    fun priceFor(attr: Attributes): Any {
        transactionLogger.info(attr.toString())

        val product = products.values.find { it.attributes == attr }
        product ?: return ErrorResponse("No product found for ${attr}")

        val usdCost = onDemandTerms[product.sku]?.get("${product.sku}.$HOURLY_OFFER")?.priceDimensions?.get("${product.sku}.$HOURLY_OFFER.$RATE_CODE")?.pricePerUnit?.get("USD")
        usdCost ?: return ErrorResponse("No cost found for sku ${product.sku}")

        return PriceResponse(BigDecimal(usdCost), "USD", product.sku, product.attributes)
    }
}