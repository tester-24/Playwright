package playwright.firstTest;

import java.nio.file.Paths;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

public class MyApp {
	 public static void main(String[] args) {
		try( Playwright playwright = Playwright.create()){
           Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
           .setChannel("chrome"));
           
           BrowserContext context = browser.newContext();
           
           context.tracing().start(new Tracing.StartOptions()
        		   .setScreenshots(true)
        		   .setSnapshots(true));
           
         //  BrowserContext context = browser.newContext();
          Page page = context.newPage();
	      page.navigate("https://ipo.jainam.in/");
	     
	      System.out.println(page.title());
	
	       page.getByText("Apply for an IPO").click();
	       page.getByPlaceholder("Enter User ID").click();
	       page.getByPlaceholder("Enter User ID").click();
	       page.getByPlaceholder("Enter User ID").fill("m3903");
	       page.getByPlaceholder("Enter Password").click();
	       page.getByPlaceholder("Enter Password").fill("Nirav@789");
	       page.getByText("Login Now").click();
	       page.getByPlaceholder("-").first().click();
	       page.getByPlaceholder("-").first().fill("1");
	       page.getByPlaceholder("-").nth(1).fill("2");
	       page.getByPlaceholder("-").nth(2).fill("3");
	       page.getByPlaceholder("-").nth(3).fill("4");
	       page.getByText("Continue").click();

	       context.tracing().stop(new Tracing.StopOptions()
	    		   .setPath(Paths.get("trace.zip")));
	       
	       context.close();
	       browser.close();
	       playwright.close();
	        
	        }
	    }
}