package com.jibo.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import com.jibo.entity.AdminRouteInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table AdminRouteInfo.
*/
public class AdminRouteInfoDao extends AbstractDao<AdminRouteInfo, String> {

    public static final String TABLENAME = "AdminRouteInfo";

    /**
     * Properties of entity AdminRouteInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "AdminRouteID");
        public final static Property NameEn = new Property(1, String.class, "nameEn", false, "AdminRouteEN");
        public final static Property NameCn = new Property(2, String.class, "nameCn", false, "AdminRouteCN");
        public final static Property ChangeDate = new Property(3, String.class, "changeDate", false, "ChangeDate");
    };


    public AdminRouteInfoDao(DaoConfig config) {
        super(config);
    }
    
    public AdminRouteInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'AdminRouteInfo' (" + //
                "'AdminRouteID' TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "'AdminRouteEN' TEXT," + // 1: nameEn
                "'AdminRouteCN' TEXT," + // 2: nameCn
                "'ChangeDate' TEXT);"); // 3: changeDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'AdminRouteInfo'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AdminRouteInfo entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String nameEn = entity.getNameEn();
        if (nameEn != null) {
            stmt.bindString(2, nameEn);
        }
 
        String nameCn = entity.getNameCn();
        if (nameCn != null) {
            stmt.bindString(3, nameCn);
        }
 
        String changeDate = entity.getChangeDate();
        if (changeDate != null) {
            stmt.bindString(4, changeDate);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public AdminRouteInfo readEntity(Cursor cursor, int offset) {
        AdminRouteInfo entity = new AdminRouteInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // nameEn
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // nameCn
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // changeDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AdminRouteInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setNameEn(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNameCn(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setChangeDate(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(AdminRouteInfo entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(AdminRouteInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
