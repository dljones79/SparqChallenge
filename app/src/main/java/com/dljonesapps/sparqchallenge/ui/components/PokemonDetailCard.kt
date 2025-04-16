package com.dljonesapps.sparqchallenge.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInQuint
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.dljonesapps.sparqchallenge.R
import com.dljonesapps.sparqchallenge.data.model.PokemonDetail

@Composable
fun PokemonDetailCard(
    pokemonDetail: PokemonDetail,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Semi-transparent scrim that covers the entire screen
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = Modifier.zIndex(10f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectTapGestures { onDismiss() }
                }
        )
    }
    
    // Animated card content
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(500, easing = EaseOutQuint)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(500, easing = EaseInQuint)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(11f)
    ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main content surface
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .pointerInput(Unit) {
                            // Prevent clicks from reaching the scrim
                            detectTapGestures { }
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    // Scrollable content
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Pokemon image
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                    ) {
                        SubcomposeAsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pokemonDetail.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "${pokemonDetail.name} image",
                            contentScale = ContentScale.Fit,
                            loading = { CircularProgressIndicator() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Pokemon name and number
                    Text(
                        text = pokemonDetail.name.capitalize(Locale.current),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "#${pokemonDetail.id.toString().padStart(3, '0')}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Types
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        pokemonDetail.types.forEach { type ->
                            TypeChip(type = type)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Physical attributes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PhysicalAttribute(
                            label = stringResource(R.string.label_height),
                            value = "${pokemonDetail.height / 10.0} m"
                        )
                        
                        PhysicalAttribute(
                            label = stringResource(R.string.label_weight),
                            value = "${pokemonDetail.weight / 10.0} kg"
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Stats
                    Text(
                        text = stringResource(R.string.label_base_stats),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        pokemonDetail.stats.forEach { (statName, value) ->
                            StatBar(
                                statName = statName.replace("-", " ").capitalize(Locale.current),
                                value = value
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Abilities
                    Text(
                        text = stringResource(R.string.label_abilities),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        pokemonDetail.abilities.forEach { ability ->
                            Text(
                                text = "• ${ability.replace("-", " ").capitalize(Locale.current)}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
                
                // Fixed close button that doesn't scroll
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .zIndex(12f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

// Obviously, I wouldn't do this in a production app. I would create some type of mapper
// and map the ui state from the response, and in turn map the correct color.
// Just did it like this to save some time.
@Composable
fun TypeChip(type: String) {
    val typeColor = when (type.lowercase()) {
        "normal" -> Color(0xFFA8A77A)
        "fire" -> Color(0xFFEE8130)
        "water" -> Color(0xFF6390F0)
        "electric" -> Color(0xFFF7D02C)
        "grass" -> Color(0xFF7AC74C)
        "ice" -> Color(0xFF96D9D6)
        "fighting" -> Color(0xFFC22E28)
        "poison" -> Color(0xFFA33EA1)
        "ground" -> Color(0xFFE2BF65)
        "flying" -> Color(0xFFA98FF3)
        "psychic" -> Color(0xFFF95587)
        "bug" -> Color(0xFFA6B91A)
        "rock" -> Color(0xFFB6A136)
        "ghost" -> Color(0xFF735797)
        "dragon" -> Color(0xFF6F35FC)
        "dark" -> Color(0xFF705746)
        "steel" -> Color(0xFFB7B7CE)
        "fairy" -> Color(0xFFD685AD)
        else -> MaterialTheme.colorScheme.primary
    }
    
    Surface(
        color = typeColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = type.capitalize(Locale.current),
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun PhysicalAttribute(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StatBar(statName: String, value: Int) {
    val maxStatValue = 255 // Maximum possible stat value in Pokémon
    val barProgress = value.toFloat() / maxStatValue
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statName,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(100.dp)
        )
        
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
        
        Spacer(modifier = Modifier.width(8.dp))

        LinearProgressIndicator(
            progress = { barProgress },
            modifier = Modifier
                .height(8.dp)
                .weight(1f)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                value < 50 -> Color(0xFFFF5959) // Red for low stats
                value < 90 -> Color(0xFFFFDD57) // Yellow for medium stats
                else -> Color(0xFF5FD75F) // Green for high stats
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
