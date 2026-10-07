package pl.neene.haft.embroideryStorage.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.time.Duration;

@Service
public class S3Service {
    private final String publicGalleryBucketName;
    private final String privateGalleryBucketName;
    private final String apiEndpoint;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Autowired
    public S3Service(
            @Value("${s3.accessKey}") String accessKey,
            @Value("${s3.secretKey}") String secretKey,
            @Value("${s3.galleryBucket.public}") String publicGalleryBucketName,
            @Value("${s3.galleryBucket.private}") String privateGalleryBucketName,
            @Value("${s3.api.endpoint}") String apiEndpoint,
            @Value("${s3.region}") String region
            ) {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);
        S3Configuration configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(apiEndpoint))
                .credentialsProvider(credentialsProvider)
                .region(Region.of(region))
                .serviceConfiguration(configuration)
                .build();

        S3Presigner s3Presigner = S3Presigner.builder()
                .endpointOverride(URI.create(apiEndpoint))
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(configuration)
                .region(Region.of(region))
                .build();

        this(publicGalleryBucketName, privateGalleryBucketName, apiEndpoint, s3Client, s3Presigner);
    }

    public S3Service(String publicGalleryBucketName, String privateGalleryBucketName, String apiEndpoint, S3Client s3Client, S3Presigner s3Presigner) {
          this.publicGalleryBucketName = publicGalleryBucketName;
          this.privateGalleryBucketName = privateGalleryBucketName;
          this.s3Client = s3Client;
          this.apiEndpoint = apiEndpoint;
          this.s3Presigner = s3Presigner;
    }

    public void store(byte[] content, String key, boolean isPublic){
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(getBucketFromIsPublic(isPublic))
                .key(key)
                .build();

        RequestBody requestBody = RequestBody.fromBytes(content);

        s3Client.putObject(request, requestBody);
    }

    public void deleteObject(String key, boolean isPublic){
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(getBucketFromIsPublic(isPublic))
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    public String generateUrl(String key, boolean isPublic){
        if(isPublic){
            return apiEndpoint + "/" + publicGalleryBucketName + "/" + key;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(privateGalleryBucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public void moveObject(String key, boolean isPublic){
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(getBucketFromIsPublic(isPublic))
                .sourceKey(key)
                .destinationBucket(getBucketFromIsPublic(!isPublic))
                .destinationKey(key)
                .build();

        s3Client.copyObject(copyObjectRequest);
        deleteObject(key, isPublic);
    }

    private String getBucketFromIsPublic(boolean isPublic){
        return isPublic ? publicGalleryBucketName : privateGalleryBucketName;
    }
}
