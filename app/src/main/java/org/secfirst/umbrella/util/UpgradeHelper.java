package org.secfirst.umbrella.util;

import android.content.res.Resources;
import android.util.Log;

import org.secfirst.umbrella.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Used as a aid in the migration process for, loading required SQL files as specified by a given version
 *
 * @author James Edward Morgan
 */
public class UpgradeHelper {

    protected static final Set<Integer> VERSION;
    static {
        VERSION = new LinkedHashSet<>();
    }

    private UpgradeHelper() {
    }

    /**
     * Add the given version to the list of available updates
     */
    public static final void addUpgrade(final int version) {
        VERSION.add(version);
    }

    /**
     * Get all available SQL Statements
     *
     * @param resources the {@link Resources} from the given {@link Context} which maybe using the helper class
     * @return A list of SQL statements which have been included in the
     */
    public static List<String> availableUpdates(final Resources resources) {
        final List<String> updates = new ArrayList<>();

        for (final Integer version : VERSION) {
            final String fileName = String.format("updates/migration-%s.sql", version);

            final String sqlStatements = loadAssetFile(resources, fileName);

            final String[] splitSql = sqlStatements.split("\\r?\\n");
            for (final String sql : splitSql) {
                if (isNotComment(sql)) {
                    updates.add(sql);
                }
            }
        }
        return updates;
    }

    /**
     * Load the given asset file, throws wrapped {@link RuntimeException} if not found
     *
     * @param fileName of the file to load, including asset directory path and sub path if required
     * @param resources the {@link Resources}, usually from a {@link Context}
     * @return the fully loaded file as a {@link String}
     */
    private static String loadAssetFile(final Resources resources, final String fileName) {
        try {
            final InputStream is = resources.getAssets().open(fileName);
            final byte[] buffer = new byte[is.available()];
            is.read(buffer);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(buffer);
            os.close();
            is.close();
            return os.toString();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A comment must consist of either '--' or '#'
     *
     * @return true if not found to be an SQL Comment
     */
    private static boolean isNotComment(final String sql) {
        return !sql.startsWith("--") && !sql.startsWith("#");
    }
}