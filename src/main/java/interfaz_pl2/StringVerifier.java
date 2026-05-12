package interfaz_pl2;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class StringVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        String texto = ((JTextField) input).getText().trim();
        
        // Regla 1: Que no esté vacío
        if (texto.isEmpty()) {
            return false; 
        }
        
        // Regla 2: Que solo contenga letras (opcional, usando Regex)
        // ^[a-zA-ZáéíóúÁÉÍÓÚñÑ ] permite letras y espacios
        if (!texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            return false;
        }

        return true; // Si pasa las reglas, es válido
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        boolean valido = verify(input);
        if (!valido) {
            // Opcional: Avisar al usuario por qué no puede salir del campo
            JOptionPane.showMessageDialog(null, "Por favor, introduce un nombre válido (solo letras).", 
                                          "Dato no válido", JOptionPane.WARNING_MESSAGE);
        }
        return valido;
    }
}