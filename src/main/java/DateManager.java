import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateManager {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat dateFormatMM = new SimpleDateFormat("MM");
    private SimpleDateFormat dateFormatDD = new SimpleDateFormat("dd");
    private SimpleDateFormat dateFormathh = new SimpleDateFormat("HH:mm:ss");
    Calendar instance = Calendar.getInstance();

    public String getCurrentDatePlusDays(int days) {

        instance.setTime(new Date()); //устанавливаем дату, с которой будет производить операции
        instance.add(Calendar.DAY_OF_MONTH, days);// прибавляем 3 дня к установленной дате
        Date newDate = instance.getTime(); // получаем измененную дату
        return dateFormat.format(newDate);
    }

    public String getCurrentMonthPlusDays(int days) {
        instance.setTime(new Date()); //устанавливаем дату, с которой будет производить операции
        instance.add(Calendar.DAY_OF_MONTH, days);// прибавляем 3 дня к установленной дате
        Date newDate = instance.getTime(); // получаем измененную дату
        return dateFormatMM.format(newDate);
    }

    public String getCurrentMonthDayPlusDays(int days) {
        instance.setTime(new Date()); //устанавливаем дату, с которой будет производить операции
        instance.add(Calendar.DAY_OF_MONTH, days);// прибавляем 3 дня к установленной дате
        Date newDate = instance.getTime(); // получаем измененную дату
        return dateFormatDD.format(newDate);
    }

    public String getCurrentTime() {
        instance.setTime(new Date());
        Date newDate = instance.getTime();
        return dateFormathh.format(newDate);
    }
}
