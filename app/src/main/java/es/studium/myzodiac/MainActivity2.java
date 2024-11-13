package es.studium.myzodiac;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView textAnos = findViewById(R.id.textAnos);
        TextView textSigno = findViewById(R.id.textSigno);
        ImageView imageView = findViewById(R.id.imageView);

        int edad = getIntent().getIntExtra("edad", 0);
        String signo = getIntent().getStringExtra("signo");

        textAnos.setText(getString(R.string.TienesxAnos, edad));
        textSigno.setText(getString(R.string.EresSigno, signo));

        int signoImageResource = getSignoImageResource(signo);
        imageView.setImageResource(signoImageResource);
    }

    private int getSignoImageResource(String signo) {
        switch (signo) {
            case "Aries":
                return R.drawable.aries;
            case "Tauro":
                return R.drawable.taurus;
            // Añade casos para otros signos
            case "Geminis":
                return R.drawable.gemini;
            case "Cancer":
                return R.drawable.cancer;
            case "Leo":
                return R.drawable.leo;
            case "Virgo":
                return R.drawable.virgo;
            case "Libra":
                return R.drawable.libra;
            case "Escorpio":
                return R.drawable.scorpio;
            case "Sagitario":
                return R.drawable.sagittarius;
            case "Capricornio":
                return R.drawable.capricorn;
            case "Acurio":
                return R.drawable.aquarius;
            case "Piscis":
                return R.drawable.pisces;
            default:
                return R.drawable.ic_launcher_background; // imagen por defecto
        }
    }
}