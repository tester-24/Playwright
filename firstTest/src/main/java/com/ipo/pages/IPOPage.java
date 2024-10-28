package com.ipo.pages;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class IPOPage {
    private final Page page;

    public IPOPage(Page page) {
        this.page = page;
    }

    public void navigateToDashboard() {
        page.getByText("IPO Dashboard").nth(1).click();
        page.getByText("Bid").nth(1).click();
        page.navigate("https://ipo.jainam.in/ipolist");
        page.waitForSelector("section.bid-section-ipolist kendo-grid .k-table-tbody");
    }

    public void placeBid(String targetCompanyName) {
        List<Locator> rows = page.locator("section.bid-section-ipolist kendo-grid .k-table-tbody tr").all();

        boolean bidClicked = false;

        for (Locator row : rows) {
            String companyName = row.locator("td:nth-child(2) div label span").textContent().trim();
            String smeTag = row.locator("td:nth-child(2) span.bid-right").textContent().trim();

            System.out.println("Checking company: " + companyName); // Debugging line

            boolean hasSmeTag = smeTag.equals("( SME )");

            if (hasSmeTag) {
                System.out.println(companyName + " is an SME. Skipping bid."); // Debugging line
                continue;
            }

            String bidButtonXPath = String.format("//tr[normalize-space(td[2]/div/label/span[contains(text(), '%s')])]/td[last()]/a[contains(@class, 'bid-btn')]", companyName.trim());
            System.out.println("Constructed XPath: " + bidButtonXPath);

            if (page.locator(bidButtonXPath).count() > 0) {
                page.locator(bidButtonXPath).click();
                System.out.println("Clicked on Bid button for: " + companyName);
                bidClicked = true;
                break;
            } else {
                System.out.println("No Bid button found for: " + companyName);
            }
        }
    }
}