package sk.upjs.ics.android.presentr;

import android.content.Context;

import java.util.List;

public class PresenceLoader extends AbstractObjectLoader<List<String>> {
    private final PresenceDao presenceDao;

    public PresenceLoader(Context context) {
        super(context);
        this.presenceDao = new PresenceDao();
    }

    @Override
    public List<String> loadInBackground() {
        return this.presenceDao.loadUsers();
    }
}
