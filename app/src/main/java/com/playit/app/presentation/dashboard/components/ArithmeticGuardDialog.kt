package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.manager.ArithmeticGateManager
import com.playit.app.domain.model.ArithmeticProblem
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DestructiveRed
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary

@Composable
fun ArithmeticGuardDialog(
    gateManager: ArithmeticGateManager = remember { ArithmeticGateManager() },
    onPass: () -> Unit,
    onDismiss: () -> Unit
) {
    var problem by remember { mutableStateOf(gateManager.generateProblem()) }
    var answerInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🔒 Parent Access Verification",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Please solve this arithmetic problem to open the Parent Dashboard:",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = problem.displayExpression,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = FriendlyPurple
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = answerInput,
                    onValueChange = {
                        answerInput = it
                        isError = false
                    },
                    label = { Text("Your Answer") },
                    singleLine = true,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Incorrect answer. Please try again.",
                        color = DestructiveRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (gateManager.validateAnswer(problem, answerInput)) {
                        onPass()
                    } else {
                        isError = true
                        problem = gateManager.generateProblem()
                        answerInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FriendlyPurple)
            ) {
                Text("Enter Dashboard", color = CreamWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
