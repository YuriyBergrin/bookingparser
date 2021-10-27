import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.StaleElementReferenceException;

public class ParserParent {
    protected String retryingGetText(SelenideElement element) {
        String value = null;
        int attempts = 0;
        while (attempts < 10) {
            try {
                if (element.isDisplayed()) {
                    value = element.getText();
                } else {
                    value = "null";
                }
                break;
            } catch (StaleElementReferenceException ignored) {
            }
            attempts++;
        }
        return value;
    }


    protected String retryingGetAttribute(SelenideElement element, String attribute) {
        String value = null;
        int attempts = 0;
        while (attempts < 10) {
            try {
                if (element.isDisplayed()) {
                    value = element.getAttribute(attribute);
                } else {
                    value = "0.0000";
                }
                break;
            } catch (StaleElementReferenceException ignored) {
            }
            attempts++;
        }
        return value;
    }

    protected void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
