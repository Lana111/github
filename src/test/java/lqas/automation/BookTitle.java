/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lqas.automation;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

/**
 *
 * @author Sveta
 */
public class BookTitle {

  WebDriver driver;
  List<WebElement> booksList;
  MySoftAssert softAssert = new MySoftAssert();

  BookTitle() {
	booksList = new ArrayList();
  }

  @BeforeSuite
  void initialis() throws InterruptedException {
	System.setProperty("webdriver.chrome.driver", "D:\\Java\\Lits Дз\\SeleniumTests\\src\\chromedriver.exe");
	driver = new ChromeDriver();
	driver.get("https://www.amazon.com/gp/bestsellers/books/ref=sv_b_1");
	Thread.sleep(1000);
  }

  @Test(priority = 1)
  public void getBooksList() {

	List<WebElement> list = driver.findElements(By.xpath(".//div[@id='zg_centerListWrapper']//div"));
	for (WebElement el : list) {
	  String s = el.getAttribute("class");
	  if ("zg_itemWrapper".equals(s)) {
		booksList.add(el);
	  }

	}

	int expectedCountOfTitles = 20;
	Assert.assertEquals(expectedCountOfTitles, booksList.size());

  }

  public String readingOfTitle(int index, String whot, String path) {
	String reading = "";
	try {
	  reading = booksList.get(index).findElement(By.xpath(path)).getText();
	} catch (Exception e) {
	  softAssert.fail(whot + " is not defined");
	}
	return reading;
  }

  public String getInnerNameOfBook(int index) {
	String reading = "";
	try {
	  reading = driver.findElement(By.id("productTitle")).getText();
	} catch (Exception e) {
	  softAssert.fail("inner name is not defined");
	}
	return reading;
  }

  public String getInnerNameOfAuthor(int index) {
	String reading = "";
	try {
	  reading = driver.findElement(By.id("byline")).getText();
	} catch (Exception e) {
	  softAssert.fail("inner author is not defined");
	}
	return reading;
  }

  public String getInnerInnerPriceOfBook(int index) {
	String reading = "";
	try {
	  reading = driver.findElement(By.className("a-color-price")).getText();
	} catch (Exception e) {
	  softAssert.fail("inner price is not defined");
	}
	return reading;
  }

  public void processingAndCheck(String title, String inner, String whotCheck) {
	inner = inner.replaceAll("\n", " ");
	inner = inner.replaceAll(" ", "");
	title = title.replaceAll(" ", "");
	if ("name".equals(whotCheck)) {
	  title = title.substring(0, (title.length() - 3));
	}
	if (inner.contains(title)) {
	  inner = title;
	}
	softAssert.assertEquals(title, inner, whotCheck);
  }

  @Test(priority = 2)
  public void inspection() {
	String[] windowHandles;

	for (int i = 18; i < booksList.size(); i++) {
	  softAssert.setBookNumber(i);
	  String titleNameOfBook = readingOfTitle(i, "titleName", ".//div[@class='zg_title']");
	  String titleNameOfAuthor = readingOfTitle(i, "titleName", ".//div[@class='zg_byline']");
	  String titlePriceOfBook = readingOfTitle(i, "titleName", ".//div[@class='zg_price']");

	  new Actions(driver).keyDown(Keys.CONTROL).click(booksList.get(i).findElement(By.tagName("a")))
			  .keyUp(Keys.CONTROL).build().perform();
	  windowHandles = driver.getWindowHandles().toArray(new String[0]);
	  driver.switchTo().window(windowHandles[1]);

	  String innerNameOfBook = getInnerNameOfBook(i);
	  String innerNameOfAuthor = getInnerNameOfAuthor(i);
	  String innerPriceOfBook = getInnerInnerPriceOfBook(i);

	  processingAndCheck(titleNameOfBook, innerNameOfBook, "name");
	  processingAndCheck(titleNameOfAuthor, innerNameOfAuthor, "author");
	  processingAndCheck(titlePriceOfBook, innerPriceOfBook, "author");
	  
	  //new Actions(driver).keyUp(Keys.CONTROL).click(booksList.get(i).findElement(By.tagName("a"))).build().perform();
	  driver.close();
	  driver.switchTo().window(windowHandles[0]);
	}
	System.out.println("All check!");
  }

  @AfterSuite
  void close() {
	driver.quit();
	softAssert.assertAll();
  }

}


/*
new Actions(driver).keyDown(Keys.CONTROL).click(booksList.get(i).findElement(By.tagName("a")))
			  .keyUp(booksList.get(i).findElement(By.tagName("a")), Keys.CONTROL).build().perform();
*/


class MySoftAssert extends SoftAssert {

  int bookNumber;
  String message = "";
  boolean errors = false;

  public void setBookNumber(int num) {
	bookNumber = num;
  }

  @Override
  public void assertEquals(String actual, String expected, String whotCheck) {

	switch (whotCheck) {
	  case "name":
		if (!actual.equals(expected)) {
		  message += "book " + (bookNumber + 1) + " - an error in the name of the book\n";
		  errors = true;
		  //System.out.println("An error in the name of the book");
		}
		break;
	  case "author":
		if (!actual.equals(expected)) {
		  message += (bookNumber + 1) + " book - an error in the name of the author\n";
		  errors = true;
		  //System.out.println("An error in the name of the author");
		}
		break;
	  case "price":
		if (!actual.equals(expected)) {
		  message += (bookNumber + 1) + " book - an error in the name of the price\n";
		  errors = true;
		  //System.out.println("An error in the name of the price");
		}
		break;
	  default:
		message += (bookNumber + 1) + "Unknown error\n";
		errors = true;
		//System.out.println("Unknown error");
		break;
	}
  }

  @Override
  public void fail(String message) {
	this.message += (bookNumber + 1) + " book - " + message + "\n";
  }

  @Override
  public void assertAll() {
	if (errors == true) {
	  System.out.println(message);
	  throw new AssertionError(message);
	}

  }

}



