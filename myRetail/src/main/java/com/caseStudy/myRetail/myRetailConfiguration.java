/**
 * 
 */
package com.caseStudy.myRetail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ghislain
 *
 */
@Configuration
@ConfigurationProperties
public class myRetailConfiguration {

	private String mongoUrl =  "localhost:27017";	 
	private String dbName  = "products";
	private String collectionName = "products";
	
	public String getMongoUrl() {
		return mongoUrl;
	}
	public String getDbName() {
		return dbName;
	}
	public String getCollectionName() {
		return collectionName;
	}
}
