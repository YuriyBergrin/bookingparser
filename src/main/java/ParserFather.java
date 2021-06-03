import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.StaleElementReferenceException;

public class ParserFather {
    public String retryingGetText(SelenideElement element) {
        String value = null;
        int attempts = 0;
        while (attempts < 10) {
            try {
                value = element.getText();
                break;
            } catch (StaleElementReferenceException e) {
            }
            attempts++;
        }
        return value;
    }


    public String retryingGetAttribute(SelenideElement element, String attribute) {
        String value = null;
        int attempts = 0;
        while (attempts < 10) {
            try {
                value = element.getAttribute(attribute);
                break;
            } catch (StaleElementReferenceException e) {
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

    public String retryingGetTextWithNoSuchEx(SelenideElement element) {
        String value = null;
        int attempts = 0;
        try {
            while (attempts < 10) {
                try {
                    value = element.getText();
                    break;
                } catch (StaleElementReferenceException e) {
                }
                attempts++;
            }
        } catch (Exception e) {
            value = "";
        }
        return value;
    }
}
