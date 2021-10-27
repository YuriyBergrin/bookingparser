import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class Parser extends ParserParent {
    private static final Logger logger = LogManager.getLogger(Parser.class);
    private DateManager dateManager = new DateManager();
    private String url;


    public void parseHotels(int bookType, int hotelClass, int dest_id, String token) {
        ApiHelper apiHelper = new ApiHelper();
        String country = dest_id == 176 ? "РОССИЯ" : "КРЫМ";
        String resultRegion;
        logger.info("бронь за - " + bookType + " дней, звезд у отеля - " + hotelClass + ", место - " + country + " token= " + token);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Статистика по отелям и хостелам");

        int rowNum = 0;
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue("Название");
        row.createCell(1).setCellValue("Ссылка");
        row.createCell(2).setCellValue("Город");
        row.createCell(3).setCellValue("Регион");
        row.createCell(4).setCellValue("Кол-во звезд");
        row.createCell(5).setCellValue("Формат бронирования");
        row.createCell(6).setCellValue("Тип номера");
        row.createCell(7).setCellValue("Стоимость за 1 ночь в руб");
        row.createCell(8).setCellValue("Кол-во ночей прибывания");


        Configuration.startMaximized = true;
        Configuration.remote = BookingParser.SELENOID_URL;
        Configuration.browserCapabilities.setCapability("enableVNC", true);
        Configuration.browserCapabilities.setCapability("enableVideo", false);
//        Configuration.holdBrowserOpen = true;
        Selenide.clearBrowserCookies();
        open(getUrl(bookType, dest_id));
        $("#onetrust-accept-btn-handler").click();
        $(String.format("[data-filters-item=\"class:class=%s\"]", hotelClass)).click();
        $x("//*[@data-filters-group=\"ht_id\"]//div[text()=\"Отели\"]").click();
        $x("//*[@data-filters-group=\"ht_id\"]//div[text()=\"Хостелы\"]").click();
        $x("//*[@data-filters-group=\"fc\"]//div[text()=\"Бесплатная отмена бронирования\"]").click();
        //начинаем сбор инфы


        rowNum = 1;

        while (true) {

            List<SelenideElement> hotelItems = $$("[data-testid=\"property-card\"]");

            for (int i = 1; i <= hotelItems.size(); i++) {
                String name, link, city, type, price;
                name = retryingGetText($x(String.format("(//*[@data-testid=\"property-card\"]//div[@data-testid=\"title\"])[%s]", i))).trim();
                link = retryingGetAttribute($x(String.format("(//*[@data-testid=\"property-card\"]//a[@data-testid=\"title-link\"])[%s]", i)), "href");
                city = retryingGetText($x(String.format("(//*[@data-testid=\"property-card\"]//*[@data-testid=\"address\"])[%s]", i))).trim();
                type = retryingGetText($x(String.format("(//*[@data-testid=\"property-card\"]//*[@data-testid=\"recommended-units\"]//span)[%s]", i))).trim();
                price = retryingGetText($x(String.format("(//*[@data-testid=\"property-card\"]//*[@data-testid=\"price-and-discounted-price\"]/span)[%s]", i))).trim();

                double dPrice;
                try {
                    dPrice = Double.parseDouble(price.replaceAll("[^0-9]", "").trim()) / 3;
                } catch (NumberFormatException e) {
                    dPrice = 0;
                }

                city = city.replaceAll("район", "");

                Row row1 = sheet.createRow(rowNum);
                row1.createCell(0).setCellValue(name);
                row1.createCell(1).setCellValue(link);
                row1.createCell(2).setCellValue(city.replaceAll(".+ ", ""));

                resultRegion = apiHelper.getRegion(city, token);

                row1.createCell(3).setCellValue(resultRegion);
                row1.createCell(4).setCellValue(hotelClass);
                row1.createCell(5).setCellValue(String.format("за %s дней", bookType));
                row1.createCell(6).setCellValue(type);
                row1.createCell(7).setCellValue(dPrice);
                row1.createCell(8).setCellValue("3");

                rowNum++;

                logger.info(" | " + name + " | " + resultRegion + " | " + city.replaceAll(".+ ", "") + " | " + hotelClass + " | " + dPrice);
            }

            if ($("button[aria-label=\"Следующая страница\"]").getAttribute("disabled") == null) {
                logger.info("Мы можем перейти на следующую страницу.");
                logger.info("Переходим на следующую страницу.");
                $("button[aria-label=\"Следующая страница\"]").click();
                pause();
            } else {
                logger.info("Мы не можем перейти на следующую страницу.");
                break;
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream
                    (dateManager.getCurrentDatePlusDays(0) + "_СТАТИСТИКА_ЦЕН_НА_ОТЕЛИ_" + hotelClass +
                            "_ЗВЕЗД_БРОНЬ_ЗА_" + bookType + "_ДНЕЙ_" + country + ".xls");
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getUrl(int bookType, int dest_id) {
        String checkin_month = dateManager.getCurrentMonthPlusDays(bookType);
        String checkout_month = dateManager.getCurrentMonthPlusDays(bookType + 3);
        String checkin_monthday = dateManager.getCurrentMonthDayPlusDays(bookType);
        String checkout_monthday = dateManager.getCurrentMonthDayPlusDays(bookType + 3);

        url = "https://www.booking.com/searchresults.ru.html?" +
                "dest_id=" + dest_id +
                "&dest_type=country" +
                "&checkin_year=2021" +
                "&checkin_month=" + checkin_month +
                "&checkin_monthday=" + checkin_monthday +
                "&checkout_year=2021" +
                "&checkout_month=" + checkout_month +
                "&checkout_monthday=" + checkout_monthday +
                "&group_adults=2" +
                "&group_children=0" +
                "&no_rooms=1" +
                "&sb_travel_purpose=business";
        return url;
    }
}
