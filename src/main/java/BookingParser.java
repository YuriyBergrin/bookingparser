import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BookingParser {
    //country id россия - 176, Крым - 507
    private static final Logger logger = LogManager.getLogger(BookingParser.class);

    private static final String token1 = "3b15d1edb501c78d46f73c52739a49e7381dde42";//токены для дадаты
    private static final String token2 = "649f14340d58fa56c0785868a39f2ec9565f05dd";
    private static final String token3 = "582fca0795183711ba5ea1799f5547026af51f87";
    private static final String token4 = "4f553c5fe41de9828a5bd9c329de24c37bff94ee";
    public static String SELENOID_URL = "http://127.0.1.1:4444/wd/hub/";//по умолчанию, если задан параметром, то изменится

    public static void main(String[] args) {
        DateManager manager = new DateManager();
        Parser parser = new Parser();
        logger.info("Начинаем парсинг {}.", manager.getCurrentTime());
        //пример запуска с параметрами
        //place=176
        //stars=0
        //days=15
        //token=582fca0795183711ba5ea1799f5547026af51f87
        //selenoidurl=http://127.0.0.1:4444/wd/hub
        int place = 0;//Россия или Крым
        int stars = 0;//кол-во звезд отеля
        int days = 0;//через сколько дней брать бронь
        String token = "";//токен, может быть параметризирован

        if (args.length == 5) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].contains("place=")) {
                    place = Integer.parseInt(args[i].replaceAll("place=", ""));
                }
                if (args[i].contains("stars=")) {
                    stars = Integer.parseInt(args[i].replaceAll("stars=", ""));
                }
                if (args[i].contains("days=")) {
                    days = Integer.parseInt(args[i].replaceAll("days=", ""));
                }

                if (args[i].contains("token=")) {
                    token = args[i].replaceAll("token=", "");
                }

                if (args[i].contains("selenoidurl=")) {
                    SELENOID_URL = args[i].replaceAll("selenoidurl=", "");
                }
            }//задаем параметры
            logger.info("Парсинг за {} дней, {} звезд, {} место, {} токен.", days, stars, place, token);
            parser.parseHotels(days, stars, place, token);//парсим букинг
        }

        if (args.length == 1 & args[0].contains("selenoidurl=")) {
            SELENOID_URL = args[0].replaceAll("selenoidurl=", "");
            logger.info("Парсинг по всем отелям.");
            int[] daysArray = {3, 15, 30};
            int[] starsArray = {0, 1, 2, 3, 4, 5};
            int[] placeArray = {507, 176};
            String[] tokenArray = {token1, token2, token3};

            for (int i = 0; i < placeArray.length; i++) {
                for (int j = 0; j < daysArray.length; j++) {
                    for (int k = 0; k < starsArray.length; k++) {
                        try {
                            parser.parseHotels(daysArray[j], starsArray[k], placeArray[i], tokenArray[j]);
                        } catch (Error error) {
                            logger.info("Неудачная попытка за " + daysArray[j] + "  - дней "
                                    + starsArray[k] + "  - звезд " + placeArray[i] + " - место.");
                        }
                    }
                }
            }
        }

        logger.info("Парсинг закончен {}.", manager.getCurrentTime());
    }
}
