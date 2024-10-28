package playwright.firstTest;

import java.nio.file.Paths;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class AutoLogin {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Playwright playwright = Playwright.create();
		Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
	           .setChannel("chrome"));
		
		BrowserContext context	= browser.newContext(new Browser.NewContextOptions()
				.setStorageStatePath(Paths.get("applogin.json"))
			//	.setRecordVideoDir(Paths.get("myvideos/")));
				.setViewportSize(1920, 911));
		
		Page page = context.newPage();
	    
		page.navigate("https://ipo.jainam.in/home");
		
		page.getByText("IPO Dashboard").nth(1).click();
	    page.getByText("Bid").nth(1).click();
	    page.navigate("https://ipo.jainam.in/ipolist");
		
	/*	context.close();
	    browser.close();
	    playwright.close();
	    */
	}
}
