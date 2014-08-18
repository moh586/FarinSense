package farin.code.air.calender;

public class YearMonthDate {

    public YearMonthDate(int year, int month, int date,int daynum) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.daynum=daynum;
    }

    private int year;
    private int month;
    private int date;
    private int daynum;

    String[][] DayName={{"شنبه","یکشنبه","دوشنبه","سه‌شنبه","چهارشنبه","پنج‌شنبه","جمعه"},
    		{"سبت","أَحَد","إثْنَان","ثلاثاء","أربعاء","خمیس","الجمعة"},
    		{"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"}};
    
    String[][] MounthName={{"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر"
    	,"آبان","آذر","دی","بهمن","اسفند"},
    		{"محرم","صفر","ربیع‌الاول","ربیع‌الثانی","جمادی‌الاول","جمادی‌الثانی","رجب"
        	,"شعبان","رمضان","شوال","ذیقعده","ذیحجه"},
        		{"Jan","Feb","Mar","Apr","May","Jun","Jul"
            	,"Aug","Sep","Oct","Nov","Dec"}};
    
 
   
    
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String toString() {
        return getYear() + "/" + getMonth() + "/" + getDate();
    }

	
    public int getDaynum() {
		return daynum;
	}

	
    public void setDaynum(int daynum) {
		this.daynum = daynum;
	}

    public String getDayName(int key) {
		return DayName[key][getDaynum()%7];
	}
    
    public String getMonthName(int key) {
		return MounthName[key][getMonth()%12];
	}

	

}
