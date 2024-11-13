package es.studium.myzodiac;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText editTextFechaN;
    private CalendarView calendarView;
    private Button button;
    private TextView textViewError; // TextView para los mensajes de error
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextFechaN = findViewById(R.id.editTextFechaN);
        calendarView = findViewById(R.id.calendarView);
        button = findViewById(R.id.button);
        textViewError = findViewById(R.id.textView); // Vinculamos el TextView para errores

        // Actualización del CalendarView desde el EditText al escribir la fecha
        editTextFechaN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textViewError.setTextColor(getResources().getColor(R.color.rojo));
                textViewError.setText(""); // Limpiar el mensaje de error al escribir

                String dateInput = s.toString().trim();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                dateFormat.setLenient(false);

                try {
                    // Intenta parsear la fecha ingresada
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(dateFormat.parse(dateInput));

                    // Si la fecha es válida, actualiza el CalendarView
                    calendarView.setDate(selectedDate.getTimeInMillis(), true, true);

                } catch (ParseException e) {
                    // Si no es una fecha válida, no hacemos nada (deja el mensaje de error)
                }
            }
        });

        // Actualización del EditText desde el CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            editTextFechaN.setText(selectedDate);
            textViewError.setTextColor(getResources().getColor(R.color.rojo));
            textViewError.setText("");// Limpiar el mensaje de error al seleccionar fecha desde el calendario

        });

        // Configuración del botón
        button.setOnClickListener(v -> {
            String dateInput = editTextFechaN.getText().toString().trim();
            textViewError.setText(""); // Limpiar el mensaje de error antes de cada validación


            StringBuilder errorBuilder = new StringBuilder();

            // Verificación del campo vacío antes de otras validaciones
            if (dateInput.isEmpty()) {
                textViewError.setTextColor(getResources().getColor(R.color.rojo));
                errorBuilder.append("Por favor, selecciona una fecha de nacimiento.\n");
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                dateFormat.setLenient(false);
                Calendar selectedDate = Calendar.getInstance();

                try {
                    // Validación de formato de fecha (dd/MM/yyyy)
                    if (!dateInput.matches("\\d{2}/\\d{2}/\\d{4}")) {
                        errorBuilder.append("Formato de fecha incorrecto. Usa dd/MM/yyyy.\n");
                    } else if (!esFechaValida(dateInput)) {
                        // Validación de número de días en el mes
                        errorBuilder.append("Has puesto más días de los que tiene este mes.\n");
                    } else {
                        // Intentar parsear la fecha solo si pasa las validaciones anteriores
                        selectedDate.setTime(dateFormat.parse(dateInput));

                        // Validar si el año es menor a 1900
                        if (selectedDate.get(Calendar.YEAR) < 1900) {
                            errorBuilder.append("El año no puede ser anterior a 1900.\n");
                        }

                        // Validación de fecha futura
                        int edad = calcularEdad(selectedDate);
                        if (edad == -1) {
                            errorBuilder.append("Todavía no has nacido.\n");
                        }
                    }

                    // Mostrar los errores acumulados en el TextView si hay alguno
                    if (errorBuilder.length() > 0) {
                        textViewError.setText(errorBuilder.toString());
                    } else {
                        // Si no hay errores, proceder a abrir MainActivity2
                        String signo = determinarSigno(selectedDate);
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        intent.putExtra("edad", calcularEdad(selectedDate));
                        intent.putExtra("signo", signo);
                        startActivity(intent);
                    }

                } catch (ParseException e) {
                    errorBuilder.append("Formato de fecha incorrecto. Usa dd/MM/yyyy.\n");
                    textViewError.setText(errorBuilder.toString());
                }
            }

            // Mostrar mensaje de error si el campo está vacío o si se acumuló algún error
            if (errorBuilder.length() > 0) {
                textViewError.setText(errorBuilder.toString());
            }
        });


    }

        private boolean esFechaValida(String dateInput) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setLenient(false);

        try {
            // parsear la fecha para verificar el formato correcto
            dateFormat.parse(dateInput);

            // DEfinir el día, mes y año para validar la cantidad de días en el mes
            int day = Integer.parseInt(dateInput.substring(0, 2));
            int month = Integer.parseInt(dateInput.substring(3, 5));
            int year = Integer.parseInt(dateInput.substring(6, 10));

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);

            int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            // Verificar si el día es mayor que el máximo del mes
            if (day > maxDayOfMonth) {
                return false; // Número de días incorrecto
            }

            // Si la fecha es válida y el número de días coincide, devolvemos true
            return true;

        } catch (ParseException | NumberFormatException e) {
            // Si falla el parseo, el formato es incorrecto
            return false;
        }
    }


    private int calcularEdad(Calendar fechaNacimiento) {
        Calendar fechaActual = Calendar.getInstance();
        if (fechaNacimiento.after(fechaActual)) {
            return -1;
        }

        int edad = fechaActual.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR);
        if (fechaActual.get(Calendar.MONTH) < fechaNacimiento.get(Calendar.MONTH) ||
                (fechaActual.get(Calendar.MONTH) == fechaNacimiento.get(Calendar.MONTH) &&
                        fechaActual.get(Calendar.DAY_OF_MONTH) < fechaNacimiento.get(Calendar.DAY_OF_MONTH))) {
            edad--;
        }
        return edad;
    }

    private String determinarSigno(Calendar fechaNacimiento) {
        int dia = fechaNacimiento.get(Calendar.DAY_OF_MONTH);
        int mes = fechaNacimiento.get(Calendar.MONTH) + 1;

        if ((mes == 3 && dia >= 21) || (mes == 4 && dia <= 19)) {
            return "Aries";
        } else if ((mes == 4 && dia >= 20) || (mes == 5 && dia <= 20)) {
            return "Tauro";
        } else if ((mes == 5 && dia >= 21) || (mes == 6 && dia <= 20)) {
            return "Geminis";
        } else if ((mes == 6 && dia >= 21) || (mes == 7 && dia <= 22)) {
            return "Cancer";
        } else if ((mes == 7 && dia >= 23) || (mes == 8 && dia <= 22)) {
            return "Leo";
        } else if ((mes == 8 && dia >= 23) || (mes == 9 && dia <= 22)) {
            return "Virgo";
        } else if ((mes == 9 && dia >= 23) || (mes == 10 && dia <= 22)) {
            return "Libra";
        } else if ((mes == 10 && dia >= 23) || (mes == 11 && dia <= 21)) {
            return "Escorpio";
        } else if ((mes == 11 && dia >= 22) || (mes == 12 && dia <= 21)) {
            return "Sagitario";
        } else if ((mes == 12 && dia >= 22) || (mes == 1 && dia <= 19)) {
            return "Capricornio";
        } else if ((mes == 1 && dia >= 20) || (mes == 2 && dia <= 18)) {
            return "Acuario";
        } else if ((mes == 2 && dia >= 19) || (mes == 3 && dia <= 20)) {
            return "Piscis";
        }
        return "Signo desconocido";
    }
}
