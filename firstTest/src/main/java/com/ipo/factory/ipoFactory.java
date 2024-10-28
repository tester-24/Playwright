package com.ipo.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class ipoFactory {

	Playwright playwright;
	Browser browser;
	BrowserContext browserContext;
	Page page;
	
	public Page initBrowser(String browsername)
	{
		System.out.println(browsername);
		playwright = Playwright.create();
		
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
		           .setChannel("chrome"));
        
		browserContext = browser.newContext();
		page = browserContext.newPage();
		page.navigate("https://ipo.jainam.in/");
		
		return page; //page class refrence
	}
}
