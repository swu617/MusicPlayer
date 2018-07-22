/*
 * Created by I310736 on 2014/10/30
 * Copyright (c) 2014 SAP. All rights reserved.
 */

package com.sam.music.player.db;

/**
 * Created by I310736 on 10/30/2014.
 * <p/>
 * This class is target for database create_table sql case in DBModule,
 */
public final class ConflictType {
    /**
     * When a constraint violation occurs, an immediate ROLLBACK occurs, thus
     * ending the current transaction, and the command aborts with a return code
     * of SQLITE_CONSTRAINT. If no transaction is active (other than the implied
     * transaction that is created on every command) then this algorithm works
     * the same as ABORT.
     */
    public static final int CONFLICT_ROLLBACK = 1;

    /**
     * When a constraint violation occurs,no ROLLBACK is executed so changes
     * from prior commands within the same transaction are preserved. This is
     * the default behavior.
     */
    public static final int CONFLICT_ABORT = 2;

    /**
     * When a constraint violation occurs, the command aborts with a return code
     * SQLITE_CONSTRAINT. But any changes to the database that the command made
     * prior to encountering the constraint violation are preserved and are not
     * backed out.
     */
    public static final int CONFLICT_FAIL = 3;

    /**
     * When a constraint violation occurs, the one row that contains the
     * constraint violation is not inserted or changed. But the command
     * continues executing normally. Other rows before and after the row that
     * contained the constraint violation continue to be inserted or updated
     * normally. No error is returned.
     */
    public static final int CONFLICT_IGNORE = 4;

    /**
     * When a UNIQUE constraint violation occurs, the pre-existing rows that are
     * causing the constraint violation are removed prior to inserting or
     * updating the current row. Thus the insert or update always occurs. The
     * command continues executing normally. No error is returned. If a NOT NULL
     * constraint violation occurs, the NULL value is replaced by the default
     * value for that column. If the column has no default value, then the ABORT
     * algorithm is used. If a CHECK constraint violation occurs then the IGNORE
     * algorithm is used. When this conflict resolution strategy deletes rows in
     * order to satisfy a constraint, it does not invoke delete triggers on
     * those rows. This behavior might change in a future release.
     */
    public static final int CONFLICT_REPLACE = 5;

    /**
     * Use the following when no conflict action is specified.
     */
    public static final int CONFLICT_NONE = 0;

    private static final String[] CONFLICT_VALUES = new String[]{"", "ON CONFLICT ROLLBACK ", " ON CONFLICT ABORT ", "ON CONFLICT FAIL ", "ON CONFLICT IGNORE ", "ON CONFLICT REPLACE "};

    public static final String getConflictValue(int type) {
        return CONFLICT_VALUES[type];
    }
}
