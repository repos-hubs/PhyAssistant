package com.jibo.dao;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.Query;
import de.greenrobot.dao.QueryBuilder;

import com.jibo.entity.AhfsDrugInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ahfsdrug.
*/
public class AhfsDrugInfoDao extends AbstractDao<AhfsDrugInfo, Void> {

    public static final String TABLENAME = "ahfsdrug";

    /**
     * Properties of entity AhfsDrugInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Pid = new Property(0, String.class, "pid", false, "pid");
        public final static Property DrugName = new Property(1, String.class, "drugName", false, "drugName");
        public final static Property ShortName = new Property(2, String.class, "shortName", false, "shortName");
        public final static Property ChangeFields = new Property(3, String.class, "changeFields", false, "changeFields");
        public final static Property State = new Property(4, String.class, "state", false, "state");
        public final static Property UpdateTime = new Property(5, String.class, "updateTime", false, "updateTime");
        public final static Property FileId = new Property(6, String.class, "fileId", false, "fileId");
    };

    private Query<AhfsDrugInfo> drugInfo_AhfsDrugInfoListQuery;

    public AhfsDrugInfoDao(DaoConfig config) {
        super(config);
    }
    
    public AhfsDrugInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ahfsdrug' (" + //
                "'pid' TEXT," + // 0: pid
                "'drugName' TEXT," + // 1: drugName
                "'shortName' TEXT," + // 2: shortName
                "'changeFields' TEXT," + // 3: changeFields
                "'state' TEXT," + // 4: state
                "'updateTime' TEXT," + // 5: updateTime
                "'fileId' TEXT);"); // 6: fileId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ahfsdrug'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AhfsDrugInfo entity) {
        stmt.clearBindings();
 
        String pid = entity.getPid();
        if (pid != null) {
            stmt.bindString(1, pid);
        }
 
        String drugName = entity.getDrugName();
        if (drugName != null) {
            stmt.bindString(2, drugName);
        }
 
        String shortName = entity.getShortName();
        if (shortName != null) {
            stmt.bindString(3, shortName);
        }
 
        String changeFields = entity.getChangeFields();
        if (changeFields != null) {
            stmt.bindString(4, changeFields);
        }
 
        String state = entity.getState();
        if (state != null) {
            stmt.bindString(5, state);
        }
 
        String updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindString(6, updateTime);
        }
 
        String fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindString(7, fileId);
        }
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public AhfsDrugInfo readEntity(Cursor cursor, int offset) {
        AhfsDrugInfo entity = new AhfsDrugInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // pid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // drugName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // shortName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // changeFields
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // state
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // updateTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // fileId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AhfsDrugInfo entity, int offset) {
        entity.setPid(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setDrugName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setShortName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setChangeFields(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setState(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUpdateTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFileId(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(AhfsDrugInfo entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(AhfsDrugInfo entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "ahfsDrugInfoList" to-many relationship of DrugInfo. */
    public synchronized List<AhfsDrugInfo> _queryDrugInfo_AhfsDrugInfoList(String pid) {
        if (drugInfo_AhfsDrugInfoListQuery == null) {
            QueryBuilder<AhfsDrugInfo> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Pid.eq(pid));
            drugInfo_AhfsDrugInfoListQuery = queryBuilder.build();
        } else {
            drugInfo_AhfsDrugInfoListQuery.setParameter(0, pid);
        }
        return drugInfo_AhfsDrugInfoListQuery.list();
    }

}