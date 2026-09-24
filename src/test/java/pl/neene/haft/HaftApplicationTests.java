package pl.neene.haft;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.neene.haft.config.AppProfile;

@SpringBootTest(classes = HaftApplicationTests.class)
@ActiveProfiles(AppProfile.TEST)
class HaftApplicationTests {

	@Test
	void contextLoads() {
	}

}
