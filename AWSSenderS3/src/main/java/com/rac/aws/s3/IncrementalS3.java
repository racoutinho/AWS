package com.rac.aws.s3;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.log4j.Logger;

public class IncrementalS3 {

    private static final Logger log = Logger.getLogger(IncrementalS3.class);

    //Need to set   
    private static final String WEB_PROXY_URL = "<set proxy";
    private static final int WEB_PROXY_PORT = 8088;

    private static String locaFolder;
    private static String s3Folder;
    private static String bucketName;

    private static AmazonS3 s3;

    private void sendFiles() {
        File local = new File(locaFolder);
        for (File file : local.listFiles()) {
            log.info("File : " + file.getPath());
            log.info("Uploading a new object to S3 from a file");
            String destinyFolder = s3Folder + "/" + file.getName();
            log.info("Destination : " + destinyFolder);
            s3.putObject(new PutObjectRequest(bucketName, destinyFolder, file));
            log.info("Done!\n");
        }
    }

    public static void main(String[] args) throws IOException {

        try {
            locaFolder = args[0];
            s3Folder = args[1];
            bucketName = args[2];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            log.error("Usage java -jar AWSSenderS3 <bucket_name> <localFolder> <s3_remote_folder>");
            System.exit(0);
        }

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. "
                    + "Please make sure that your credentials file is at the correct "
                    + "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        ClientConfiguration conf = new ClientConfiguration();
        conf.setProtocol(Protocol.HTTP);
        conf.setProxyHost(WEB_PROXY_URL);
        conf.setProxyPort(WEB_PROXY_PORT);

        s3 = new AmazonS3Client(credentials, conf);

        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        s3.setRegion(usWest2);

        new IncrementalS3().sendFiles();
    }
}
