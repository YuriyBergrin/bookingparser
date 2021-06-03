import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class Parser extends ParserFather {
    private DateManager dateManager = new DateManager();
    private String url;


    public void setHotels(int bookType, int hotelClass, int dest_id) {
        ApiHelper apiHelper = new ApiHelper();
        System.out.println("бронь за " + bookType + " звезд " + hotelClass);
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
        Configuration.remote = "http://127.0.0.1:4444/wd/hub";
//        Configuration.holdBrowserOpen = true;
//        Configuration.headless = true;
        Selenide.clearBrowserCookies();
        open(getUrl(bookType, dest_id));
        $("#onetrust-accept-btn-handler").click();
        $(String.format("#filter_class [data-id=\"class-%s\"]", hotelClass)).click();
        $x("//*[@id=\"filter_hoteltype\"]//span[contains(text(),\"Отели\") and not (contains(text(),\"типа\"))" +
                " and not (contains(text(),\"свиданий\")) and not (contains(text(),\"эконом\"))]").click();
        $x("//*[@id=\"filter_hoteltype\"]//span[contains(text(),\"Хостелы\")]").click();
        $x("//*[@id=\"filter_fc\"]//span[contains(text(),\"Бесплатная отмена бронирования\")]").click();
        //начинаем сбор инфы

        List<SelenideElement> hotelItems = $$(".sr_item");

        for (int i = 1; i <= hotelItems.size(); i++) {
            String name, link, region, coordinates, stars, type, price;
            name = retryingGetText($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"sr-hotel__name\")])[%s]", i))).trim();
            link = retryingGetAttribute($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"js-sr-hotel-link\")])[%s]", i)), "href");
            region = retryingGetText($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"r_card_address_line\")]/a)[%s]", i))).replaceAll("Показать на карте", "").trim();
            coordinates = retryingGetAttribute($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"r_card_address_line\")]/a)[%s]", i)), "data-coords");
            type = retryingGetText($x(String.format("(//div[contains(@class,\"sr_item \")]//strong)[%s]", i))).trim();
            price = retryingGetText($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"bui-price-display__value prco-inline-block-maker-helper\")])[%s]", i))).trim();

            double dPrice = Double.parseDouble(price.replaceAll("[^0-9]", "").trim()) / 3;
            region = region.replaceAll("район", "");
            rowNum = 1;
            Row row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(name);
            row1.createCell(1).setCellValue(link);
            row1.createCell(2).setCellValue(region.replaceAll(".+ ", ""));
            row1.createCell(3).setCellValue(apiHelper.getRegion(coordinates));
            row1.createCell(4).setCellValue(hotelClass);
            row1.createCell(5).setCellValue(String.format("за %s дней", bookType));
            row1.createCell(6).setCellValue(type);
            row1.createCell(7).setCellValue(dPrice);
            row1.createCell(8).setCellValue("3");
        }

        if ($(".bui-pagination__next-arrow").isDisplayed()) {


            $(".bui-pagination__next-arrow").click();

            boolean flag = true;
            rowNum = 2;

            while (flag) {

                hotelItems = $$(".sr_item");
                int size = hotelItems.size();


                for (int i = 1; i < size; i++) {
                    hotelItems = $$(".sr_item");
                    size = hotelItems.size();

                    String name, link, region, coordinates, stars, type, price;
                    name = retryingGetText($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"sr-hotel__name\")])[%s]", i))).trim();
                    link = retryingGetAttribute($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"js-sr-hotel-link\")])[%s]", i)), "href");
                    region = retryingGetText($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"r_card_address_line\")]/a)[%s]", i))).replaceAll("Показать на карте", "").trim();
                    coordinates = retryingGetAttribute($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"r_card_address_line\")]/a)[%s]", i)), "data-coords");
                    type = retryingGetTextWithNoSuchEx($x(String.format("(//*[@class=\"c-beds-configuration\"])[%s]", i))).trim();
                    price = retryingGetText($x(String.format("(//div[contains(@class,\"sr_item \")]//*[contains(@class,\"bui-price-display__value prco-inline-block-maker-helper\")])[%s]", i))).trim();

                    double dPrice = Double.parseDouble(price.replaceAll("[^0-9]", "").trim()) / 3;
                    region = region.replaceAll("район", "");

                    Row row1 = sheet.createRow(rowNum);
                    row1.createCell(0).setCellValue(name);
                    row1.createCell(1).setCellValue(link);
                    row1.createCell(2).setCellValue(region.replaceAll(".+ ", ""));
                    row1.createCell(3).setCellValue(apiHelper.getRegion(coordinates));
                    row1.createCell(4).setCellValue(hotelClass);
                    row1.createCell(5).setCellValue(String.format("за %s дней", bookType));
                    row1.createCell(6).setCellValue(type);
                    row1.createCell(7).setCellValue(dPrice);
                    row1.createCell(8).setCellValue("3");

                    rowNum++;

                }

                if ($(".bui-pagination__item--disabled span").isDisplayed()) {
                    flag = false;
                    break;
                } else {
                    $(".bui-pagination__next-arrow").click();
                    pause();
                }
            }
        }

        try {
            String country = dest_id == 176 ? "РОССИЯ" : "КРЫМ";
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
