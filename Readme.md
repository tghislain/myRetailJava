MyRetail Case study

Prerequisites
Any Java capacle IDE
Spring Boot 2.0.2.RELEASE
Maven
Mongo DB

Preparation. 
Start the mongo service
Create a database with the name "products"
create a collection with the name "products"

if you wish to change the name on the database and collection you can do so but make sure you update the it in myRetailConfiguration.java

default mongo url is "mongodb://localhost:27017"
if you have a different url make sure to update it in myRetailConfiguration.java
Running the tests
The product API listens at : http://localhost:8080(default tomcat port)/products responds to GET and PUT verbs

import and run the application.

EX: GET: http://localhost:8080/products/
	-->returns: "Welcome to the myRetail case study!"

    GET: http://localhost:8080/products/16696652
	-->returns: { "productID" : 16696652, "productName" : "Beats Solo 2 Wireless - Black", "productPrice" : { "price" : "279.99", "currency" : "USD" } }

    PUT: http://localhost:55971/product/16696652
	 with the same product id and a full body of the product	 
	 modify the price at : "product>price>listPrice>price" in the JSON object
    	-->returns: { "productID" : 16696652, "productName" : "Beats Solo 2 Wireless - Black", "productPrice" : { "price" : "888.99", "currency" : "USD" } }

Note: if a put request is received and the product is not in the local database the product will be created with the given information
      given that it has a name a price and an id

Author Ghislain Twagirayezu
email: twagghi@gmail.com