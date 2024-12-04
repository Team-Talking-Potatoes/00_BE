package potatoes.server.utils.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import potatoes.server.error.exception.InvalidFileFormat;
import potatoes.server.error.exception.S3FileUploadFailed;

@RequiredArgsConstructor
@Component
public class S3UtilsProvider {
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3Client amazonS3;

	public List<String> uploadFiles(List<MultipartFile> multipartFiles) {
		return new FileUploader().uploadMultipartFiles(multipartFiles);
	}

	public String uploadFile(MultipartFile multipartFile) {
		return new FileUploader().uploadMultipartFile(multipartFile);
	}

	public String getFileUrl(String fileName) {
		return new FileUrlGenerator().generate(fileName);
	}

	private class FileUploader {
		List<String> uploadMultipartFiles(List<MultipartFile> multipartFiles) {
			List<String> fileNameList = new ArrayList<>();
			multipartFiles.forEach(file -> fileNameList.add(uploadMultipartFile(file)));
			return fileNameList;
		}

		String uploadMultipartFile(MultipartFile file) {
			String fileName = new FileNameGenerator().generate(file.getOriginalFilename());
			ObjectMetadata metadata = new MetadataGenerator().generate(file);

			try (InputStream inputStream = file.getInputStream()) {
				PutObjectRequest request = RequestBuilder.builder()
					.bucket(bucket)
					.fileName(fileName)
					.inputStream(inputStream)
					.metadata(metadata)
					.build()
					.createRequest();

				amazonS3.putObject(request);
			} catch (IOException e) {
				throw new S3FileUploadFailed();
			}

			return fileName;
		}
	}

	private static class FileNameGenerator {
		String generate(String originalFileName) {
			return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
		}

		private String getFileExtension(String fileName) {
			try {
				return fileName.substring(fileName.lastIndexOf("."));
			} catch (StringIndexOutOfBoundsException e) {
				throw new InvalidFileFormat();
			}
		}
	}

	private static class MetadataGenerator {
		ObjectMetadata generate(MultipartFile file) {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			metadata.setContentType(file.getContentType());
			return metadata;
		}
	}

	@Builder
	private static class RequestBuilder {
		private String bucket;
		private String fileName;
		private InputStream inputStream;
		private ObjectMetadata metadata;

		public PutObjectRequest createRequest() {
			return new PutObjectRequest(bucket, fileName, inputStream, metadata);
		}
	}

	private class FileUrlGenerator {
		String generate(String fileName) {
			return amazonS3.getUrl(bucket, fileName).toString();
		}
	}
}
