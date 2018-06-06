package com.caseStudy.myRetail.controllers;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.BsonObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.caseStudy.myRetail.myRetailConfiguration;
import com.caseStudy.myRetail.models.Product;
import com.caseStudy.myRetail.models.ProductPrice;
import com.caseStudy.myRetail.repositories.productRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author Ghislain
 *
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    RestTemplate client = new RestTemplate(requestFactory);
  
	productRepository _repository;
	myRetailConfiguration _configuration;
	
	/**
	 * this constructor takes a configuration and a product repository and injects
	 * them into the controller to be used by the application
	 * @param configuration stores the application configuration information
	 * @param repository is the respository used to retrieve price information
	 */
	@Autowired
	public ProductController(myRetailConfiguration configuration, productRepository repository) {
		_configuration = configuration;
		_repository = repository;
	}


	/**
	 * This method takes inteder product id and builds the URL used to get product
	 * @param id  product id
	 * @return built url to fetc a particular product
	 */
	private String BuildProductURL(int id) {
		return "http://redsky.target.com/v2/pdp/tcin/" + String.valueOf(id);// +
		 //"?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics";
	}


	/**
	 * this method takes the built external url and fetches product information
	 * @param path external url used to fetch product information
	 * @return resulting information from the call to get product information
	 */
	private String GetExternalProduct(String path) {
		try { 			 
			 ResponseEntity<String> response = client.getForEntity(path, String.class);
			 if(response != null && response.getStatusCodeValue() == HttpStatus.OK.value()) {
			 	 return response.getBody().toString();
			 }
		} catch (Exception ex) {
			ex.printStackTrace();
			return "An error occured getting product information from external API";
		}
		return null;
	}
	
	@RequestMapping("/")
	public String home() {
		return "Welcome to the myRetail case study!";
	}
	
	/**
	 * his controller action takes a product id and looks in the local database if
	 * we already have the product and returns it's information
	 * if the product is not in the local database it goes out to the external API
	 * and fetches the product information,
	 * stores the information in a local database and returns the stored product
	 * @param id product id
	 * @return product information
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String  getProduct(@PathVariable(value="id")int id) {            
        int inId = (int)id;
        String product;

        product = _repository.GetProduct(inId);

        if (product != null)
        {
            return product;
        }
        else
        {
            try
            {
                String url = BuildProductURL(inId);
                String jsonProduct = null;
                jsonProduct = GetExternalProduct(url);
                ObjectMapper mapper = new ObjectMapper();
                if (jsonProduct != null)
                {
                    
                    JsonNode root = mapper.readTree(jsonProduct);
                    String productPrice = root.path("product").path("price").path("listPrice").path("price").asText();
                    String productName = root.path("product").path("item").path("product_description").path("title").asText();
                    int productID = root.path("product").path("available_to_promise_network").path("product_id").asInt();
                    
                    Product inProduct = new Product();
                    ProductPrice inPrice = new ProductPrice();
                    inProduct.productID = productID;
                    inProduct.productName = productName;

                    inPrice.price = productPrice;
                    inPrice.currency = "USD";
                    inProduct.productPrice = inPrice;
                    inProduct._id = new BsonObjectId().getValue();
                    product = _repository.updateProduct(inProduct);
                    
                    return product;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            
            return "no product found for product id:" +  String.valueOf(id);
        }
        
    }

	/**
	 * this controller takes a product id and a product json object and updates the
	 * stored product information
	 * @param id product ID
	 * @param value product JSON object
	 * @return product information
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String putProduct(String id, @RequestBody String value)
    {

        Product product = new Product();
        ProductPrice price = new ProductPrice();
        ObjectMapper mapper = new ObjectMapper();
        int productID = 0;
        try
        {
            JsonNode root = mapper.readTree(value);
            String productPrice = root.path("product").path("price").path("listPrice").path("price").asText();
            String productName = root.path("product").path("item").path("product_description").path("title").asText();
            productID = root.path("product").path("available_to_promise_network").path("product_id").asInt();
            
            product.productID = productID;
            product.productName = productName;

            price.price = productPrice;
            price.currency = "USD";
            
            product.productPrice = price;
            
            if((productPrice.equals("")) || (productName.equals("")) || (productID  == 0) ) {
            	return "The product entered was invalid";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return _repository.updateProduct(product);
    }

}