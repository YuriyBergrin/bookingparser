

public class BookingParser {
    public static void main(String[] args) {
        DateManager manager = new DateManager();
        Parser parser = new Parser();
        System.out.println("Start " + manager.getCurrentTime());
        //country id россия - 176, Крым - 507 place=176 stars=0 days=15
        int place = 0;
        int stars = 0;
        int days = 0;
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
            }
            parser.setHotels(days, stars, place);
        } else {
            int[] daysArray = {3, 15, 30};
            int[] starsArray = {0, 1, 2, 3, 4, 5};
            int[] placeArray = {176, 507};

            for (int i = 0; i < daysArray.length; i++) {
                for (int j = 0; j < starsArray.length; j++) {
                    for (int k = 0; k < placeArray.length; k++) {
                        parser.setHotels(daysArray[i], starsArray[j], placeArray[k]);
                    }
                }
            }
        }
        System.out.println("Finish " + manager.getCurrentTime());
    }
}
