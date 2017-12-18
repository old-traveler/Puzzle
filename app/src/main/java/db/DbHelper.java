package db;

/**
 * Created by hyc on 2017/12/18 08:00
 */

public class DbHelper {

    public static DbHelper dbHelper;

    private DbHelper(){
    }

    public static synchronized DbHelper getInstance(){
        if (dbHelper==null){
            dbHelper = new DbHelper();
        }
        return dbHelper;
    }


}
