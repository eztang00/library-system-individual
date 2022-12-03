package cosc310_T28_librarySystem;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class GoogleStaticMapTest {

    @Test
    void test() throws IOException {
      BufferedImage i = GoogleStaticMapAPILink.getMap("");
      assert(i.getWidth() >= 5 && i.getHeight() >= 5 && i.getWidth() < 1000000 && i.getHeight() < 1000000);

    }

}
