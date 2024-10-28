package playwright.firstTest;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

public class Login {

	public static void main(String[] args) {

		try( Playwright playwright = Playwright.create()){
	           Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
	           .setChannel("chrome"));
	           
	           BrowserContext context = browser.newContext();
	           
	           context.tracing().start(new Tracing.StartOptions()
	        		   .setScreenshots(true)
	        		   .setSnapshots(true));
	           
	         // BrowserContext context = browser.newContext();
	          Page page = context.newPage();
		      page.navigate("https://ipo.jainam.in/");
		      
		     // System.out.println(page.title());
		  	
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

		       page.getByText("IPO Dashboard").nth(1).click();
			   page.getByText("Bid").nth(1).click();
			   page.navigate("https://ipo.jainam.in/ipolist");
			   
	          
			   //String targetCompanyName = "YOUR_TARGET_COMPANY_NAME";
			   
			   page.waitForSelector("section.bid-section-ipolist kendo-grid .k-table-tbody");

	            // Locate all rows in the IPO window table
	            List<Locator> rows = page.locator("section.bid-section-ipolist kendo-grid .k-table-tbody tr").all();

	            boolean bidClicked = false; // Flag to indicate if a bid has been clicked

	            // Loop through each row and check for "( SME )"
	            for (Locator row : rows) {
	                String companyName = row.locator("td:nth-child(2) div label span").textContent().trim();
	                String smeTag = row.locator("td:nth-child(2) span.bid-right").textContent().trim();

	                System.out.println("Checking company: " + companyName); // Debugging line

	                // Check if the company has the SME tag
	                boolean hasSmeTag = smeTag.equals("( SME )");

	                if (hasSmeTag) {
	                    System.out.println(companyName + " is an SME. Skipping bid."); // Debugging line
	                    continue; // Skip the rest of the loop if the company is an SME
	                }

	                // If the company does not have SME, click the Bid button
	                String bidButtonXPath = String.format("//tr[normalize-space(td[2]/div/label/span[contains(text(), '%s')])]/td[last()]/a[contains(@class, 'bid-btn')]", companyName.trim());

	                // Debugging line to check the XPath
	                System.out.println("Constructed XPath: " + bidButtonXPath);

	                // Check if the bid button exists and click it
	                if (page.locator(bidButtonXPath).count() > 0) {
	                    page.locator(bidButtonXPath).click();
	                    System.out.println("Clicked on Bid button for: " + companyName);
	                    bidClicked = true; // Set the flag to true
	                    break; // Exit the loop after clicking a bid
	                } else {
	                    System.out.println("No Bid button found for: " + companyName);
	                }
	            }
	            
		       context.tracing().stop(new Tracing.StopOptions()
		    		   .setPath(Paths.get("loginsteps.zip")));
		      
		       context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("applogin.json")));
		}
		     
	}

}
