
package net.bat.store.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DownloadDBHelper extends SQLiteOpenHelper {
    /**
     * debug tag.
     */
    private static final String TAG = "DownloadDBHelper";

    /**
     * table name : download
     */
    private static final String TABLE_NAME = "download";

    /**
     * 表中字段[插入数据库时系统生成的id]
     */
    private static final String FIELD_ID = "_id";

    /**
     * 表中字段[下载url]
     */
    private static final String FIELD_URL = "url";

    /**
     * 表中字段[下载状态]
     */
    private static final String FIELD_DOWNLOAD_STATE = "downloadState";

    /**
     * 表中字段[文件放置路径]
     */
    private static final String FIELD_FILEPATH = "filepath";

    /**
     * 表中字段[文件名]
     */
    private static final String FIELD_FILENAME = "filename";

    private static final String FIELD_TITLE = "title";


    private static final String FIELD_THUMBNAIL = "thumbnail";

    /**
     * 表中字段[已完成文件大小]
     */
    private static final String FIELD_FINISHED_SIZE = "finishedSize";

    /**
     * 表中字段[文件总大小]
     */
    private static final String FIELD_TOTAL_SIZE = "totalSize";

    private static final String FIELD_PACKAGE_NAME = "packageName";

    private static final String FIELD_INSTALL_STATE= "intallState";
    private static final String  FIELD_FLOOR_TITLE="floortitle";
    private static final String  FIELD_APP_CATEGORY="appCategory";
    private static final String FIELD_APP_DEVELOPER="developer";
    private static final String  FIELD_APP_ATTRIBUTE="appAttribute";
    private static final String FIELD_FLOOR_ID = "floor_id";
    private static final String App_ID="app_id";
    private static final int DB_VERSION = 2;
    /**
     * Constructor
     * 
     * @param context Context
     * @param name 数据库文件名（.db）由调用者提供
     */
    public DownloadDBHelper(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    /**
     * 当数据库被首次创建时执行该方法<BR>
     * 创建表等初始化操作在该方法中执行，调用execSQL方法创建表
     * 
     * @param db SQLiteDatabase
     * @see SQLiteOpenHelper#onCreate(SQLiteDatabase)
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "create download table.");
        String sql = getSql();
        Log.i(TAG, sql);
        db.execSQL(sql);
    }

    private String getSql(){
        StringBuffer buffer = new StringBuffer("create table ");
        buffer.append(TABLE_NAME);
        buffer.append("(");
        buffer.append(FIELD_ID);
        buffer.append(" integer primary key autoincrement, ");
        buffer.append(FIELD_URL);
        buffer.append(" text, ");
        buffer.append(FIELD_DOWNLOAD_STATE);
        buffer.append(" text,");
        buffer.append(FIELD_FILEPATH);
        buffer.append(" text, ");
        buffer.append(FIELD_FILENAME);
        buffer.append(" text, ");
        buffer.append(FIELD_TITLE);
        buffer.append(" text, ");
        buffer.append(FIELD_THUMBNAIL);
        buffer.append(" text, ");
        buffer.append(FIELD_FINISHED_SIZE);
        buffer.append(" integer, ");
        buffer.append(FIELD_TOTAL_SIZE);
        buffer.append(" integer,");
        buffer.append(FIELD_INSTALL_STATE);
        buffer.append(" text, ");
        buffer.append(FIELD_FLOOR_TITLE);
        buffer.append(" text, ");
        buffer.append(FIELD_APP_CATEGORY);
        buffer.append(" text, ");
        buffer.append(FIELD_APP_DEVELOPER);
        buffer.append(" text, ");
        buffer.append(FIELD_APP_ATTRIBUTE);
        buffer.append(" integer,");
        buffer.append(FIELD_PACKAGE_NAME);
        buffer.append(" text,");
        buffer.append(FIELD_FLOOR_ID);
        buffer.append(" text,");
        buffer.append(App_ID);
        buffer.append(" text)");
        return buffer.toString();
    }

    /**
     * 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法。<BR>
     * 
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version
     * @see SQLiteOpenHelper#onUpgrade(SQLiteDatabase,
     *      int, int)
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         if(oldVersion!=newVersion){
             db.execSQL("ALTER TABLE "+TABLE_NAME+" RENAME TO download_temp");
             db.execSQL(getSql());
             db.execSQL("insert into "+TABLE_NAME+"("+FIELD_ID+", "+FIELD_URL+","+FIELD_DOWNLOAD_STATE+","+FIELD_FILEPATH+","+FIELD_FILENAME+","+FIELD_TITLE+","+FIELD_THUMBNAIL+","+FIELD_FINISHED_SIZE+","+FIELD_TOTAL_SIZE+","+FIELD_INSTALL_STATE+","+FIELD_FLOOR_TITLE+","+FIELD_APP_CATEGORY+","+FIELD_APP_DEVELOPER+","+FIELD_APP_ATTRIBUTE+","+FIELD_PACKAGE_NAME+","+FIELD_FLOOR_ID+","+App_ID+") "
                     + "select "+FIELD_ID+", "+FIELD_URL+","+FIELD_DOWNLOAD_STATE+","+FIELD_FILEPATH+","+FIELD_FILENAME+","+FIELD_TITLE+","+FIELD_THUMBNAIL+","+FIELD_FINISHED_SIZE+","+FIELD_TOTAL_SIZE+","+FIELD_INSTALL_STATE+","+FIELD_FLOOR_TITLE+","+FIELD_APP_CATEGORY+","+FIELD_APP_DEVELOPER+","+FIELD_APP_ATTRIBUTE+","+FIELD_PACKAGE_NAME+","+FIELD_FLOOR_ID+",\"-1\" from download_temp");
             db.execSQL("DROP TABLE download_temp");
         }
    }

    /**
     * 存入一条下载任务（直接存入数据库）<BR>
     * 
     * @param downloadTask DownloadTask
     */
    synchronized void insert(DownloadTask downloadTask) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, getContentValues(downloadTask));
    }


    DownloadTask query(String fileName){
        SQLiteDatabase db = getReadableDatabase();
        DownloadTask dlTask = null;
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                FIELD_URL, FIELD_DOWNLOAD_STATE, FIELD_FILEPATH, FIELD_FILENAME, FIELD_TITLE,
                FIELD_THUMBNAIL, FIELD_FINISHED_SIZE, FIELD_TOTAL_SIZE,FIELD_INSTALL_STATE,FIELD_FLOOR_TITLE,FIELD_APP_CATEGORY,FIELD_APP_DEVELOPER,FIELD_APP_ATTRIBUTE,
        FIELD_PACKAGE_NAME,FIELD_FLOOR_ID,App_ID}, FIELD_FILENAME + "=? OR " + FIELD_PACKAGE_NAME + "=?", new String[] {
            fileName,fileName
        }, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                dlTask = new DownloadTask(cursor.getString(0), null,cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getInt(12),cursor.getString(14), cursor.getString(15),null);
                dlTask.setDownloadState(DownloadState.valueOf(cursor.getString(1)));
                dlTask.setFinishedSize(cursor.getInt(6));
                dlTask.setTotalSize(cursor.getInt(7));
                dlTask.setInstallState(InstallState.valueOf(cursor.getString(8)));
                dlTask.setPackageName(cursor.getString(13));
                dlTask.setFloorId(cursor.getString(14));
            }
            cursor.close();
        }
        return dlTask;
    }

    DownloadTask query(String fileName,String url){
        SQLiteDatabase db = getReadableDatabase();
        DownloadTask dlTask = null;
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                FIELD_URL, FIELD_DOWNLOAD_STATE, FIELD_FILEPATH, FIELD_FILENAME, FIELD_TITLE,
                FIELD_THUMBNAIL, FIELD_FINISHED_SIZE, FIELD_TOTAL_SIZE,FIELD_INSTALL_STATE,FIELD_FLOOR_TITLE,FIELD_APP_CATEGORY,FIELD_APP_DEVELOPER,FIELD_APP_ATTRIBUTE,
        FIELD_PACKAGE_NAME,FIELD_FLOOR_ID,App_ID}, FIELD_FILENAME + "=? and " + FIELD_URL + "=?", new String[] {
                fileName,url
        }, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                dlTask = new DownloadTask(cursor.getString(0),null, cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getInt(12),cursor.getString(14),cursor.getString(15),null);
                dlTask.setDownloadState(DownloadState.valueOf(cursor.getString(1)));
                dlTask.setFinishedSize(cursor.getInt(6));
                dlTask.setTotalSize(cursor.getInt(7));
                dlTask.setInstallState(InstallState.valueOf(cursor.getString(8)));
                dlTask.setPackageName(cursor.getString(13));
                dlTask.setFloorId(cursor.getString(14));
            }
            cursor.close();
        }
        return dlTask;
    }
    /**
     * 查询数据库中所有下载任务集合<BR>
     * 
     * @return 下载任务List
     */
    List<DownloadTask> queryAll() {
        List<DownloadTask> tasks = new ArrayList<DownloadTask>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                FIELD_URL, FIELD_DOWNLOAD_STATE, FIELD_FILEPATH, FIELD_FILENAME, FIELD_TITLE,
                FIELD_THUMBNAIL, FIELD_FINISHED_SIZE, FIELD_TOTAL_SIZE,FIELD_INSTALL_STATE,FIELD_FLOOR_TITLE,FIELD_APP_CATEGORY,FIELD_APP_DEVELOPER,FIELD_APP_ATTRIBUTE,
        FIELD_PACKAGE_NAME,FIELD_FLOOR_ID,App_ID}, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DownloadTask dlTask = new DownloadTask(cursor.getString(0), null,cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getInt(12),cursor.getString(14),cursor.getString(15),null);
                dlTask.setDownloadState(DownloadState.valueOf(cursor.getString(1)));
                dlTask.setFinishedSize(cursor.getInt(6));
                dlTask.setTotalSize(cursor.getInt(7));
                dlTask.setInstallState(InstallState.valueOf(cursor.getString(8)));
                dlTask.setPackageName(cursor.getString(13));
                dlTask.setFloorId(cursor.getString(14));
                tasks.add(dlTask);
            }
            cursor.close();
        }

        return tasks;
    }

    List<DownloadTask> queryDownloaded() {
        List<DownloadTask> tasks = new ArrayList<DownloadTask>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                FIELD_URL, FIELD_DOWNLOAD_STATE, FIELD_FILEPATH, FIELD_FILENAME, FIELD_TITLE,
                FIELD_THUMBNAIL, FIELD_FINISHED_SIZE, FIELD_TOTAL_SIZE,FIELD_INSTALL_STATE,FIELD_FLOOR_TITLE,FIELD_APP_CATEGORY,FIELD_APP_DEVELOPER,FIELD_APP_ATTRIBUTE,
        FIELD_PACKAGE_NAME,FIELD_FLOOR_ID,App_ID}, FIELD_DOWNLOAD_STATE + "='FINISHED'", null, null, null, "_id desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DownloadTask dlTask = new DownloadTask(cursor.getString(0), null,cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getInt(12),cursor.getString(14),cursor.getString(15),null);
                dlTask.setDownloadState(DownloadState.valueOf(cursor.getString(1)));
                dlTask.setFinishedSize(cursor.getInt(6));
                dlTask.setTotalSize(cursor.getInt(7));
                dlTask.setInstallState(InstallState.valueOf(cursor.getString(8)));
                dlTask.setPackageName(cursor.getString(13));
                dlTask.setFloorId(cursor.getString(14));
                tasks.add(dlTask);
            }
            cursor.close();
        }

        return tasks;
    }

    List<DownloadTask> queryUnDownloaded() {
        List<DownloadTask> tasks = new ArrayList<DownloadTask>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                FIELD_URL, FIELD_DOWNLOAD_STATE, FIELD_FILEPATH, FIELD_FILENAME, FIELD_TITLE,
                FIELD_THUMBNAIL, FIELD_FINISHED_SIZE, FIELD_TOTAL_SIZE,FIELD_INSTALL_STATE,FIELD_FLOOR_TITLE,FIELD_APP_CATEGORY,FIELD_APP_DEVELOPER,FIELD_APP_ATTRIBUTE,
        FIELD_PACKAGE_NAME,FIELD_FLOOR_ID,App_ID}, FIELD_DOWNLOAD_STATE + "<> 'FINISHED'", null, null, null, "_id desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DownloadTask dlTask = new DownloadTask(cursor.getString(0), null,cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getInt(12),cursor.getString(14),cursor.getString(15),null);
                dlTask.setDownloadState(DownloadState.valueOf(cursor.getString(1)));
                dlTask.setFinishedSize(cursor.getInt(6));
                dlTask.setTotalSize(cursor.getInt(7));
                dlTask.setInstallState(InstallState.valueOf(cursor.getString(8)));
                dlTask.setPackageName(cursor.getString(13));
                dlTask.setFloorId(cursor.getString(14));
                tasks.add(dlTask);
            }
            cursor.close();
        }

        return tasks;
    }

    public List<DownloadTask> queryDownloadTask(){
        List<DownloadTask> tasks = new ArrayList<DownloadTask>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                FIELD_URL, FIELD_DOWNLOAD_STATE, FIELD_FILEPATH, FIELD_FILENAME, FIELD_TITLE,
                FIELD_THUMBNAIL, FIELD_FINISHED_SIZE, FIELD_TOTAL_SIZE,FIELD_INSTALL_STATE,FIELD_FLOOR_TITLE,FIELD_APP_CATEGORY,FIELD_APP_DEVELOPER,FIELD_APP_ATTRIBUTE,
                FIELD_PACKAGE_NAME,FIELD_FLOOR_ID,App_ID},null, null, null, null, "_id desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DownloadTask dlTask = new DownloadTask(cursor.getString(0), null,cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getInt(12),cursor.getString(14),cursor.getString(15),null);
                dlTask.setDownloadState(DownloadState.valueOf(cursor.getString(1)));
                dlTask.setFinishedSize(cursor.getInt(6));
                dlTask.setTotalSize(cursor.getInt(7));
                dlTask.setInstallState(InstallState.valueOf(cursor.getString(8)));
                dlTask.setPackageName(cursor.getString(13));
                dlTask.setFloorId(cursor.getString(14));
                tasks.add(dlTask);
            }
            cursor.close();
        }

        return tasks;
    }

    public List<DownloadTask> queryInstalled() {
        List<DownloadTask> tasks = new ArrayList<DownloadTask>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {
                FIELD_URL, FIELD_DOWNLOAD_STATE, FIELD_FILEPATH, FIELD_FILENAME, FIELD_TITLE,
                FIELD_THUMBNAIL, FIELD_FINISHED_SIZE, FIELD_TOTAL_SIZE,FIELD_INSTALL_STATE,FIELD_FLOOR_TITLE,FIELD_APP_CATEGORY,FIELD_APP_DEVELOPER,FIELD_APP_ATTRIBUTE,
        FIELD_PACKAGE_NAME,FIELD_FLOOR_ID,App_ID}, FIELD_INSTALL_STATE + "='INSTALLSUCCESS'", null, null, null, "_id desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DownloadTask dlTask = new DownloadTask(cursor.getString(0), null,cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getInt(12),cursor.getString(14),cursor.getString(15),null);
                dlTask.setDownloadState(DownloadState.valueOf(cursor.getString(1)));
                dlTask.setFinishedSize(cursor.getInt(6));
                dlTask.setTotalSize(cursor.getInt(7));
                dlTask.setInstallState(InstallState.valueOf(cursor.getString(8)));
                dlTask.setPackageName(cursor.getString(13));
                dlTask.setFloorId(cursor.getString(14));
                tasks.add(dlTask);
            }
            cursor.close();
        }

        return tasks;
    }

    /**
     * 更新下载任务<BR>
     * 
     * @param downloadTask DownloadTask
     */
    synchronized void update(DownloadTask downloadTask) {
        SQLiteDatabase db = getWritableDatabase();

        db.update(TABLE_NAME, getContentValues(downloadTask), FIELD_FILENAME + "=?", new String[] {
            downloadTask.getFileName()
        });
    }

    /**
     * 从数据库中删除一条下载任务<BR>
     * 
     * @param downloadTask DownloadTask
     */
    synchronized void delete(DownloadTask downloadTask) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, FIELD_FILENAME + "=?", new String[] {
            downloadTask.getFileName()
        });
    }

    /**
     * 将DownloadTask转化成ContentValues<BR>
     * 
     * @param downloadTask DownloadTask
     * @return ContentValues
     */
    private ContentValues getContentValues(DownloadTask downloadTask) {
        ContentValues values = new ContentValues();
        values.put(FIELD_URL, downloadTask.getUrl());
        values.put(FIELD_DOWNLOAD_STATE, downloadTask.getDownloadState().toString());
        values.put(FIELD_FILEPATH, downloadTask.getFilePath());
        values.put(FIELD_FILENAME, downloadTask.getFileName());
        values.put(FIELD_TITLE, downloadTask.getTitle());
        values.put(FIELD_THUMBNAIL, downloadTask.getThumbnail());
        values.put(FIELD_FINISHED_SIZE, downloadTask.getFinishedSize());
        values.put(FIELD_TOTAL_SIZE, downloadTask.getTotalSize());
        InstallState installState=downloadTask.getInstallState();
        if(installState!=null){
            values.put(FIELD_INSTALL_STATE,installState.toString());
        }
        values.put(FIELD_FLOOR_TITLE,downloadTask.getFloortitle());
        values.put(FIELD_APP_CATEGORY,downloadTask.getAppCategory());
        values.put(FIELD_APP_DEVELOPER,downloadTask.getDeveloper());
        values.put(FIELD_APP_ATTRIBUTE,downloadTask.getAppAttribute());
        values.put(FIELD_PACKAGE_NAME,downloadTask.getPackageName());
        values.put(FIELD_FLOOR_ID,downloadTask.getFloorId());
        values.put(App_ID,downloadTask.getAppId());

        return values;
    }
}
