package dev.thiagooliveira.tablesplit.infrastructure.imagestorage.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import java.util.Map;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("integration-test")
public class MockCloudinaryConfig {

  @Bean(name = "mockCloudinary")
  @Primary
  public Cloudinary mockCloudinary() throws Exception {
    Cloudinary cloudinary = Mockito.mock(Cloudinary.class);
    Uploader uploader = Mockito.mock(Uploader.class);

    when(cloudinary.uploader()).thenReturn(uploader);

    when(uploader.upload(any(), anyMap()))
        .thenReturn(Map.of("secure_url", "http://mock-cloudinary.com/mock-image.jpg"));

    when(uploader.destroy(anyString(), anyMap())).thenReturn(Map.of("result", "ok"));

    return cloudinary;
  }
}
