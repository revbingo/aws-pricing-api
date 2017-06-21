AWS Pricing API
====

A web API for making sense of the AWS EC2 Pricing "API" (in reality, a bunch of very large JSON files).  This was originally
a very small project for testing the AWS CodeStar service - hence it is based on Spring, has zero tests, and a bunch of scripts
that probably don't make sense outside the CodeStar ecosystem. It probably works, but might not. But it's here, and it's MIT licensed, 
so use as you see fit. 

To use the API, simply hit `/price` and pass the following query string parameters (optional unless otherwise stated):

* type (mandatory) - the instance type e.g. `t2.micro`
* region - the region in which the instance is situated. Default: `us-east-1`
* tenancy - whether this instance is a `shared`, `dedicated` or `host` tenancy. Default: `shared`
* os - the operating system of the instance. Default: `linux`
* software - the software installed on the instance. Default: `NA`
* license - the license type for the software, 'included', 'byo' or 'none'. Default: 'none'

If this is useful to you...
=====
Just a hint, but if you're looking at this code and thinking "this is just what I need", you should probably be aware
that I didn't go any further with it because as I was scouting for possible domain names I came across [this API](http://ec2price.info/) 
which is free, hosted and works in almost exactly the same way as this code. You might want to use that instead.