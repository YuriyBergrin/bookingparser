

public class BookingParser {

    private static final String token1 = "3b15d1edb501c78d46f73c52739a49e7381dde42";
    private static final String token2 = "649f14340d58fa56c0785868a39f2ec9565f05dd";
    private static final String token3 = "582fca0795183711ba5ea1799f5547026af51f87";

    public static void main(String[] args) {
        DateManager manager = new DateManager();
        Parser parser = new Parser();
        System.out.println("Start " + manager.getCurrentTime());
        //country id россия - 176, Крым - 507 place=176 stars=0 days=15
        int place = 0;
        int stars = 0;
        int days = 0;
        String token = "";
        //
        if (args.length > 0) {
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
            }
            parser.setHotels(days, stars, place, token);
        } else {
            int[] daysArray = {30, 15, 3};
            int[] starsArray = {0, 1, 2, 3, 4, 5};
            int[] placeArray = {176, 507};
            String[] tokenArray = {token1, token2, token3};


            for (int i = 0; i < placeArray.length; i++) {
                for (int j = 0; j < daysArray.length; j++) {
                    for (int k = 0; k < starsArray.length; k++) {
                        try {
                            parser.setHotels(daysArray[j], starsArray[k], placeArray[i], tokenArray[j]);
                        } catch (Error error) {
                            System.out.println("НЕУДАЧНАЯ ПОПЫТКА ДЛЯ БРОНИРОВАНИЯ ЗА " + daysArray[j] + "  - ДНЕЙ "
                                    + starsArray[k] + "  - ЗВЕЗД " + placeArray[i] + " - МЕСТО");
                        }
                    }
                }
            }//последний

        }
        System.out.println("Finish " + manager.getCurrentTime());
    }
}
