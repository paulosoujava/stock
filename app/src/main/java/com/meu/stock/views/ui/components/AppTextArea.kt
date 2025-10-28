// ... (imports e outros componentes)
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ...

/**
 * Um Composable para um campo de texto de múltiplas linhas (área de texto).
 * O texto digitado começará no topo do campo, e não no meio.
 *
 * @param value O valor atual do texto.
 * @param onValueChange Callback que é chamado quando o valor muda.
 * @param label O texto a ser exibido como rótulo do campo.
 * @param enabled Se o campo está habilitado para interação.
 * @param modifier Modificador para customização adicional.
 */
@Composable
fun AppTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 3,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 150.dp),
        enabled = enabled
    )
}
