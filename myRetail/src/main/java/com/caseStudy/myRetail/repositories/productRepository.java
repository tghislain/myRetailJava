/**
 * 
 */
package com.caseStudy.myRetail.repositories;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.springframework.stereotype.Service;

import com.caseStudy.myRetail.myRetailConfiguration;
import com.caseStudy.myRetail.models.IproductRepository;
import com.caseStudy.myRetail.models.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;

/**
 * @author Ghislain
 * this is a product repository to access product data from a local database
 */
@Service
public class productRepository implements IproductRepository {
	myRetailConfiguration _configuration;
	MongoClient _client;
	MongoDatabase _db;

	String _connectionString;
	String _databaseName;
	String _collectionName;

	
	/**
	 * this contructor initializes the configuration, the database client and the
	 * @param configuration stores configuration information
	 */
	public productRepository(myRetailConfiguration configuration) {
		_connectionString = configuration.getMongoUrl();
		_databaseName = configuration.getDbName();
		_collectionName = configuration.getCollectionName();
		_configuration = configuration;
		_client = new MongoClient(_connectionString);
		_db = _client.getDatabase(_databaseName);
	}

	
	/**
	 * this method takes in a product ID and returns a product object
	 * @param id 
	 * @return results
	 */
	
	public String GetProduct(int id) {
		Document results = null;
		
		try {
			results = _db.getCollection(_collectionName).find(eq("productID", id))
					.projection(Projections.include("productID", "productName", "productPrice")).first();

		} catch (Exception ex) {
			ex.printStackTrace();
			return "A problem occured retrieving the product from the database";
		}
		if (results != null) {
			results.remove("_id");
			return results.toJson();
		}

		else
			return null;
	}

	
	/**
	 * this method takes in a product object and if it exists, the product is
	 * updated with given information
	 * if the product does not exist this method inserts the product in the
	 * @param updated 
	 * @return results
	 */
	
	public String updateProduct(Product updated)
    {
        String returnProduct = null;
        ObjectMapper mapper = new ObjectMapper();
        Document convertedProduct = new Document();
        Document priceDocument = new Document();
        
        priceDocument.append("price", updated.productPrice.price)
        			 .append("currency", updated.productPrice.currency);
        
        convertedProduct.append("productID", updated.productID)
        				.append("productName", updated.productName)
        				.append("productPrice", priceDocument);
		
        try
        {
            String returnedProduct = GetProduct(updated.productID);
            if (returnedProduct != null)
            {
                updated._id = mapper.readValue(returnedProduct, Product.class)._id;
            }
            _db.getCollection(_collectionName).replaceOne(eq("productID", updated.productID), convertedProduct, new UpdateOptions().upsert(true));
            returnProduct = GetProduct(updated.productID);
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
       
        return returnProduct;
    }	
}
