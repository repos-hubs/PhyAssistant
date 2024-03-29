package com.jibo.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.SqlUtils;

import com.jibo.entity.DrugDetailInfo;

import com.jibo.entity.DrugDetailTypeInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table InsertContent.
*/
public class DrugDetailTypeInfoDao extends AbstractDao<DrugDetailTypeInfo, Void> {

    public static final String TABLENAME = "InsertContent";

    /**
     * Properties of entity DrugDetailTypeInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", false, "InsertID");
        public final static Property Content = new Property(1, String.class, "content", false, "InsertContent");
        public final static Property Type = new Property(2, String.class, "type", false, "InsertType");
        public final static Property ChangeDate = new Property(3, String.class, "changeDate", false, "ChangeDate");
    };

    private DaoSession daoSession;


    public DrugDetailTypeInfoDao(DaoConfig config) {
        super(config);
    }
    
    public DrugDetailTypeInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'InsertContent' (" + //
                "'InsertID' TEXT," + // 0: id
                "'InsertContent' TEXT," + // 1: content
                "'InsertType' TEXT," + // 2: type
                "'ChangeDate' TEXT);"); // 3: changeDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'InsertContent'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DrugDetailTypeInfo entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(2, content);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(3, type);
        }
 
        String changeDate = entity.getChangeDate();
        if (changeDate != null) {
            stmt.bindString(4, changeDate);
        }
    }

    @Override
    protected void attachEntity(DrugDetailTypeInfo entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public DrugDetailTypeInfo readEntity(Cursor cursor, int offset) {
        DrugDetailTypeInfo entity = new DrugDetailTypeInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // content
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // type
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // changeDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DrugDetailTypeInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setContent(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setChangeDate(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(DrugDetailTypeInfo entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(DrugDetailTypeInfo entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getDrugDetailInfoDao().getAllColumns());
            builder.append(" FROM InsertContent T");
            builder.append(" LEFT JOIN InsertBasicInfo T0 ON T.'InsertID'=T0.'InsertID'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected DrugDetailTypeInfo loadCurrentDeep(Cursor cursor, boolean lock) {
        DrugDetailTypeInfo entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        DrugDetailInfo drugDetailInfo = loadCurrentOther(daoSession.getDrugDetailInfoDao(), cursor, offset);
        entity.setDrugDetailInfo(drugDetailInfo);

        return entity;    
    }

    public DrugDetailTypeInfo loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<DrugDetailTypeInfo> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<DrugDetailTypeInfo> list = new ArrayList<DrugDetailTypeInfo>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<DrugDetailTypeInfo> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<DrugDetailTypeInfo> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
