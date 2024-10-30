package playwright.firstTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.ipo.pages.IPOLogin;
import com.ipo.pages.IPOPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
public class Reports {

	
	public ExtentSparkReporter htmlReporter;
    public ExtentReports extent;
    public ExtentTest test;
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
	
    @BeforeTest
    public void setReport() throws IOException {
        htmlReporter = new ExtentSparkReporter("./reports/extent.html");
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setDocumentTitle("Test Reports");
        htmlReporter.config().setReportName("IPO Automation Tests");

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Automation", "Elite");
        

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
                .setChannel("chrome"));
        
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Browser Version", browser.version());
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        
        context = browser.newContext(new Browser.NewContextOptions()
        		.setRecordVideoDir(Paths.get("./reports/videos"))
                        .setRecordVideoSize(1280, 720));
        
        page = context.newPage();
        page.setViewportSize(1920, 1080);
        
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                );

        if (!Files.exists(Paths.get("./reports/screenshots"))) {
            Files.createDirectories(Paths.get("./reports/screenshots"));
        }
        
        if (!Files.exists(Paths.get("./reports/videos"))) {
            Files.createDirectories(Paths.get("./reports/videos"));
        }
    }

    @Test
    public void doLogin() throws Exception {
        test = extent.createTest("IPO Login Test");

        IPOLogin loginPage = new IPOLogin(page);
        
        try {
            loginPage.navigateTo();
            test.log(Status.PASS, "Navigated to the login page successfully.");

            loginPage.enterUserId("m3903");
            test.log(Status.PASS, "User ID entered successfully.");

            loginPage.enterPassword("Nirav@789");
            test.log(Status.PASS, "Password entered successfully.");

            loginPage.login();
            test.log(Status.PASS, "Login button clicked.");

            page.waitForTimeout(2000);
            if (page.isVisible("#toast-container.toast-top-right.toast-container")) {
                captureAndLogScreenshot();
                throw new Exception("Login failed, notification displayed.");
            }

            loginPage.fillVerificationCode(new String[]{"1", "2", "3", "4"});
            test.log(Status.PASS, "Verification code filled successfully.");

        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed: " + e.getMessage());
            captureAndLogScreenshot();
        }
    }

    @Test(dependsOnMethods = {"doLogin"})
    public void navigateToDashboard() {
        test = extent.createTest("Navigate to IPO Dashboard");

        
        IPOPage ipoPage = new IPOPage(page);
        
        try {
            ipoPage.navigateToDashboard();
            test.log(Status.PASS, "Navigated to the IPO Dashboard successfully.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Failed to navigate to IPO Dashboard: " + e.getMessage());
            captureAndLogScreenshot();
        }
    }

    @Test(dependsOnMethods = {"navigateToDashboard"})
    public void placeBid() {
        test = extent.createTest("Place Bid");
        
        IPOPage ipoPage = new IPOPage(page);
        
        try {
            ipoPage.placeBid("Target Company Name");
            test.log(Status.PASS, "Bid placed successfully.");
            if (page.locator("h2:has-text('NO IPO TODAY !!')").isVisible()) {
                System.out.println("NO IPO TODAY !!");
                test.log(Status.INFO, "NO IPO TODAY !!");
            }
        } catch (Exception e) {
            test.log(Status.FAIL, "Failed to place bid: " + e.getMessage());
            captureAndLogScreenshot();
        }
    }

    private void captureAndLogScreenshot() {
        String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
        String filePath = "./reports/screenshots/" + fileName;

        // Capture screenshot
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));

        try {
            // Read the screenshot as a byte array
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            // Convert to Base64
            String base64String = Base64.getEncoder().encodeToString(fileContent);
            // Add screenshot to the report
            test.addScreenCaptureFromBase64String(base64String, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void updateResults(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable());
        }
    }

    @AfterTest
    public void tearDown() throws IOException {
    	if (context != null) {
    		context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("./reports/Tracesteps.zip"))); // Stop tracing here
    		context.close();
        }
    	if (page != null) page.close();
    	
       // if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        extent.flush();
        sendEmailReport();
    }

    private void sendEmailReport() throws IOException {
        String[] recipients = {
            "ashish.test.p@gmail.com",
            "tester3.elitetechno@gmail.com"
        };
        String from = "tester4.elitetechno@gmail.com";
        String host = "smtp.gmail.com";
        String username = "tester4.elitetechno@gmail.com";
        String password = "cmezcxcglnjpetlo";

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", recipients)));
            message.setSubject("Test Report");

           MimeMultipart multipart = new MimeMultipart("related");

            // Email body part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            String reportPath = "./reports/extent.html";

            // Build the HTML content
            StringBuilder content = new StringBuilder();
            content.append("<h3>Please find the attached test report.</h3>")
                   .append("<a href=\"file://").append(reportPath).append("\">View Report</a><br/>");
            
            // Get the most recent screenshot path
            String recentScreenshotPath = getMostRecentScreenshotPath();
            if (recentScreenshotPath != null) {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(Files.readAllBytes(Paths.get(recentScreenshotPath)), "image/png");
                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.setDataHandler(new DataHandler(dataSource));
                imagePart.setDisposition(MimeBodyPart.INLINE);
                imagePart.setHeader("Content-ID", "<screenshot>");

                content.append("<img src='cid:screenshot' style='max-width:100%; height:auto;'>");
                messageBodyPart.setContent(content.toString(), "text/html");
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(imagePart);
            } else {
                content.append("No screenshot available.");
                messageBodyPart.setContent(content.toString(), "text/html");
                multipart.addBodyPart(messageBodyPart);
            }

            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(reportPath);
            multipart.addBodyPart(attachmentBodyPart);
            
            String recentVideoPath = getMostRecentVideoPath();
            if (recentVideoPath != null) {
                MimeBodyPart videoBodyPart = new MimeBodyPart();
                videoBodyPart.attachFile(recentVideoPath);
                multipart.addBodyPart(videoBodyPart);
            }
            
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Sent message successfully....");

        } catch (MessagingException | IOException mex) {
            mex.printStackTrace();
        }
    }

    private String getMostRecentScreenshotPath() {
        String screenshotsDir = "./reports/screenshots";
        File dir = new File(screenshotsDir);
        if (dir.exists() && dir.isDirectory()) {
            Optional<File> recentFile = Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith(".png"))
                .max(Comparator.comparingLong(File::lastModified));
            return recentFile.map(File::getAbsolutePath).orElse(null);
        }
        return null;
    }
    
    private String getMostRecentVideoPath() {
        String videosDir = "./reports/videos";
        File dir = new File(videosDir);
        if (dir.exists() && dir.isDirectory()) {
            Optional<File> recentFile = Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile() && file.getName().endsWith(".webm")) // or .mp4 depending on the format
                .max(Comparator.comparingLong(File::lastModified));
            return recentFile.map(File::getAbsolutePath).orElse(null);
        }
        return null;
    }
}
