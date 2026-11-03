package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CartItem
import com.example.model.getDrawableResId
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.CrimsonSale
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.StatusSuccess
import com.example.viewmodel.AthenaScreen
import com.example.viewmodel.AthenaViewModel
import com.example.viewmodel.PaymentType
import java.util.Locale

@Composable
fun CartAndCheckoutScreen(
    viewModel: AthenaViewModel,
    onNavigateToTracking: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cart by viewModel.cart.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val step by viewModel.checkoutStep.collectAsState()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()
    val lastCompletedOrder by viewModel.lastCompletedOrder.collectAsState()

    val customerName by viewModel.customerName.collectAsState()
    val deliveryAddress by viewModel.deliveryAddress.collectAsState()
    val deliveryCity by viewModel.deliveryCity.collectAsState()
    val deliveryNotes by viewModel.deliveryNotes.collectAsState()

    val selectedPaymentType by viewModel.selectedPaymentType.collectAsState()
    val cardNumber by viewModel.cardNumber.collectAsState()
    val cardHolder by viewModel.cardHolder.collectAsState()
    val cardExpiry by viewModel.cardExpiry.collectAsState()
    val cardCvv by viewModel.cardCvv.collectAsState()

    val subtotal = cart.sumOf { it.totalPrice }
    val discountPercent = when (appliedCoupon) {
        "ATHENA10" -> 0.10
        "GOLDEN20" -> 0.20
        "DIOSA15" -> 0.15
        else -> 0.0
    }
    val discount = subtotal * discountPercent
    val shipping = 0.0 // Free VIP shipping
    val total = (subtotal - discount + shipping).coerceAtLeast(0.0)

    var couponInput by remember { mutableStateOf("") }
    var couponError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Progress Stepper Indicator
        if (step in 1..3 && cart.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                StepIndicator(stepNumber = 1, title = "Bolsa", isActive = step == 1, isCompleted = step > 1)
                HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = AthenaGold.copy(alpha = 0.5f))
                StepIndicator(stepNumber = 2, title = "Entrega", isActive = step == 2, isCompleted = step > 2)
                HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = AthenaGold.copy(alpha = 0.5f))
                StepIndicator(stepNumber = 3, title = "Pago Seguro", isActive = step == 3, isCompleted = step > 3)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // STEP 1: CART ITEMS
        if (step == 1) {
            if (cart.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu bolsa de compras está vacía",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explora el catálogo de Athena Style y descubre prendas de alta costura.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AthenaScreen.CATALOG) },
                        colors = ButtonDefaults.buttonColors(containerColor = AthenaGold, contentColor = ObsidianNight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("explore_catalog_from_empty_cart_btn")
                    ) {
                        Text("Ir al Catálogo", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    text = "Prendas Seleccionadas (${cart.sumOf { it.quantity }})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Cart items list
                cart.forEach { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { delta -> viewModel.updateCartQuantity(item.id, delta) },
                        onRemove = { viewModel.removeFromCart(item.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Coupon code application box
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Discount, contentDescription = null, tint = AthenaGoldDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cupón de Descuento Athena", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = {
                                    couponInput = it
                                    couponError = false
                                },
                                placeholder = { Text("Ej: GOLDEN20 o ATHENA10", fontSize = 12.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("coupon_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val success = viewModel.applyCoupon(couponInput)
                                    couponError = !success
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianNight, contentColor = AthenaGold),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("apply_coupon_btn")
                            ) {
                                Text("Aplicar")
                            }
                        }
                        if (appliedCoupon != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "✓ Cupón $appliedCoupon activo (${(discountPercent * 100).toInt()}% de descuento)",
                                    fontSize = 11.sp,
                                    color = StatusSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Quitar",
                                    fontSize = 11.sp,
                                    color = CrimsonSale,
                                    modifier = Modifier.clickable { viewModel.removeCoupon() }
                                )
                            }
                        }
                        if (couponError) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Cupón no válido. Prueba con 'ATHENA10' o 'GOLDEN20'", fontSize = 11.sp, color = CrimsonSale)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Summary Breakdown
                OrderSummaryCard(subtotal = subtotal, discount = discount, shipping = shipping, total = total)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.startCheckout() },
                    colors = ButtonDefaults.buttonColors(containerColor = AthenaGold, contentColor = ObsidianNight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("proceed_to_address_btn")
                ) {
                    Text("Continuar a Entrega", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // STEP 2: ADDRESS & DELIVERY
        if (step == 2) {
            Text(
                text = "Datos de Entrega Express",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tu pedido será despachado en caja de lujo sellada con lacre de oro.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = customerName,
                onValueChange = { viewModel.customerName.value = it },
                label = { Text("Nombre y Apellidos del Destinatario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("address_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = deliveryAddress,
                onValueChange = { viewModel.deliveryAddress.value = it },
                label = { Text("Dirección de Residencia / Oficina") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("address_street_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = deliveryCity,
                onValueChange = { viewModel.deliveryCity.value = it },
                label = { Text("Ciudad / Municipio") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("address_city_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = deliveryNotes,
                onValueChange = { viewModel.deliveryNotes.value = it },
                label = { Text("Notas de entrega para el mensajero") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth().testTag("address_notes_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery method card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AthenaGold)
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = ObsidianNight, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Envío VIP Express Athena", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Entrega garantizada en menos de 45 minutos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("GRATIS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StatusSuccess)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { viewModel.checkoutStep.value = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Atrás")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { viewModel.proceedToPayment() },
                    colors = ButtonDefaults.buttonColors(containerColor = AthenaGold, contentColor = ObsidianNight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.5f).height(50.dp).testTag("proceed_to_payment_btn")
                ) {
                    Text("Ir al Pago Seguro", fontWeight = FontWeight.Bold)
                }
            }
        }

        // STEP 3: PAYMENT GATEWAY
        if (step == 3) {
            Text(
                text = "Pasarela de Pagos Segura",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cifrado bancario AES-256 bit verificado", fontSize = 11.sp, color = StatusSuccess)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment method selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethodOption(
                    title = "Tarjeta de Crédito / Débito",
                    subtitle = "Visa, Mastercard, American Express",
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedPaymentType == PaymentType.CREDIT_CARD,
                    onClick = { viewModel.selectedPaymentType.value = PaymentType.CREDIT_CARD }
                )
                PaymentMethodOption(
                    title = "Billeteras Digitales",
                    subtitle = "Nequi, Daviplata, Google Pay, Apple Pay",
                    icon = Icons.Default.Smartphone,
                    isSelected = selectedPaymentType == PaymentType.DIGITAL_WALLET,
                    onClick = { viewModel.selectedPaymentType.value = PaymentType.DIGITAL_WALLET }
                )
                PaymentMethodOption(
                    title = "PSE / Transferencia Bancaria",
                    subtitle = "Débito bancario desde cualquier cuenta en Colombia",
                    icon = Icons.Default.AccountBalance,
                    isSelected = selectedPaymentType == PaymentType.PSE,
                    onClick = { viewModel.selectedPaymentType.value = PaymentType.PSE }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Luxury Card Visualizer (when Credit Card is chosen)
            if (selectedPaymentType == PaymentType.CREDIT_CARD) {
                LuxuryCreditCardView(
                    cardNumber = cardNumber,
                    cardHolder = cardHolder,
                    cardExpiry = cardExpiry,
                    cardCvv = cardCvv
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Card input fields
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 19) viewModel.cardNumber.value = it },
                    label = { Text("Número de Tarjeta") },
                    leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null, tint = AthenaGoldDark) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("card_number_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = { viewModel.cardHolder.value = it.uppercase() },
                    label = { Text("Titular de la Tarjeta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("card_holder_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = cardExpiry,
                        onValueChange = { if (it.length <= 5) viewModel.cardExpiry.value = it },
                        label = { Text("Expiración (MM/YY)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("card_expiry_input")
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = cardCvv,
                        onValueChange = { if (it.length <= 4) viewModel.cardCvv.value = it },
                        label = { Text("CVV / CVC") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("card_cvv_input")
                    )
                }
            } else if (selectedPaymentType == PaymentType.DIGITAL_WALLET) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pago Instantáneo por Billetera", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Al presionar Pagar, se conectará de manera segura con tu cuenta de Nequi o Google Pay para autorizar el cobro de $${String.format(Locale.US, "%.2f", total)}.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Transferencia Segura PSE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Se generará la transacción protegida con ACH Colombia directamente a tu banco.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary box
            OrderSummaryCard(subtotal = subtotal, discount = discount, shipping = shipping, total = total)

            Spacer(modifier = Modifier.height(20.dp))

            // Pay Button with spinner
            Button(
                onClick = { viewModel.processPayment() },
                enabled = !isProcessingPayment,
                colors = ButtonDefaults.buttonColors(containerColor = AthenaGold, contentColor = ObsidianNight),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("confirm_and_pay_btn")
            ) {
                if (isProcessingPayment) {
                    CircularProgressIndicator(
                        color = ObsidianNight,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Procesando con Banco...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pagar $${String.format(Locale.US, "%.2f", total)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // STEP 4: PAYMENT SUCCESS & INSTANT TRACKING ENTRY
        if (step == 4 && lastCompletedOrder != null) {
            val order = lastCompletedOrder!!
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .testTag("checkout_success_screen")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(StatusSuccess.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = StatusSuccess,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Pago Procesado con Éxito!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tu compra en Athena Style ha sido confirmada.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Order details receipt card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Orden Athena:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("#${order.orderId}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AthenaGoldDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Código de Rastreo:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(order.trackingNumber, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Monto Total:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${String.format(Locale.US, "%.2f", order.total)}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Dirección de Entrega:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(order.deliveryAddress.take(22) + "...", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.selectActiveOrder(order.orderId)
                        onNavigateToTracking(order.orderId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AthenaGold, contentColor = ObsidianNight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("track_live_order_btn")
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Seguimiento en Tiempo Real", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.resetCheckout()
                        viewModel.navigateTo(AthenaScreen.CATALOG)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seguir Comprando")
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun StepIndicator(stepNumber: Int, title: String, isActive: Boolean, isCompleted: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) StatusSuccess else if (isActive) AthenaGold else Color.LightGray.copy(alpha = 0.4f)
                )
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = stepNumber.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) ObsidianNight else Color.DarkGray
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) AthenaGoldDark else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Mini product photo
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEDE8E0))
            ) {
                Image(
                    painter = painterResource(id = item.product.getDrawableResId()),
                    contentDescription = item.product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Talla: ${item.selectedSize}  •  Color: ${item.selectedColor.name}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format(Locale.US, "%.2f", item.product.price)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AthenaGoldDark
                )
            }

            // Quantity controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                IconButton(onClick = { onQuantityChange(-1) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(14.dp))
                }
                Text(
                    text = item.quantity.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                IconButton(onClick = { onQuantityChange(1) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Más", modifier = Modifier.size(14.dp))
                }
            }

            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = CrimsonSale, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(subtotal: Double, discount: Double, shipping: Double, total: Double) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Resumen del Pedido", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Subtotal de prendas", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${String.format(Locale.US, "%.2f", subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            if (discount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Descuento Athena", fontSize = 12.sp, color = CrimsonSale)
                    Text("-$${String.format(Locale.US, "%.2f", discount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonSale)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Envío VIP Express", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("GRATIS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusSuccess)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("$${String.format(Locale.US, "%.2f", total)}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = AthenaGoldDark)
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AthenaGold) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = AthenaGold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = icon, contentDescription = null, tint = AthenaGoldDark, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LuxuryCreditCardView(
    cardNumber: String,
    cardHolder: String,
    cardExpiry: String,
    cardCvv: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF241F35),
                            Color(0xFF382F52),
                            Color(0xFF1B1629)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // Card Top: Brand and Chip
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ATHENA GOLD PLATINUM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = AthenaGold
                    )
                    Icon(
                        imageVector = Icons.Default.SimCard,
                        contentDescription = "Chip",
                        tint = AthenaGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Card Number
                Text(
                    text = cardNumber.ifBlank { "•••• •••• •••• ••••" },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color.White
                )

                // Card Bottom: Holder and Expiry
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = "CARD HOLDER", fontSize = 9.sp, color = Color.LightGray)
                        Text(
                            text = cardHolder.ifBlank { "CAMILA RESTREPO" },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "EXPIRES", fontSize = 9.sp, color = Color.LightGray)
                        Text(
                            text = cardExpiry.ifBlank { "MM/YY" },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
