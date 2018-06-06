package com.caseStudy.myRetail.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

/**
 * this is a object representation of the product
 * @author Ghislain
 *
 */
public class Product {
	@BsonId
	public ObjectId _id;
	
	public int productID;
	
	public String productName;
	
	public ProductPrice productPrice;
	
}
