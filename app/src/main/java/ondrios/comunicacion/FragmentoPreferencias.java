package ondrios.comunicacion;

import android.os.Bundle;
import android.preference.PreferenceFragment;
/**
 * Activity para mostrar las preferencias
 *
 * Esta actividad sirve para mostrar las preferencias.
 *
 */
public class FragmentoPreferencias extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
