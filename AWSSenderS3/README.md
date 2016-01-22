AWS Sender to S3

This simple java application send all files from a specific local folder to a remote AWS S3 bucket.

1) Set your credentials to your ~/.aws/credentials file and type:

java -jar AWSSenderS3.jar <bucket_name> <localFolder> <s3_remote_folder>
