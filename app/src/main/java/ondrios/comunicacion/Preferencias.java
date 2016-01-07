package ondrios.comunicacion;


import android.preference.PreferenceActivity;
import android.os.Bundle;

public class Preferencias extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new FragmentoPreferencias()).commit();
    }
}


