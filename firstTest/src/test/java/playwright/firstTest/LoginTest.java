package playwright.firstTest;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.ipo.pages.IPOLogin;
import com.ipo.pages.IPOPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.testng.Assert;


public class LoginTest {
    public static void main(String[] args) throws Exception {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
                    .setChannel("chrome"));

            BrowserContext context = browser.newContext();
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true));

            Page page = context.newPage();
            IPOLogin loginPage = new IPOLogin(page);
            
            loginPage.navigateTo();
            loginPage.enterUserId("m3903");
            loginPage.enterPassword("Nirav@789");
            loginPage.login();
            loginPage.fillVerificationCode(new String[]{"1", "2", "3", "4"});
            
            
            IPOPage ipoPage = new IPOPage(page);

            ipoPage.navigateToDashboard();
            takeScreenshot(page, "after-login-screenshot.png");

            ipoPage.placeBid("YOUR_TARGET_COMPANY_NAME");

            takeScreenshot(page, "after-bid-screenshot.png");

            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("loginsteps.zip")));

            context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("applogin.json")));
        }
     }
    
    private static void takeScreenshot(Page page, String fileName) throws Exception {
        byte[] screenshot = page.screenshot(); 
        Files.write(Paths.get(fileName), screenshot); 

        String baselinePath = "baseline-" + fileName;

        
        if (!Files.exists(Paths.get(baselinePath))) {
            System.out.println("Baseline image does not exist: " + baselinePath);
            
            return; 
        }

        BufferedImage baselineImage = ImageIO.read(Paths.get(baselinePath).toFile());
        BufferedImage newImage = ImageIO.read(Paths.get(fileName).toFile());

        // Compare images
        boolean isIdentical = compareImages(newImage, baselineImage);
        Assert.assertTrue(isIdentical, "The screenshots do not match for " + fileName);
    }

    // Optional: Method to create a baseline image
    private static void takeBaselineScreenshot(Page page, String baselineFileName) throws Exception {
        byte[] screenshot = page.screenshot(); // Get the screenshot as a byte array
        Files.write(Paths.get(baselineFileName), screenshot); // Save as baseline
    }

    private static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }
        for (int y = 0; y < imgA.getHeight(); y++) {
            for (int x = 0; x < imgA.getWidth(); x++) {
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}