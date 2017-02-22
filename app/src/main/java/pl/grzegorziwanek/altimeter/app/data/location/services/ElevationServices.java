package pl.grzegorziwanek.altimeter.app.data.location.services;

import android.os.AsyncTask;

/**
 * Created by Grzegorz Iwanek on 19.02.2017.
 */

public class ElevationServices extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}

/**
 * US service (WORKS ONLY FOR US TERRITORY!!!) USGS GOV DATA
 * http://ned.usgs.gov/epqs/pqs.php?x=%2$f&y=%1$f&units=Meters&output=json
 * where x = longitude e.g -90.23, y = latitude e.g. 40.23332
 * units = Meters / Feet , output = json / xml
 * e.g. query:
 * USA, longitude -92.323, latitude 32.332
 * units Meters, output format json
 * http://ned.usgs.gov/epqs/pqs.php?x=-92.323&y=32.332&units=Meters&output=json
 */

//return (C0574d) this.f1643b.execute(new HttpGet(String.format(locale, this.f1642a.getResources().getString(R.string.elevation_googleapi_url), new Object[]{Double.valueOf(this.f1644c.getLatitude()), Double.valueOf(this.f1644c.getLongitude())})), new C0579b(this, c0584i));
//        return (C0574d) this.f1643b.execute(new HttpGet(String.format(locale, this.f1642a.getResources().getString(R.string.elevation_geonames_url), new Object[]{string, Double.valueOf(this.f1644c.getLatitude()), Double.valueOf(this.f1644c.getLongitude())})), new C0578a(this, c0584i));
//        return (C0574d) this.f1643b.execute(new HttpGet(String.format(locale, this.f1642a.getResources().getString(R.string.elevation_openmap_url), new Object[]{Double.valueOf(this.f1644c.getLatitude()), Double.valueOf(this.f1644c.getLongitude())})), this.f1649h)

