package playwright.firstTest;

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
import javax.mail.*;
import javax.mail.internet.*;

import java.util.Base64;
import java.util.Properties;
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
        htmlReporter.config().setReportName("Test ME");

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Automation", "Elite");

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
                .setChannel("chrome"));
        context = browser.newContext();
        page = context.newPage();
        
        if (!Files.exists(Paths.get("./reports/screenshots"))) {
            Files.createDirectories(Paths.get("./reports/screenshots"));
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
                // Capture the screenshot when notification is displayed
                String screenshotPath = captureScreenshot();
                test.addScreenCaptureFromPath("screenshots/" + screenshotPath);
                throw new Exception("Login failed, notification displayed."); // Force failure

            }
            
            loginPage.fillVerificationCode(new String[]{"1", "2", "3", "4"});
            test.log(Status.PASS, "Verification code filled successfully.");            
            
            
        }catch (Exception e) {
            test.log(Status.FAIL, "Test failed: " + e.getMessage());
            String screenshotPath = captureScreenshot();
            test.addScreenCaptureFromPath("screenshots/" + screenshotPath);
            throw e;
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
            String screenshotPath = captureScreenshot();
            test.addScreenCaptureFromPath("screenshots/" + screenshotPath);
            throw e;
            }
    }

    @Test(dependsOnMethods = {"navigateToDashboard"})
    public void placeBid() {
        test = extent.createTest("Place Bid");

        IPOPage ipoPage = new IPOPage(page);
        
        try {
            ipoPage.placeBid("Target Company Name"); 
            test.log(Status.PASS, "Bid placed successfully.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Failed to place bid: " + e.getMessage());
            String screenshotPath = captureScreenshot();
            test.addScreenCaptureFromPath("screenshots/" + screenshotPath);
        }
    }
    
    public String captureScreenshot() {
    	 String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
    	    String filePath = "./reports/screenshots/" + fileName; 
    	    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));
    	    return fileName;
    }
    
	@AfterMethod																																																																																																						
	public void updateResults(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable());
        }
	}
	
	@AfterTest
	public void endReport() throws IOException {
		
		if (page != null) {
            page.close(); 
        }
        if (context != null) {
            context.close(); 
        }
        if (browser != null) {
            browser.close(); 
        }
        if (playwright != null) {
            playwright.close(); 
        }
        extent.flush(); 
    
        sendEmailReport();
	}
	
	private void sendEmailReport() throws IOException {
        // Email configurations
		String[] recipients = {
		        "sagar@elitetechnocrats.com",
		        "tester3.elitetechno@gmail.com"
		    };
        String from = "tester4.elitetechno@gmail.com";
        String host = "smtp.gmail.com"; 
        String username = "tester4.elitetechno@gmail.com"; 
        String password = "cmezcxcglnjpetlo"; 

        // Set properties
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465"); // Use 465 for SSL
        properties.put("mail.smtp.socketFactory.port", "465"); // Port for SSL
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // Use SSL socket factory
        properties.put("mail.smtp.socketFactory.fallback", "false");

        // Create a session
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(from));

            InternetAddress[] recipientAddresses = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                recipientAddresses[i] = new InternetAddress(recipients[i]);
            }
            
            message.addRecipients(Message.RecipientType.TO, recipientAddresses);

            // Set Subject: header field
            message.setSubject("Test Report");

            MimeMultipart multipart = new MimeMultipart();

            // Set message body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            String reportPath = "./reports/extent.html"; // Path to your report
            String content = "Please find the attached test report.<br/><a href=\"file://" + reportPath + "\">View Report</a>";
            
            String base64Image = encodeImageToBase64("./reports/screenshots/screenshot.png");
            if (!base64Image.isEmpty()) {
                content += "<br/><img src='data:image/png;base64," + base64Image + "' alt='Screenshot'>";
            }
            
            messageBodyPart.setContent(content, "text/html");
            multipart.addBodyPart(messageBodyPart);
            
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(reportPath); // Attach the report file
            multipart.addBodyPart(attachmentBodyPart);
            
            message.setContent(multipart);
            
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
        
	}
	private String encodeImageToBase64(String imagePath) {
	    try {
	        Path path = Paths.get(imagePath);
	        byte[] bytes = Files.readAllBytes(path);
	        return Base64.getEncoder().encodeToString(bytes);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "";
	    }
	    
	}
}
