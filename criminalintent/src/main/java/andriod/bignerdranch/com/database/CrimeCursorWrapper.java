package andriod.bignerdranch.com.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import andriod.bignerdranch.com.Crime;
import andriod.bignerdranch.com.database.CrimeDbSchema.CrimeTable;

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        long time = getLong(getColumnIndex(CrimeTable.Cols.TIME));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));


        Crime crime = new Crime(UUID.fromString(uuidString));
                crime.setTitle(title);
                crime.setDate(new Date(date));
                crime.setTime(new Date(time));
                crime.setSuspect(suspect);
                crime.setSolved(isSolved != 0);

        return crime;

    }


}
