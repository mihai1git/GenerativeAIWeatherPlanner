package com.amazonaws.lambda.mihai.bedrockinvoker.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * layer between lambda logic and S3 filesystem; files are treated as objects
 * @author Mihai ADAM
 *
 */
public class S3Service {

    private AmazonS3 s3Client;
    
    private Logger logger = LogManager.getLogger(S3Service.class);
    	  
    public static S3Service build() {
    	AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
    	.withRegion(Regions.US_EAST_2)
		.build();
    	S3Service s3dao = new S3Service();
    	s3dao.setS3Client(s3Client);
    	
    	return s3dao;
    }

    public String getTemplate (String bucketName, String key) {
    	return s3Client.getObjectAsString(bucketName, key);
    }

	public AmazonS3 getS3Client() {
		return s3Client;
	}

	public void setS3Client(AmazonS3 s3Client) {
		this.s3Client = s3Client;
	}
    
    
}
