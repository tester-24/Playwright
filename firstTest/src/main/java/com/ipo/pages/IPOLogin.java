package com.ipo.pages;

import com.microsoft.playwright.Page;

public class IPOLogin {
    private final Page page;

    public IPOLogin(Page page) {
        this.page = page;
    }

    public void navigateTo() {
        page.navigate("https://ipo.jainam.in/");
        page.getByText("Apply for an IPO").click();
    }

    public void enterUserId(String userId) {
        page.getByPlaceholder("Enter User ID").click();
        page.getByPlaceholder("Enter User ID").fill(userId);
    }

    public void enterPassword(String password) {
        page.getByPlaceholder("Enter Password").click();
        page.getByPlaceholder("Enter Password").fill(password);
    }

    public void login() {
        page.getByText("Login Now").click();
    }

    public void fillVerificationCode(String[] code) {
        for (int i = 0; i < code.length; i++) {
            page.getByPlaceholder("-").nth(i).fill(code[i]);
        }
        page.getByText("Continue").click();
    }
}
